package com.zhoujg77.eas.server;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Base64;
import android.widget.Toast;

import com.zhoujg77.eas.bean.ReceiptBean;
import com.zhoujg77.eas.db.DbAdapter;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by zhoujiangang on 15/11/29.
 */
public class UploadService extends Service {


    public static final String NAME_SPACE = "http://webservice.eas.zhixingkeji.com";
    public static final String WDSL_LINK_PREFIX = "http://";
    public static final String WDSL_LINK_POSTFIX = "/services/ReceiptService?wsdl";
    private static final String WDSL_LINK = "http://192.168.1.104/EAS/services/ReceiptService?wsdl";
    public static final String METHOD_TEST_NAME = "testHello";
    private String resultStr;
    private static boolean isUpLoding = false;
    DbAdapter dbHandle = null;

    public String getWdslLink(DbAdapter dh){

        String url = dh.getConfigUrl();
        String resultStr = WDSL_LINK_PREFIX + url + WDSL_LINK_POSTFIX;

        return resultStr;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("MyService.onCreate");
    }

    @SuppressWarnings("deprecation")
    public void DisplayTitle(Cursor c) {
        Toast.makeText(
                this,
                "id: " + c.getString(0) + "\n" + "qcode_id: " + c.getString(1)
                        + "\n" + "state: " + c.getString(2) + "\n"
                        + "location: " + c.getString(3) + "\n" + "date: "
                        + c.getString(4) + "\n" + "note: " + c.getString(5)
                        + "\n" + "table_uploaded: " + c.getString(6) + "\n"
                        + "image_uploaded: " + c.getString(7) + "\n",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("MyService.onDestroy");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("MyService.onStartCommand.intent= " + intent);
        System.out.println("MyService.onStartCommand.flags= " + flags);
        System.out.println("MyService.onStartCommand.startId= " + startId);

        doUpload();
        return super.onStartCommand(intent, flags, startId);
    }

    private void doUpload() {
        // TODO Auto-generated method stub
		/* check the network */
        if (!preCheck()) {
            Intent intent2 = new Intent(
                    "cn.wislight.logisticassistant.broadcast");
            intent2.putExtra("cmd", "error");
            intent2.putExtra("errorCode", 100);
            sendBroadcast(intent2);
            return;

        }

        if (isUpLoding) {
            Intent intent2 = new Intent(
                    "cn.wislight.logisticassistant.broadcast");
            intent2.putExtra("cmd", "isuploading");
            sendBroadcast(intent2);
            return;
        }
        upLoad();
    }

    private void upLoad() {
        // TODO Auto-generated method stub
        isUpLoding = true;

        Thread thr = new Thread() {
            @SuppressLint("UseValueOf")
            Integer step = 0;

            public void run() {
                int error_code = 0;

                dbHandle = new DbAdapter(getApplicationContext());
                dbHandle.open();


                int totalReceipt = dbHandle.getUnUploadedRecordsCount();

                if (totalReceipt == 0){
                    return;
                }else{
                    step = 100 / (totalReceipt * 2);
                    if (step == 0){
                        step = 1;
                    }

					/* update progress */
                    Intent intent2 = new Intent(
                            "cn.wislight.logisticassistant.broadcast");
                    intent2.putExtra("cmd", "progress");
                    intent2.putExtra("step", "init");
                    sendBroadcast(intent2);
                }

                Cursor cursor = dbHandle.getUnUploadedRecords();
                if (cursor.moveToFirst()) {
                    do {
                        if ((error_code = uploadReceipt(cursor, dbHandle)) != 0) {

							/* update ui */
                            Intent intent2 = new Intent(
                                    "cn.wislight.logisticassistant.broadcast");
                            intent2.putExtra("errorCode", error_code);
                            intent2.putExtra("cmd", "error");
                            sendBroadcast(intent2);
                            break;
                        } else {
							/* update ui */
                            Intent intent2 = new Intent(
                                    "cn.wislight.logisticassistant.broadcast");
                            intent2.putExtra("cmd", "updateUI");
                            sendBroadcast(intent2);

                        }
                    } while (cursor.moveToNext());

					/* update ui */
                    if (error_code == 0) {
                        Intent intent2 = new Intent(
                                "cn.wislight.logisticassistant.broadcast");
                        intent2.putExtra("cmd", "finish");
                        sendBroadcast(intent2);
                    }
                }

                isUpLoding = false;
                dbHandle.close();

            }

            private int uploadReceipt(Cursor cursor, DbAdapter dh) {
                // TODO Auto-generated method stub
                int err = 0;
                err = uploadImg(cursor, dh);
                if (err != 0){
                    return err;
                }else{
                    Intent intent2 = new Intent(
                            "cn.wislight.logisticassistant.broadcast");
                    intent2.putExtra("cmd", "progress");
                    intent2.putExtra("step", step.toString());
                    sendBroadcast(intent2);
                }


                err = uploadReceiptList(cursor, dh);
                if (err == 0){
                    Intent intent2 = new Intent(
                            "cn.wislight.logisticassistant.broadcast");
                    intent2.putExtra("cmd", "progress");
                    intent2.putExtra("step", step.toString());
                    sendBroadcast(intent2);
                }

                return err;
            }

            @SuppressLint("UseValueOf")
            private int uploadReceiptList(Cursor cursor, DbAdapter dh) {
                int err = 0;
                if ("0".equals(cursor.getString(6))) {

                    String method = "addReceipt";
                    SoapObject request = new SoapObject(NAME_SPACE, method);

                    ReceiptBean receipt = new ReceiptBean();
                    receipt.setReceiptId(1514);
                    receipt.setFilePath(cursor.getString(8));
                    receipt.setLocation("xian");
                    receipt.setPhone(dbHandle.getOwnerInfoPhone());
                    receipt.setQcodeId(cursor.getString(1));
                    receipt.setDate(cursor.getString(4));
                    receipt.setNote("test web service");
                    receipt.setOwner(dbHandle.getOwnerInfoName());
                    receipt.setMacAddress(dbHandle.getOwnerInfoMac());
                    receipt.setState(new Integer(cursor.getString(2)));
                    PropertyInfo pi = new PropertyInfo();
                    pi.setName("receipt");
                    pi.setValue(receipt);
                    pi.setType(receipt.getClass());
                    request.addProperty(pi);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER10);
                    envelope.bodyOut = request;
                    envelope.setOutputSoapObject(request);
                    envelope.addMapping(NAME_SPACE, "ReceiptBean",
                            ReceiptBean.class);//
                    HttpTransportSE ht = new HttpTransportSE(getWdslLink(dh));
                    try {
                        ht.call("", envelope);
                        String ret = String.valueOf(envelope.getResponse());
                        if ("false".equals(ret)) {
                            err = 1; // network error
                        } else {
                            dbHandle.setRecordsTableUploaded(
                                    new Integer(cursor.getString(0)), true);
                        }
                    } catch (HttpResponseException e) {
                        // TODO Auto-generated catch block
                        err = 2; // http error
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        err = 3; // IO error
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        // TODO Auto-generated catch block
                        err = 4; // xml error
                        e.printStackTrace();
                    } finally {

                    }
                }
                return err;
            }

            private int uploadImg(Cursor cursor, DbAdapter dh) {
                int err = 0;
                if ("0".equals(cursor.getString(7))) {
					/* upload Image */
                    FileInputStream fis;
                    try {
                        fis = new FileInputStream("sdcard/eas/img/" + cursor.getString(8));

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte buffer[] = new byte[fis.available()];
                        int count = 0;
                        while ((count = fis.read(buffer)) > 0) {
                            baos.write(buffer, 0, count);
                        }
                        String uploadBuffer = Base64.encodeToString(
                                baos.toByteArray(), Base64.DEFAULT);
                        String method = "UploadImageWithString";

                        SoapObject request = new SoapObject(NAME_SPACE, method);
                        request.addProperty("name", cursor.getString(8));
                        request.addProperty("image", uploadBuffer);

                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                                SoapEnvelope.VER11);
                        // SoapEnvelope envelope1 = new
                        // SoapEnvelope(SoapEnvelope.VER11);
                        envelope.bodyOut = request;
                        envelope.setOutputSoapObject(request);

                        HttpTransportSE ht = new HttpTransportSE(getWdslLink(dh));
                        ht.call("", envelope);
                        String ret = String.valueOf(envelope.getResponse());
                        if ("false".equals(ret)) {
                            err = 1; // network error
                        } else {
                            dbHandle.setImageUploaded(
                                    new Integer(cursor.getString(0)), true);
                        }

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        err = 5;
                        e.printStackTrace();
                    } catch (SoapFault e) {
                        // TODO Auto-generated catch block
                        err = 1;
                        e.printStackTrace();
                    } catch (HttpResponseException e) {
                        err = 2; // http error
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        err = 3; // IO error
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        // TODO Auto-generated catch block
                        err = 4; // xml error
                        e.printStackTrace();
                    } finally {

                    }
                }
                return err;
            }
        };
        thr.start();
    }

    private boolean preCheck() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }



    public void setResultStr(String resultStr) {
        this.resultStr = resultStr;
    }


}

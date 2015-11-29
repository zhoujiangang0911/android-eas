package com.zhoujg77.eas;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.zhoujg77.eas.db.DbAdapter;
import com.zhoujg77.eas.server.UploadService;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoujiangang on 15/11/29.
 */
public class ConfigActivity extends AppCompatActivity {
    private EditText URL = null;
    private Button btSave = null;
    private Button btTest = null;
    private List<String> list = new ArrayList<String>();
    private Spinner spinnerCompay;
    private ArrayAdapter<String> adapter;
    private RadioGroup group;
    private EditText etName = null;
    private EditText etPhone = null;


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.activity_config);
        etName = (EditText)findViewById(R.id.editText2);
        etPhone= (EditText)findViewById(R.id.editText3);
        URL = (EditText) findViewById(R.id.editText1);
        final DbAdapter dbHandle = new DbAdapter(this);
        dbHandle.open();
        URL.setText(dbHandle.getConfigUrl());
        etName.setText(dbHandle.getOwnerInfoName());
        etPhone.setText(dbHandle.getOwnerInfoPhone());
        dbHandle.close();

        btSave = (Button) findViewById(R.id.btn1);
        btTest = (Button) findViewById(R.id.btn2);

        btSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String strUrl = URL.getText().toString();
                String strName = etName.getText().toString();
                String strPhone = etPhone.getText().toString();
                String message = null;
                if (null == strUrl || "".equals(strUrl)){
                    message = getString(R.string.error_url);
                    URL.setFocusable(true);
                    URL.setFocusableInTouchMode(true);
                    URL.requestFocus();
                    URL.requestFocusFromTouch();
                }else if (null == strName || "".equals(strName)){
                    message = getString(R.string.error_name);
                    etName.setFocusable(true);
                    etName.setFocusableInTouchMode(true);
                    etName.requestFocus();
                    etName.requestFocusFromTouch();
                }else if (null == strPhone || "".equals(strPhone)){
                    message = getString(R.string.error_phone);
                    etPhone.setFocusable(true);
                    etPhone.setFocusableInTouchMode(true);
                    etPhone.requestFocus();
                    etPhone.requestFocusFromTouch();
                }

                if (null != message){
                    Toast.makeText(ConfigActivity.this, message,
                            Toast.LENGTH_SHORT).show();
                }else{
                    dbHandle.open();
                    dbHandle.setConfigUrl(URL.getText().toString());
                    dbHandle.setOwnerInfoName(etName.getText().toString());
                    dbHandle.setOwnerInfoPhone(etPhone.getText().toString());
                    dbHandle.close();
                    Toast.makeText(ConfigActivity.this, getString(R.string.saved),
                            Toast.LENGTH_SHORT).show();

                    finish();
                }
            }
        });

        btTest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                testNetWork();
            }
        });

		/* Spinner init */
        Resources res = getResources();
        String[] company = res.getStringArray(R.array.company);
        for (int i = 0; i < company.length; i++) {
            list.add(company[i]);
        }

        spinnerCompay = (Spinner) findViewById(R.id.spinner_company);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCompay.setAdapter(adapter);
        spinnerCompay
                .setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        // TODO Auto-generated method stub

                        dbHandle.open();
                        Cursor cursor = dbHandle.getCompanys();


                        if (cursor.moveToFirst()){
                            do {
                                if ( adapter.getItem(arg2).equals(cursor.getString(0))){
                                    break;
                                }
                            }while(cursor.moveToNext());
                        }

                        int radioButtonId = group.getCheckedRadioButtonId();
                        RadioButton rb = (RadioButton) ConfigActivity.this
                                .findViewById(radioButtonId);
                        String rbText = (String) rb.getText();
                        if ("LAN".equals(rbText)){
                            URL.setText(cursor.getString(2));
                        }else{
                            URL.setText(cursor.getString(1));
                        }
                        dbHandle.close();
                        arg0.setVisibility(View.VISIBLE);
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                        arg0.setVisibility(View.VISIBLE);
                    }
                });
        spinnerCompay.setOnTouchListener(new Spinner.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                /**
                 *
                 */
                return false;
            }
        });
        spinnerCompay
                .setOnFocusChangeListener(new Spinner.OnFocusChangeListener() {
                    public void onFocusChange(View v, boolean hasFocus) {
                        // TODO Auto-generated method stub

                    }
                });

		/*
		 * radio init *.
		 */
        group = (RadioGroup) this.findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                // TODO Auto-generated method stub
                int radioButtonId = arg0.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) ConfigActivity.this
                        .findViewById(radioButtonId);
                // �����ı����ݣ��Է���ѡ����
                rb.getText();
                dbHandle.open();
                Cursor cursor = dbHandle.getCompanys();


                int id = (int) spinnerCompay.getSelectedItemId();
                String companyName = adapter.getItem(id);
                if (cursor.moveToFirst()){
                    do {
                        if (companyName.equals(cursor.getString(0))){
                            break;
                        }
                    }while(cursor.moveToNext());
                }

                String rbText = (String) rb.getText();
                if ("LAN".equals(rbText)){
                    URL.setText(cursor.getString(2));
                }else{
                    URL.setText(cursor.getString(1));
                }
                dbHandle.close();
            }
        });
    }




    protected void testNetWork() {
        // TODO Auto-generated method stub

        Thread thr = new Thread() {
            public void run() {
                boolean bResult = false;
                try {
                    String name = "aaabbbccc";

                    // Soap Object �� SoapSerializationEnvelope����Ҫ��ɲ���

                    SoapObject request = new SoapObject(
                            UploadService.NAME_SPACE,
                            UploadService.METHOD_TEST_NAME);
                    request.addProperty("name", name);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);
                    envelope.bodyOut = request;
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE ht = new HttpTransportSE(getWdslLink());
                    ht.call("", envelope);

                    String ret = String.valueOf(envelope.getResponse());

                    if ("hello,aaabbbccc".equals(ret)) {
                        bResult = true;
                    }

                    System.out.println("wangting resultStr = " + ret);

                } catch (SoapFault e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("wangting IOException");
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(
                        "cn.wislight.logisticassistant.broadcast");
                intent.putExtra("cmd", "testResult");
                intent.putExtra("result", bResult ? "1" : "0");
                sendBroadcast(intent);

            }
        };

        thr.start();
    }


    public String getWdslLink() {

        String url = URL.getText().toString();
        String resultStr = UploadService.WDSL_LINK_PREFIX + url
                + UploadService.WDSL_LINK_POSTFIX;

        return resultStr;
    }

}

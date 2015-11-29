package com.zhoujg77.eas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.zhoujg77.eas.bean.UpdateRecordsBean;
import com.zhoujg77.eas.db.DbAdapter;
import com.zhoujg77.eas.server.UploadService;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {



    private Button button1 = null;
    private Button button2 = null;
    private Button button3 = null;
    private ProgressBar progress= null;
    ListView receiptList = null;
    final int TAKE_PICTURE = 1;
    private final static int SCANNIN_GREQUEST_CODE = 10;


    private static String localEasDir = "eas";
    private static String localImageDir = "img";
    private static String localTempImgFileName = "temp.bmp";
    private static String sdcard = "sdcard/";
    protected static String localWholeImgPath = sdcard + localEasDir + "/"
            + localImageDir + "/";

    private Integer state = 0;
    static Integer STATE_SEND = 20;
    static Integer STATE_RECEIPT = 30;

    UpdateRecordsBean updateRecord = null;
    DbAdapter dbHandle = null;
    protected static String EXTRA_MESSAGE_ID = "msg_id";
    protected static String EXTRA_MESSAGE_QCODE = "msg_qcode";
    protected static String EXTRA_MESSAGE_STATE = "msg_state";
    protected static String EXTRA_MESSAGE_DATE = "msg_date";
    protected static String EXTRA_MESSAGE_PATH = "msg_path";

    String ItemID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandle = new DbAdapter(this);
        checkDatabase();
        checkConfig();
        IntentFilter filter = new IntentFilter(
                "cn.wislight.logisticassistant.broadcast");
        registerReceiver(receiver, filter);
        initList(this);

        progress = (ProgressBar) findViewById(R.id.progress_horizontal);
        progress.setVisibility(View.GONE);

        button1 = (Button) findViewById(R.id.btn1);
        button2 = (Button) findViewById(R.id.btn2);
        button3 = (Button) findViewById(R.id.btn3);

        System.out.println("button1 is: " + button1);

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                state = STATE_RECEIPT;
                scanQcode();
				/*
	*/
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // callWebService();
                state = STATE_SEND;
                scanQcode();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {



                Intent intent = new Intent();// ���ݲ�����
                intent.setClass(MainActivity.this, UploadService.class);
                startService(intent);
            }
        });
		/*
		 * DbAdapter db = new DbAdapter(this); // ---get a title--- db.open();
		 * long id; UpdateRecordsBean bean = new UpdateRecordsBean();
		 * bean.setDate("1223123"); bean.setImage_uploaded(false);
		 * bean.setLocation("xian"); bean.setNote("hehe");
		 * bean.setQcode_id("100"); bean.setFileName("100.jpg");
		 * bean.setState(1); bean.setTable_uploaded(false); id =
		 * db.insertUpdateRecords(bean);
		 *
		 * bean.setDate("12231233"); id = db.insertUpdateRecords(bean);
		 *
		 * Cursor c = db.getAllUpdateRecords(); if (c.moveToFirst()) { do {
		 * DisplayTitle(c); } while (c.moveToNext()); }
		 *
		 * db.setImageUploaded(3, true); db.setRecordsTableUploaded(3, true);
		 *
		 * db.close();
		 */


    }


    protected void scanQcode() {
        // TODO Auto-generated method stub
        updateRecord = null;
        updateRecord = new UpdateRecordsBean();

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, MipcaActivityCapture.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
    }



    private void checkDatabase() {
        // TODO Auto-generated method stub
        dbHandle.open();
        if (null == dbHandle.getConfigUrl()){
            dbHandle.insertConfig("");
        }

        if (null == dbHandle.getOwnerInfoName()){
            dbHandle.insertOwnerInfo("", "", "");
        }

        Resources res =getResources();
        String[] company=res.getStringArray(R.array.company);
        String[] wanUrl=res.getStringArray(R.array.wan_url);
        String[] lanUrl=res.getStringArray(R.array.lan_url);
        if (0 == dbHandle.getCompanysCount()){
            for (int i=0; i<company.length; i++){
                dbHandle.insertCompany(company[i], wanUrl[i], lanUrl[i]);
            }
        }

        if (company.length != dbHandle.getCompanysCount()){
            dbHandle.deleteAllCompanys();

            for (int i=0; i<company.length; i++){
                dbHandle.insertCompany(company[i], wanUrl[i], lanUrl[i]);
            }
        }

        if (company.length == dbHandle.getCompanysCount()){
			/* check url updated or not */
            boolean bUrlUpdated = false;
            int i = 0;
            Cursor cursor = dbHandle.getCompanys();

            if (cursor.moveToFirst()){

                do {
                    if (!wanUrl[i].equals(cursor.getString(1)) || !lanUrl[i].equals(cursor.getString(2))){
                        bUrlUpdated = true;
                        break;
                    }
                    i++;
                } while (cursor.moveToNext());
            }
            if (bUrlUpdated){
                dbHandle.deleteAllCompanys();
                for (int j=0; j<company.length; j++){
                    dbHandle.insertCompany(company[j], wanUrl[j], lanUrl[j]);
                }
            }

        }

        dbHandle.close();
    }


    private void checkConfig() {
        // TODO Auto-generated method stub
        dbHandle.open();
        String url = dbHandle.getConfigUrl();
        dbHandle.close();

        if ("".equals(url)){

            Toast toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.pls_config),
                    Toast.LENGTH_SHORT);
            toast.show();

            Intent intent = new Intent(getBaseContext(),
                    ConfigActivity.class);
            startActivity(intent);
        }
    }


    private void initList(Context ctx) {

        receiptList = (ListView) findViewById(R.id.ListView01);
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        DbAdapter db = new DbAdapter(ctx);
        db.open();
        Cursor cursor = db.getUnUploadedRecords();
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, Object> map = new HashMap<String, Object>();
                if (STATE_SEND.toString().equals(cursor.getString(2))) {
                    map.put("ItemImage", R.drawable.send);
                    map.put("ItemTitle1",
                            ctx.getResources().getString(R.string.to_start) + ":"
                                    + cursor.getString(1));
                } else if (STATE_RECEIPT.toString().equals(cursor.getString(2))) {
                    map.put("ItemImage", R.drawable.send_end);
                    map.put("ItemTitle1",
                            ctx.getResources().getString(R.string.to_receipt) + ":"
                                    + cursor.getString(1));
                }
                map.put("ItemTitle",
                        ctx.getResources().getString(R.string.qcode)
                                + cursor.getString(1));
                map.put("ItemText", cursor.getString(4));
                map.put("ItemID", cursor.getString(0));
                map.put("ItemPath", cursor.getString(8));
                map.put("ItemState", cursor.getString(2));
                listItem.add(map);
            } while (cursor.moveToNext());
        }

        db.close();
        SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
                R.layout.list_items,

                new String[] { "ItemImage", "ItemTitle1", "ItemText" },
                new int[] { R.id.ItemImage, R.id.ItemTitle, R.id.ItemText });

        receiptList.setAdapter(listItemAdapter);

        receiptList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                Object o = receiptList.getItemAtPosition(arg2);
                @SuppressWarnings("rawtypes")
                HashMap map = (HashMap) o;
                Intent intent = new Intent(getBaseContext(),
                        DetailActivity.class);
                intent.putExtra(EXTRA_MESSAGE_ID, (String) map.get("ItemID"));
                intent.putExtra(EXTRA_MESSAGE_QCODE,
                        (String) map.get("ItemTitle"));
                intent.putExtra(EXTRA_MESSAGE_STATE,
                        (String) map.get("ItemState"));
                intent.putExtra(EXTRA_MESSAGE_DATE,
                        (String) map.get("ItemText"));
                intent.putExtra(EXTRA_MESSAGE_PATH,
                        (String) map.get("ItemPath"));
                startActivity(intent);
            }
        });

        receiptList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                // TODO Auto-generated method stub

                Object o = receiptList.getItemAtPosition(arg2);
                @SuppressWarnings("rawtypes")
                HashMap map = (HashMap) o;
                ItemID = (String) map.get("ItemID");
                return false;
            }
        });

        receiptList
                .setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v,
                                                    ContextMenu.ContextMenuInfo menuInfo) {
                        menu.setHeaderTitle(getString(R.string.options));
                        menu.add(0, 0, 0, getString(R.string.delete));
                        menu.add(0, 1, 0, getString(R.string.delete_all));

                    }
                });

    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();

    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String message = null;
            String cmd = intent.getExtras().getString("cmd");
            if (null != cmd && "finish".equals(cmd)) {
                message = getString(R.string.finish_uploading);
                updateList();
            } else if (null != cmd && "error".equals(cmd)) {
                message = getString(R.string.errormsg);
            } else if (null != cmd && "updateUI".equals(cmd)) {
                updateList();
            } else if (null != cmd && "isuploading".equals(cmd)) {
                // do nothing
            } else if (null != cmd && "clearResult".equals(cmd)){
                String resultFormat = getResources().getString(
                        R.string.delete_file_result);
                String count = intent.getExtras().getString("count");

                message = String.format(resultFormat, new Integer(count));
            } else if (null != cmd && "testResult".equals(cmd)){
                String result = intent.getExtras().getString("result");
                if (null != result && "1".equals(result)){
                    message = getResources().getString(
                            R.string.connect_ok);
                }else if (null == result || "0".equals(result)){
                    message = getResources().getString(
                            R.string.connect_fail);
                }
            } else if (null != cmd && "progress".equals(cmd)){
                String step = intent.getExtras().getString("step");
                if (null != step && "init".equals(step)){
                    progress.setVisibility(View.VISIBLE);
                    progress.setProgress(2);
                }else if (null != step){
                    progress.setProgress(progress.getProgress() + new Integer(step));
                }
            }

            if (null != message) {
                Toast toast = Toast.makeText(getApplicationContext(), message,
                        Toast.LENGTH_SHORT);
                toast.show();

                progress.setVisibility(View.GONE);
            }
        }
    };


    private void updateList() {
        // TODO Auto-generated method stub
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        dbHandle.open();
        Cursor cursor = dbHandle.getUnUploadedRecords();
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, Object> map = new HashMap<String, Object>();
                if (STATE_SEND.toString().equals(cursor.getString(2))) {
                    map.put("ItemImage", R.drawable.send);
                    map.put("ItemTitle1",
                            getString(R.string.to_start) + ":"
                                    + cursor.getString(1));
                } else if (STATE_RECEIPT.toString().equals(cursor.getString(2))) {
                    map.put("ItemImage", R.drawable.send_end);
                    map.put("ItemTitle1",
                            getString(R.string.to_receipt) + ":"
                                    + cursor.getString(1));
                }
                map.put("ItemTitle",
                        getString(R.string.qcode) + cursor.getString(1));
                map.put("ItemText", cursor.getString(4));
                map.put("ItemID", cursor.getString(0));
                map.put("ItemPath", cursor.getString(8));
                map.put("ItemState", cursor.getString(2));
                listItem.add(map);
            } while (cursor.moveToNext());
        }
        dbHandle.close();

        SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,
                R.layout.list_items, new String[] { "ItemImage", "ItemTitle1",
                "ItemText" }, new int[] { R.id.ItemImage,
                R.id.ItemTitle, R.id.ItemText });

        receiptList.setAdapter(listItemAdapter);
    }



}

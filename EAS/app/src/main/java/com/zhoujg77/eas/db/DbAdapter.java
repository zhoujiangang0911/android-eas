package com.zhoujg77.eas.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zhoujg77.eas.bean.UpdateRecordsBean;

/**
 * Created by zhoujiangang on 15/11/29.
 */
public class DbAdapter {


    public static final String KEY_ROWID = "_id";
    public static final String KEY_QCODE = "qcode_id";
    public static final String KEY_STATE = "state";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_DATE = "date";
    public static final String KEY_NOTE = "note";
    public static final String KEY_TABLEUP = "table_uploaded";
    public static final String KEY_IMGUP = "image_uploaded";
    public static final String KEY_FILENAME = "file_name";

    private static final String TAG = "DbAdapter";
    private static final String DATABASE_NAME = "records";
    private static final String DATABASE_TABLE = "upload_records";
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_CREATE = "create table upload_records (_id integer primary key autoincrement, qcode_id text not null, state integer, location text, date text not null, note text, table_uploaded boolean, image_uploaded boolean, file_name text);";

    /* owner info */
    public static final String OWNER_TABLE = "owner_info";
    public static final String KEY_MAC = "mac_address";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_OWNER = "owner";
    private static final String OWNERINFO_CREATE = "create table owner_info (_id integer primary key autoincrement, owner text, phone text, mac_address text);";

    /* config */
    public static final String KEY_URL = "url";
    public static final String CONFIG_TABLE = "config";
    private static final String CONFIGRATION_CREATE = "create table config (_id integer primary key autoincrement, url text);";

    /* company */
    public static final String COMPANY_TABLE = "companys";
    public static final String KEY_WANURL = "wan_url";
    public static final String KEY_LANURL = "lan_url";
    public static final String KEY_COMPANY = "company";
    private static final String COMPANY_CREATE = "create table companys (_id integer primary key autoincrement, company text, wan_url text, lan_url text);";

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DbAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
            db.execSQL(OWNERINFO_CREATE);
            db.execSQL(CONFIGRATION_CREATE);
            db.execSQL(COMPANY_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            if (newVersion == 4){
                db.execSQL(COMPANY_CREATE);
                db.execSQL("DROP TABLE IF EXISTS owner_info");
                db.execSQL(OWNERINFO_CREATE);
                db.execSQL(CONFIGRATION_CREATE);
            }
        }
    }

    // ---�����ݿ�---

    public DbAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    // ---�ر����ݿ�---

    public void close() {
        DBHelper.close();
    }

    public long insertConfig(String url) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_URL, url);
        return db.insert(CONFIG_TABLE, null, initialValues);
    }

    public long insertOwnerInfo(String owner, String phone, String macAddr) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_OWNER, owner);
        initialValues.put(KEY_PHONE, phone);
        initialValues.put(KEY_MAC, macAddr);
        return db.insert(OWNER_TABLE, null, initialValues);
    }

    public long insertUpdateRecords(String qcode, Integer state,
                                    String location, String date, String note, String name) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_QCODE, qcode);
        initialValues.put(KEY_STATE, state);
        initialValues.put(KEY_LOCATION, location);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_NOTE, note);
        initialValues.put(KEY_TABLEUP, false);
        initialValues.put(KEY_IMGUP, false);
        initialValues.put(KEY_FILENAME, name);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public long insertUpdateRecords(UpdateRecordsBean bean) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_QCODE, bean.getQcode_id());
        initialValues.put(KEY_STATE, bean.getState());
        initialValues.put(KEY_LOCATION, bean.getLocation());
        initialValues.put(KEY_DATE, bean.getDate());
        initialValues.put(KEY_NOTE, bean.getDate());
        initialValues.put(KEY_TABLEUP, bean.isTable_uploaded());
        initialValues.put(KEY_IMGUP, bean.isImage_uploaded());
        initialValues.put(KEY_FILENAME, bean.fileName);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public long insertCompany(String company, String wanUrl, String lanUrl) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_COMPANY, company);
        initialValues.put(KEY_WANURL, wanUrl);
        initialValues.put(KEY_LANURL, lanUrl);
        return db.insert(COMPANY_TABLE, null, initialValues);
    }

    // ---ɾ��һ��ָ������---

    public boolean deleteUpdateRecords(long rowId) {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    // ---�������б���---

    public Cursor getAllUpdateRecords() {
        return db.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_QCODE,
                KEY_STATE, KEY_LOCATION, KEY_DATE, KEY_NOTE, KEY_TABLEUP,
                KEY_IMGUP, KEY_FILENAME }, null, null, null, null, null);
    }

    // ---����һ��ָ������---

    public Cursor getUpdateRecords(long rowId) throws SQLException {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] {
                KEY_ROWID, KEY_QCODE, KEY_STATE, KEY_LOCATION, KEY_DATE,
                KEY_NOTE, KEY_TABLEUP, KEY_IMGUP, KEY_FILENAME }, KEY_ROWID
                + "=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getCompanys() {
        return db.query(COMPANY_TABLE, new String[] { KEY_COMPANY, KEY_WANURL,
                KEY_LANURL }, null, null, null, null, null);
    }
    public Cursor getCompany(long rowId) {
        Cursor mCursor = db.query(COMPANY_TABLE, new String[] { KEY_COMPANY, KEY_WANURL,
                KEY_LANURL }, KEY_ROWID
                + "=" + rowId, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getCompany(String company) {
        Cursor mCursor = db.query(COMPANY_TABLE, new String[] { KEY_COMPANY, KEY_WANURL,
                KEY_LANURL }, KEY_COMPANY
                + "=" + company, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Integer getCompanysCount() {
        Cursor cur = db.query(COMPANY_TABLE, new String[] { KEY_COMPANY,
                KEY_WANURL, KEY_LANURL }, null, null, null, null, null);
        Integer count = 0;
        if (cur.moveToFirst()) {
            do {
                count++;
            } while (cur.moveToNext());
        }

        return count;
    }

    public Cursor getConfig() {
        return db.query(CONFIG_TABLE, new String[] { KEY_ROWID, KEY_URL },
                null, null, null, null, null);
    }

    public Cursor getOwnerInfo() {
        return db.query(OWNER_TABLE, new String[] { KEY_ROWID, KEY_OWNER,
                KEY_PHONE, KEY_MAC }, null, null, null, null, null);
    }

    public boolean updateUpdateRecords(long rowId, String qcode, Integer state,
                                       String location, String date, String note, boolean btable,
                                       boolean bimage, String name) {
        ContentValues args = new ContentValues();
        args.put(KEY_QCODE, qcode);
        args.put(KEY_STATE, state);
        args.put(KEY_LOCATION, location);
        args.put(KEY_DATE, date);
        args.put(KEY_NOTE, note);
        args.put(KEY_TABLEUP, btable);
        args.put(KEY_IMGUP, bimage);
        args.put(KEY_FILENAME, name);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean setRecordsTableUploaded(long rowId, boolean btable) {
        ContentValues args = new ContentValues();
        args.put(KEY_TABLEUP, btable);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public boolean setOwnerInfoName(String name) {
        ContentValues args = new ContentValues();
        args.put(KEY_OWNER, name);
        return db.update(OWNER_TABLE, args, KEY_ROWID + "=1", null) > 0;
    }

    public String getOwnerInfoName() {

        Cursor cur = null;
        try {
            cur = db.query(OWNER_TABLE, new String[] { KEY_ROWID, KEY_OWNER,
                    KEY_PHONE, KEY_MAC }, null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (cur.moveToFirst()) {
            return cur.getString(1);
        }
        return null;
    }

    public boolean setOwnerInfoPhone(String phone) {
        ContentValues args = new ContentValues();
        args.put(KEY_PHONE, phone);
        return db.update(OWNER_TABLE, args, KEY_ROWID + "=1", null) > 0;
    }

    public String getOwnerInfoPhone() {

        Cursor cur = null;
        try {
            cur = db.query(OWNER_TABLE, new String[] { KEY_ROWID, KEY_OWNER,
                    KEY_PHONE, KEY_MAC }, null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (cur.moveToFirst()) {
            return cur.getString(2);
        }
        return null;
    }

    public boolean setOwnerInfoMac(String mac) {
        ContentValues args = new ContentValues();
        args.put(KEY_MAC, mac);
        return db.update(OWNER_TABLE, args, KEY_ROWID + "=1", null) > 0;
    }

    public String getOwnerInfoMac() {

        Cursor cur = null;
        try {
            cur = db.query(OWNER_TABLE, new String[] { KEY_ROWID, KEY_OWNER,
                    KEY_PHONE, KEY_MAC }, null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (cur.moveToFirst()) {
            return cur.getString(3);
        }
        return null;
    }

    public boolean setConfigUrl(String url) {
        ContentValues args = new ContentValues();
        args.put(KEY_URL, url);
        return db.update(CONFIG_TABLE, args, KEY_ROWID + "=1", null) > 0;
    }

    public String getConfigUrl() {

        Cursor cur = null;
        try {
            cur = db.query(CONFIG_TABLE, new String[] { KEY_ROWID, KEY_URL },
                    null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (cur.moveToFirst()) {
            return cur.getString(1);
        }
        return null;
    }

    public boolean setImageUploaded(long rowId, boolean bImage) {
        ContentValues args = new ContentValues();
        args.put(KEY_IMGUP, bImage);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor getUnUploadedRecords() {
        // TODO Auto-generated method stub
        return db
                .query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_QCODE,
                        KEY_STATE, KEY_LOCATION, KEY_DATE, KEY_NOTE,
                        KEY_TABLEUP, KEY_IMGUP, KEY_FILENAME }, KEY_TABLEUP
                        + "=?" + " OR " + KEY_IMGUP + "=?", new String[] { "0",
                        "0" }, null, null, null);
    }

    public Integer getUnUploadedRecordsCount(){
        Cursor cur = db
                .query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_QCODE,
                        KEY_STATE, KEY_LOCATION, KEY_DATE, KEY_NOTE,
                        KEY_TABLEUP, KEY_IMGUP, KEY_FILENAME }, KEY_TABLEUP
                        + "=?" + " OR " + KEY_IMGUP + "=?", new String[] { "0",
                        "0" }, null, null, null);
        int count = 0;
        if (cur.moveToFirst()) {
            do {
                count++;
            } while (cur.moveToNext());
        }
        return count;
    }



    @SuppressLint("UseValueOf")
    public int deleteAllUnUploaderRocords() {
        // TODO Auto-generated method stub
        Cursor cur = db.query(DATABASE_TABLE, new String[] { KEY_ROWID,
                        KEY_QCODE, KEY_STATE, KEY_LOCATION, KEY_DATE, KEY_NOTE,
                        KEY_TABLEUP, KEY_IMGUP, KEY_FILENAME }, KEY_TABLEUP + "=?"
                        + " OR " + KEY_IMGUP + "=?", new String[] { "0", "0" }, null,
                null, null);
        int count = 0;
        if (cur.moveToFirst()) {
            do {
                boolean re = db.delete(DATABASE_TABLE, KEY_ROWID + "="
                        + new Integer(cur.getString(0)), null) > 0;
                if (re) {
                    count++;
                }
            } while (cur.moveToNext());
        }
        return count;
    }

    public Cursor getUploadedRecords() {
        // TODO Auto-generated method stub
        return db
                .query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_QCODE,
                        KEY_STATE, KEY_LOCATION, KEY_DATE, KEY_NOTE,
                        KEY_TABLEUP, KEY_IMGUP, KEY_FILENAME }, KEY_TABLEUP
                        + "=?" + " AND " + KEY_IMGUP + "=?", new String[] {
                        "1", "1" }, null, null, null);
    }

    @SuppressLint("UseValueOf")
    public Integer deleteAllCompanys() {
        // TODO Auto-generated method stub
        Cursor cur = db.query(COMPANY_TABLE, new String[] { KEY_ROWID, KEY_COMPANY, KEY_WANURL,
                KEY_LANURL }, null, null, null, null, null);
        int count = 0;
        if (cur.moveToFirst()) {
            do {
                boolean re = db.delete(COMPANY_TABLE, KEY_ROWID + "="
                        + new Integer(cur.getString(0)), null) > 0;
                if (re) {
                    count++;
                }
            } while (cur.moveToNext());
        }
        return count;
    }


}

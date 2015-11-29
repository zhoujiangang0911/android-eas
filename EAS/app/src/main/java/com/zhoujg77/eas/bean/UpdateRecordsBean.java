package com.zhoujg77.eas.bean;

/**
 * Created by zhoujiangang on 15/11/29.
 */
public class UpdateRecordsBean {
    Integer _id;
    String qcode_id;
    Integer state;
    String location;
    String date;
    String note;
    boolean table_uploaded;
    boolean image_uploaded;
    public String fileName;

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public Integer get_id() {
        return _id;
    }
    public void set_id(Integer _id) {
        this._id = _id;
    }
    public String getQcode_id() {
        return qcode_id;
    }
    public void setQcode_id(String qcode_id) {
        this.qcode_id = qcode_id;
    }
    public Integer getState() {
        return state;
    }
    public void setState(Integer state) {
        this.state = state;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public boolean isTable_uploaded() {
        return table_uploaded;
    }
    public void setTable_uploaded(boolean table_uploaded) {
        this.table_uploaded = table_uploaded;
    }
    public boolean isImage_uploaded() {
        return image_uploaded;
    }
    public void setImage_uploaded(boolean image_uploaded) {
        this.image_uploaded = image_uploaded;
    }
}

package com.zhoujg77.eas.bean;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Created by zhoujiangang on 15/11/29.
 */
public class ReceiptBean implements KvmSerializable {



    Integer id;
    Integer receiptId;
    String qcodeId;
    String filePath;
    Integer state;
    String phone;
    String macAddress;
    String location;
    String owner;
    String date;
    String note;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Integer receiptId) {
        this.receiptId = receiptId;
    }

    public String getQcodeId() {
        return qcodeId;
    }

    public void setQcodeId(String qcodeId) {
        this.qcodeId = qcodeId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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


    @Override
    public Object getProperty(int arg0) {
        // TODO Auto-generated method stub
        Object res = null;
        switch (arg0) {
            case 0:
                res = this.date;
                break;
            case 1:
                res = this.filePath;
                break;
            case 2:
                res = this.id;
                break;
            case 3:
                res = this.location;
                break;
            case 4:
                res = this.macAddress;
                break;
            case 5:
                res = this.note;
                break;
            case 6:
                res = this.owner;
                break;
            case 7:
                res = this.phone;
                break;
            case 8:
                res = this.qcodeId;
                break;
            case 9:
                res = this.receiptId;
                break;
            case 10:
                res = this.state;
                break;

            default:
                break;
        }
        return res;

    }

    @Override
    public int getPropertyCount() {
        // TODO Auto-generated method stub
        return 11;
    }

    @Override
    public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
        // TODO Auto-generated method stub
        switch (arg0) {
            case 0:
                arg2.type =  PropertyInfo.STRING_CLASS;
                arg2.name = "date";
                break;
            case 1:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "filePath";
                break;
            case 2:
                arg2.type = PropertyInfo.INTEGER_CLASS;
                arg2.name = "id";
                break;
            case 3:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "location";
                break;
            case 4:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "macAddress";
                break;
            case 5:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "note";
                break;
            case 6:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "owner";
                break;
            case 7:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "phone";
                break;
            case 8:
                arg2.type = PropertyInfo.STRING_CLASS;
                arg2.name = "qcodeId";
                break;
            case 9:
                arg2.type = PropertyInfo.INTEGER_CLASS;
                arg2.name = "receiptId";
                break;
            case 10:
                arg2.type = PropertyInfo.INTEGER_CLASS;
                arg2.name = "state";
                break;
            default:
                break;
        }
    }

    @Override
    public void setProperty(int arg0, Object arg1) {
        // TODO Auto-generated method stub
        switch (arg0) {
            case 0:
                this.date = arg1.toString();
                break;
            case 1:
                this.filePath = arg1.toString();
                break;
            case 2:
                this.id = Integer.valueOf(arg1.toString());
                ;
                break;
            case 3:
                this.location =  arg1.toString();;
                break;
            case 4:
                this.macAddress =  arg1.toString();
                break;
            case 5:
                this.note = arg1.toString();
                break;
            case 6:
                this.owner = arg1.toString();
                break;
            case 7:
                this.phone = arg1.toString();
                break;
            case 8:
                this.qcodeId = arg1.toString();
                break;
            case 9:
                this.receiptId = Integer.valueOf(arg1.toString());
                break;
            case 10:
                this.state = Integer.valueOf(arg1.toString());
                break;

            default:
                break;
        }
    }

}

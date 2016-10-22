package com.ariel.guardian.library.db.model;

import com.orm.SugarRecord;

/**
 * Created by mikalackis on 7.10.16..
 *
 * Main data transfer class. This class is used to hold
 * and transfer data between devices via pubnub
 */

public class WrapperMessage extends SugarRecord {

    private String sender;

    private String type;

    private String dataObject;

    public WrapperMessage(){
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDataObject(String dataObject) {
        this.dataObject = dataObject;
    }

    public String getSender() {
        return sender;
    }

    public String getType() {
        return type;
    }

    public String getDataObject() {
        return dataObject;
    }

}

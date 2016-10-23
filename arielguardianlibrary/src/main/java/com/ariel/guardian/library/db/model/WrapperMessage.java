package com.ariel.guardian.library.db.model;

import com.ariel.guardian.library.db.ArielDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by mikalackis on 7.10.16..
 *
 * Main data transfer class. This class is used to hold
 * and transfer data between devices via pubnub
 */

@Table(database = ArielDatabase.class)
public class WrapperMessage extends ArielModel {

    @Column
    @PrimaryKey
    long id;

    @Column
    private String sender;

    @Column
    private String type;

    @Column
    private String dataObject;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

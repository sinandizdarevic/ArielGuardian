package com.ariel.guardian.library.db.realm.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mikalackis on 7.10.16..
 */

public class WrapperMessage extends RealmObject{

    @PrimaryKey
    private long id;

    private String sender;

    private String type;

    private String realmObject;

    public WrapperMessage(){

    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRealmObject(String realmObject) {
        this.realmObject = realmObject;
    }

    public String getSender() {
        return sender;
    }

    public String getType() {
        return type;
    }

    public String getRealmObject() {
        return realmObject;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

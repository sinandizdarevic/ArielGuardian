package com.ariel.guardian.library.database.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mikalackis on 7.10.16..
 *
 * Main data transfer class. This class is used to hold
 * and transfer data between devices via pubnub
 */

public class WrapperMessage extends RealmObject {

    @PrimaryKey
    long id;

    private String sender;

    private String messageType;

    private String actionType;

    private String dataObject;

    private boolean reportReception;

    private boolean executed;

    // this field should be used only in combination with report messages
    private String originalMessageType;

    private boolean sent;

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public void setDataObject(String dataObject) {
        this.dataObject = dataObject;
    }

    public String getSender() {
        return sender;
    }

    public String getActionType() {
        return actionType;
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

    public boolean getReportReception() {
        return reportReception;
    }

    public void setReportReception(boolean reportReception) {
        this.reportReception = reportReception;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public String getOriginalMessageType() {
        return originalMessageType;
    }

    public void setOriginalMessageType(String originalMessageType) {
        this.originalMessageType = originalMessageType;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }
}

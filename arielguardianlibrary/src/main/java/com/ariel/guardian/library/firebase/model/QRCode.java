package com.ariel.guardian.library.firebase.model;

/**
 * Created by mikalackis on 6.9.16..
 */
public class QRCode {

    public String qrCode;

    public String timestamp;

    public String userThatActivated;


    public QRCode(){

    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserThatActivated() {
        return userThatActivated;
    }

    public void setUserThatActivated(String userThatActivated) {
        this.userThatActivated = userThatActivated;
    }
}

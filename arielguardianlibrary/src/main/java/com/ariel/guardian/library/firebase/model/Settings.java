package com.ariel.guardian.library.firebase.model;

/**
 * Created by mikalackis on 6.9.16..
 */
public class Settings {

    public String pubNubCipherKey;

    public String pubNubPublishKey;

    public String pubNubSecretKey;

    public String pubNubSubscribeKey;

    public boolean pubNubUseSSL;

    public Settings(){

    }

    public String getPubNubCipherKey() {
        return pubNubCipherKey;
    }

    public void setPubNubCipherKey(String pubNubCipherKey) {
        this.pubNubCipherKey = pubNubCipherKey;
    }

    public String getPubNubPublishKey() {
        return pubNubPublishKey;
    }

    public void setPubNubPublishKey(String pubNubPublishKey) {
        this.pubNubPublishKey = pubNubPublishKey;
    }

    public String getPubNubSecretKey() {
        return pubNubSecretKey;
    }

    public void setPubNubSecretKey(String pubNubSecretKey) {
        this.pubNubSecretKey = pubNubSecretKey;
    }

    public String getPubNubSubscribeKey() {
        return pubNubSubscribeKey;
    }

    public void setPubNubSubscribeKey(String pubNubSubscribeKey) {
        this.pubNubSubscribeKey = pubNubSubscribeKey;
    }

    public boolean isPubNubUseSSL() {
        return pubNubUseSSL;
    }

    public void setPubNubUseSSL(boolean pubNubUseSSL) {
        this.pubNubUseSSL = pubNubUseSSL;
    }
}

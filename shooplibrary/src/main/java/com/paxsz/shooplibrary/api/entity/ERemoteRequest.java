package com.paxsz.shooplibrary.api.entity;

/**
 * Created by Mohsen Beiranvand on 17/08/10.
 */

public class ERemoteRequest {

    private int type;
    private String version;
    private String message;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

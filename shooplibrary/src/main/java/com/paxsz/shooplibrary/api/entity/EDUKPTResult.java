package com.paxsz.shooplibrary.api.entity;

/**
 * Created by Mohsen Beiranvand on 17/06/11.
 */

public class EDUKPTResult {
    /**
     * Key Serial Number
     */
    private byte[] KSN;
    /**
     * point to the generated 8 bytes PIN Block
     */
    private byte[] result;

    public byte[] getKSN() {
        return KSN;
    }

    public void setKSN(byte[] KSN) {
        this.KSN = KSN;
    }

    public byte[] getResult() {
        return result;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }
}

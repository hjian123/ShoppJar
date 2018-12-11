package com.paxsz.shooplibrary.api.entity;

/**
 * Created by Mohsen Beiranvand on 17/06/11.
 */

public class ETrackData {

    private String Track1;
    private String Track2;
    private String Track3;
    private String EncTrack2;
    private String PAN;
    private String MaskedPAN;
    private String KSN;
    private String PinBlock;
    private int ResultCode;

    public String getTrack1() {
        return Track1;
    }

    public void setTrack1(String track1) {
        Track1 = track1;
    }

    public String getTrack2() {
        return Track2;
    }

    public void setTrack2(String track2) {
        Track2 = track2;
    }

    public String getTrack3() {
        return Track3;
    }

    public void setTrack3(String track3) {
        Track3 = track3;
    }

    public String getPAN() {
        return PAN;
    }

    public void setPAN(String PAN) {
        this.PAN = PAN;
    }

    public String getMaskedPAN() {
        return MaskedPAN;
    }

    public void setMaskedPAN(String maskedPAN) {
        MaskedPAN = maskedPAN;
    }


    public String getKSN() {
        return KSN;
    }

    public void setKSN(String KSN) {
        this.KSN = KSN;
    }

    public String getPinBlock() {
        return PinBlock;
    }

    public void setPinBlock(String pinBlock) {
        PinBlock = pinBlock;
    }

    public int getResultCode() {
        return ResultCode;
    }

    public void setResultCode(int resultCode) {
        ResultCode = resultCode;
    }

    public String getEncTrack2() {
        return EncTrack2;
    }

    public void setEncTrack2(String encTrack2) {
        EncTrack2 = encTrack2;
    }
}

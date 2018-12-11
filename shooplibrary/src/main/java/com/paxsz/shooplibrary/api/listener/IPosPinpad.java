package com.paxsz.shooplibrary.api.listener;

import android.support.annotation.Nullable;

import com.paxsz.shooplibrary.api.entity.EDUKPTResult;


/**
 * Created by Mohsen Beiranvand on 17/06/22.
 */

public interface IPosPinpad {

    void pinpadErase() throws Exception;

    @Nullable
    byte[] getPinBlock(String dataIn, int timeOut) throws Exception;

    @Nullable
    EDUKPTResult getDUKPTPin(String dataIn, int timeoutMs)  throws Exception;

    @Nullable
    String getPinpadSN();

    void injectTLK(String key, String KCV) throws Exception;
    void injectTMK(String key, String KCV) throws Exception;
    void injectTPK(String key, String KCV, boolean isDUKPT) throws Exception;
    void injectTDK(String key, String KCV, boolean isDUKPT) throws Exception;
    void injectTAK(String key, String KCV, boolean isDUKPT) throws Exception;
    void injectTIK(String key, String KSN, String KCV) throws Exception;

    void inputPinListener();

    @Nullable
    String inputStr(byte mode, byte min, byte max, int timeoutMs);
    void showStr(byte x, byte y, String message);
    void clearScreen();

    boolean isInjected();

    boolean hasMasterKey(boolean isDUKPT);

    boolean injectKTM(IPosPinpadInjectProcess injectProcess, boolean isDUKPT);

}

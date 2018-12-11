package com.paxsz.shooplibrary.api.listener;

import android.support.annotation.Nullable;

import com.paxsz.shooplibrary.api.entity.EDUKPTResult;
import com.paxsz.shooplibrary.api.enums.EnPedDesMode;


/**
 * Created by Mohsen Beiranvand on 17/06/11.
 */

/**
 * Pin Entry Device (PED) Interface
 */
public interface IPosPed {

    @Nullable
    String getPedSN();
    @Nullable
    String getPedVersion();

    @Nullable
    byte[] getMac(byte[] dataIn) throws Exception;

    @Nullable
    EDUKPTResult getDUPKTMac(byte[] dataIn) throws Exception;

    @Nullable
    byte[] calcDes(byte[] dataIn, EnPedDesMode mode)  throws Exception;

    @Nullable
    EDUKPTResult calcDUKPTDes(byte[] dataIn, EnPedDesMode mode) ;

    @Nullable
    byte[] getKSN();

    void incDUKPTKsn();

    void setExternalMode(int mode);

}

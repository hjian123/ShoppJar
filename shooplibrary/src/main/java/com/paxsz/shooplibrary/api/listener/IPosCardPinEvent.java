package com.paxsz.shooplibrary.api.listener;

import android.support.annotation.Nullable;

import com.paxsz.shooplibrary.api.entity.ETrackData;


/**
 * Created by Mohsen Beiranvand on 17/07/28.
 */

public interface IPosCardPinEvent {

    void onDeviceNotReady();

    void onPreCardRead();

    void onCardRead();

    void onCardFailed(@Nullable String message, @Nullable Exception exception);

    void onPrePinRead();

    void onPinRead();

    void onPinFailed(@Nullable String message, @Nullable Exception exception);

    void onCardPinReadSucceed(@Nullable ETrackData trackData);

    void onCardPinReadFailed(@Nullable String message, @Nullable Exception exception);

}

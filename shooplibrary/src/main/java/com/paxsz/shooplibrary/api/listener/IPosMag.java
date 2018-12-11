package com.paxsz.shooplibrary.api.listener;

import android.support.annotation.Nullable;

import com.paxsz.shooplibrary.api.entity.ETrackData;

/**
 * Created by Mohsen Beiranvand on 17/06/11.
 */

public interface IPosMag {

    @Nullable
    ETrackData readMagCard(int timeOutMs);
    void resetMagCard();
    void cancelReadCard();
}

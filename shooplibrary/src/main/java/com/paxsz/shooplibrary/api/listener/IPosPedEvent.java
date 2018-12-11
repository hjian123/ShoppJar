package com.paxsz.shooplibrary.api.listener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.os.Handler;

/**
 * Created by Mohsen Beiranvand on 17/07/07.
 */

public interface IPosPedEvent {

    BroadcastReceiver registerEntryEvent(Activity activity, Handler handler);
    void unregisterEntryEvent(Activity activity, BroadcastReceiver receiver);
}

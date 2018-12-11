package com.paxsz.shooplibrary.api.listener;

/**
 * Created by m.tavakoli on 11/13/2018.
 */

public interface IPosChangeChannelProcess {

    void onStart();
    void onFinish();
    void OnError();
}

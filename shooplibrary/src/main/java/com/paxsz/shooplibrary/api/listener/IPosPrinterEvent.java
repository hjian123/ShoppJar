package com.paxsz.shooplibrary.api.listener;

/**
 * Created by Mohsen Beiranvand on 17/08/06.
 */

public interface IPosPrinterEvent {

    void onPrintStarted();
    void onPrinterError(String error, boolean isPaperError);
    void onPrintEnd();

}

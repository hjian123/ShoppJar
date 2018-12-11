package com.paxsz.shooplibrary.api.listener;

import java.util.concurrent.ExecutionException;


public interface IKeyInjectListener {

    void onErase();
    void eraseCompleted();
    void eraseFailed();

    void insertCard(boolean isTK);
    void cardInserted(boolean isTK);
    String enterCardPin(boolean isTK) throws ExecutionException, InterruptedException;
    void onCardPinWrong(boolean isTK);
    void onCardReading(boolean isTK);
    void cardReadingFailed(boolean isTK);
    void takeCard(boolean isTK);

    void operationFailed();
    void operationComplete();
}

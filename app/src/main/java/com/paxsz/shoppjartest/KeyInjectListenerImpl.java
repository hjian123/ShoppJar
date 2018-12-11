package com.paxsz.shoppjartest;

import android.app.AlertDialog;
import android.content.Context;

import com.paxsz.shooplibrary.api.listener.IKeyInjectListener;


public class KeyInjectListenerImpl implements IKeyInjectListener {

    protected Context context;
    private AlertDialog dialog;
    final static String TITLE = "Key Inject";

    public KeyInjectListenerImpl(Context context) {
        this.context = context;
    }

    @Override
    public void onCardPinWrong(boolean b) {
        showDialog(TITLE,"Card PIN is Wrong");
    }

    @Override
    public void onCardReading(boolean b) {
        showDialog(TITLE,"Card reading...");
    }

    @Override
    public void onErase() {
        showDialog(TITLE,"PIN has Erased");
    }

    @Override
    public void operationComplete() {
        showDialog(TITLE,"Operation Complete");
    }

    @Override
    public void operationFailed() {
        showDialog(TITLE,"Operation Failed");
    }

    @Override
    public void eraseFailed() {
        showDialog(TITLE,"Erase Failed");
    }

    @Override
    public String enterCardPin(boolean b) {  //TODO
        return "1111";
    }

    @Override
    public void cardInserted(boolean b) {
        showDialog(TITLE,"Card Inserted");
    }

    @Override
    public void cardReadingFailed(boolean b) {
        showDialog(TITLE,"Card Reading failed");
    }

    @Override
    public void eraseCompleted() {

    }

    @Override
    public void insertCard(boolean b) {
        String prompts;
        if(b){
            prompts = "Please Insert TK Card";
        }
        else{
            prompts = "Please Inser TI Card";
        }
        showDialog(TITLE, prompts);
    }

    @Override
    public void takeCard(boolean b) {
        showDialog(TITLE,"Please Remove Card");
    }


    private void showDialog(final String title, final String message) {
        FinancialApplication.getApp().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(dialog != null){
                    dialog.dismiss();
                    dialog = null;
                }

                dialog = new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setCancelable(true)
                        .create();
                dialog.show();
            }
        });

    }
}

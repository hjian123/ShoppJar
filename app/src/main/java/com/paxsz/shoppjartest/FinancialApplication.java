/*
 * ===========================================================================================
 * = COPYRIGHT
 *          PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or nondisclosure
 *   agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied or
 *   disclosed except in accordance with the terms in that agreement.
 *     Copyright (C) YYYY-? PAX Computer Technology(Shenzhen) CO., LTD All rights reserved.
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20180507 	         lixc           	    Create
 * ===========================================================================================
 */
package com.paxsz.shoppjartest;

import android.app.Application;
import android.os.Handler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FinancialApplication extends Application {

    private static FinancialApplication mApp;
    private ExecutorService backgroundExecutor;
    private Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();

        FinancialApplication.mApp = this;
        handler = new Handler();
        backgroundExecutor = Executors.newFixedThreadPool(10, runnable -> {
            Thread thread = new Thread(runnable, "Background executor service");
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.setDaemon(true);
            return thread;
        });

    }

    public static FinancialApplication getApp() {
        return mApp;
    }

    public void runInBackground(final Runnable runnable) {
        backgroundExecutor.submit(runnable);
    }

    public void runOnUiThreadDelay(final Runnable runnable, long delayMillis) {
        handler.postDelayed(runnable, delayMillis);
    }

    public void runOnUiThread(final Runnable runnable) {
        handler.post(runnable);
    }
}

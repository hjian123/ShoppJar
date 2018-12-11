package com.paxsz.shoppjartest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.paxsz.shooplibrary.api.Payment;

public class MainActivity extends AppCompatActivity {
  //  private Context context = FinancialApplication.getApp();
    private Payment payment;

    static final String TIK = "9AE735B1D4BDBEB26179DDFECF77340E";
    static final String KSN = "FFFF9876543210E00000";
    static final String KCV = "AF8C07";

    static final String key = "98AD40557055F74376FA5078A4E6A654";
    static final String kcv = "81A279";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        payment = Payment.getInstance();
        payment.init(MainActivity.this);

        Button bt1 = (Button)findViewById(R.id.bt1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinancialApplication.getApp().runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        payment.injectKeysByCard(new KeyInjectListenerImpl(MainActivity.this));
                    }
                });

            }
        });

        Button bt2 = (Button)findViewById(R.id.bt2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinancialApplication.getApp().runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            payment.injectTIK(TIK, KSN, KCV);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

        Button bt3 = (Button)findViewById(R.id.bt3);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinancialApplication.getApp().runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            payment.injectTPK(key,kcv,true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

        Button bt4 = (Button)findViewById(R.id.bt4);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinancialApplication.getApp().runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            payment.injectTDK(key,kcv,true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

        Button bt5 = (Button)findViewById(R.id.bt5);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FinancialApplication.getApp().runInBackground(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            payment.injectTAK(key,kcv,true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });


    }
}

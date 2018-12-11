package com.paxsz.shooplibrary.api.entity;


import com.paxsz.shooplibrary.api.enums.EnTxnType;

/**
 * Created by Mohsen Beiranvand on 17/07/28.
 */

public class ECardAndPinParam {

    private String amount;
    private String currency = "364";
    private String trace;
    private int timeout = 120000;
    private String lang = "fa";
    private EnTxnType txnType;

    public ECardAndPinParam(String amount, String currency, String trace, int timeout, EnTxnType txnType) {
        this.amount = amount;
        this.currency = currency;
        this.trace = trace;
        this.timeout = timeout;
        this.txnType = txnType;
    }

    public ECardAndPinParam(String amount, String trace, EnTxnType txnType) {
        this.amount = amount;
        this.trace = trace;
        this.txnType = txnType;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getTrace() {
        return trace;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public EnTxnType getTxnType() {
        return txnType;
    }

    public void setTxnType(EnTxnType txnType) {
        this.txnType = txnType;
    }
}

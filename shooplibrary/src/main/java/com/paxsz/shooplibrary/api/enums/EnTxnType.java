package com.paxsz.shooplibrary.api.enums;

/**
 * Created by Mohsen Beiranvand on 17/07/28.
 */

public enum EnTxnType {

    Purchase(1),
    BillPayment(2),
    BillInquiry(4),
    Balance(3),
    Voucher(5),
    BillInquiryPayment(7),
    Confirm(10),
    Thp(11),
    Topup(15);

    private final int transType;

    EnTxnType(int transType) {
        this.transType = transType;
    }

    public int getTransType() {
        return transType;
    }
}

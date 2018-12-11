package com.paxsz.shooplibrary.api.enums;

/**
 * Created by Mohsen Beiranvand on 17/06/11.
 */

public enum EnDUKPTPinMode {
    /**
     * format 0, KSN auto-add 1
     */
    ISO9564_0_INC,
    /**
     * format 1, KSN auto-add 1
     */
    ISO9564_1_INC,
    /**
     * format 2, KSN auto-add 1
     */
    ISO9564_2_INC,
    /**
     * format EPS, KSN auto-add 1
     */
    HKEPS_INC,
    /**
     * format0 KSN won't auto-add 1
     */
    ISO9564_0,
    /**
     * format1 KSN won't auto-add 1
     */
    ISO9564_1,
    /**
     * format2 KSN won't auto-add 1
     */
    ISO9564_2,
    /**
     * KSN won't auto-add 1
     */
    HKEPS
}

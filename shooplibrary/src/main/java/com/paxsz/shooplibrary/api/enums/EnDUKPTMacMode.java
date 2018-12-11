package com.paxsz.shooplibrary.api.enums;

/**
 * Created by Mohsen Beiranvand on 17/06/11.
 */


public enum EnDUKPTMacMode {
    /**
     * Doing TDES encryption for BLOCK1 by using MAC key. Doing TDES encryption again by using MAC key when and after bitwise XOR the previous encryption result with BLOCK2. Processing in turn to get the 8 bytes encryption result.KSN auto-add 1
     */
    MODE_00,
    /**
     * Doing bitwise XOR for BLOCK1 and BLOCK2; Do bitwise XOR again by using previous XOR result with BLOCK3. Do it in turn and finally get the 8 bytes XOR result. Using MAC key to process TDES encryption for the result.KSN auto-add 1.
     */
    MODE_01,
    /**
     * ANSIX9.19 standard, Do DES encryption for BLOCK1 by using MAC key (only take the first 8 bytes of key). The encryption result wills bitwise XOR with BLOCK2,and then doing DES encryption by using MAC key again. Do it in turn and get the 8 bytes encryption result. Using TDES to encrypt in the last time.KSN auto-add 1.
     */
    MODE_02,
    /**
     * Doing TDES encryption for BLOCK1 by using MAC key. Doing TDES encryption again by using MAC key when and after bitwise XOR the previous encryption result with BLOCK2. Processing in turn to get the 8 bytes encryption result.KSN won't auto-add 1.
     */
    MODE_20,
    /**
     * Doing bitwise XOR for BLOCK1 and BLOCK2; Do bitwise XOR again by using previous XOR result with BLOCK3. Do it in turn and finally get the 8 bytes XOR result. Using MAC key to process TDES encryption for the result.KSN won't auto-add 1.
     */
    MODE_21,
    /**
     * ANSIX9.19 standard, Do DES encryption for BLOCK1 by using MAC key (only take the first 8 bytes of key). The encryption result wills bitwise XOR with BLOCK2,and then doing DES encryption by using MAC key again. Do it in turn and get the 8 bytes encryption result. Using TDES to encrypt in the last time.KSN won't auto-add 1.
     */
    MODE_22,
    /**
     * Doing TDES encryption for BLOCK1 by using MAC key. Doing TDES encryption again by using MAC key when and after bitwise XOR the previous encryption result with BLOCK2. Processing in turn to get the 8 bytes encryption result.KSN won't auto-add 1.
     */
    MODE_40,
    /**
     * Doing bitwise XOR for BLOCK1 and BLOCK2; Do bitwise XOR again by using previous XOR result with BLOCK3. Do it in turn and finally get the 8 bytes XOR result. Using MAC key to process TDES encryption for the result.KSN won't auto-add 1.
     */
    MODE_41,
    /**
     * ANSIX9.19 standard, Do DES encryption for BLOCK1 by using MAC key (only take the first 8 bytes of key). The encryption result wills bitwise XOR with BLOCK2,and then doing DES encryption by using MAC key again. Do it in turn and get the 8 bytes encryption result. Using TDES to encrypt in the last time.KSN won't auto-add 1.
     */
    MODE_42
}

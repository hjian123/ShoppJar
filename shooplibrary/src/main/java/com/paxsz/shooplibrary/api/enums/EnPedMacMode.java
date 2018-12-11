package com.paxsz.shooplibrary.api.enums;

/**
 * Created by Mohsen Beiranvand on 17/06/11.
 */

public enum  EnPedMacMode {
    /**
     * Doing DES/TDES encryption for BLOCK1 by usingMAC key. Doing DES/TDES encryption again by using TAK when and after bitwise XOR the previous encryption result with BLOCK2. Processing in turn to get the 8 bytes encryption result.
     */
    MODE_00,
    /**
     * Doing bitwise XOR for BLOCK1 and BLOCK2; Do bitwise XOR again by using previous XOR result with BLOCK3. Do it in turn and finally get the 8 bytes XOR result. Using TAK to process DES/TDES encryption for the result
     */
    MODE_01,
    /**
     * ANSIX9.19 standard, Do DES encryption for BLOCK1 by using TAK (only take the first 8 bytes of key). The encryption result wills bitwise XOR with BLOCK2,and then doing DES encryption by using TAK again. Do it in turn and get the 8 bytes encryption result. Using DES/TDES to encrypt in the last time.
     */
    MODE_02
}

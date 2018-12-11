package com.paxsz.shooplibrary.api.enums;

/**
 * Created by Mohsen Beiranvand on 17/06/11.
 */

public enum EnPinBlockMode {
    /**
     * DataIn is the 16 bytes primary account number after shifting.
     */
    ISO9564_0,
    /**
     * Input parameters for participation in PinBlock formatting, 8 bytes data. (refer to ISO9564 standard, this data can be Random number, the transaction serial number or time stamp, etc.)
     */
    ISO9564_1,
    /**
     * DataIn is the 16 bytes primary account number after shifting. DataIn+16 point to the 8 bytes data which has participated in PinBlock formatting. (refer to ISO9564 standard, this data can be Random number, the transaction serial number or time stamp,etc. But the higher 4 bits and lower 4 bits of each byte should between 0xA~0xF. So, if the Mode=0x02, the bottom level will do this check for the 8 bytes data, it will return an error if does not meet the requirement.).
     */
    ISO9564_3,
    /**
     * DataIn is ISN [6 Bytes, ASCII code]
     */
    HKPES
}

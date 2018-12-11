package com.paxsz.shooplibrary.api;

import java.util.Arrays;

/**
 * Created by m.beiranvand on 7/17/2017.
 */

public class ApduHelper {

    public static byte[] cmd_selectTkApplet()
    {
        return makeBuffer((byte)0x00,(byte)0xa4,(byte)0x04,(byte)0x00, ByteUtil.hex2byte("a0000002029002"),(byte)0x0);
    }

    public static byte[] cmd_selectTiApplet()
    {
        return makeBuffer((byte)0x00,(byte)0xa4,(byte)0x04,(byte)0x00,ByteUtil.hex2byte("a0000002029001"),(byte)0x0);
    }

    public static byte[] cmd_verifyPin(byte[] pin)
    {
        byte[] pinByte = new byte[4];
        pinByte[0] = (byte) (pin[0] - 0x30);
        pinByte[1] = (byte) (pin[1] - 0x30);
        pinByte[2] = (byte) (pin[2] - 0x30);
        pinByte[3] = (byte) (pin[3] - 0x30);

        return makeBuffer((byte)0x80,(byte)0xA8,(byte)0x00,(byte)0x00,pinByte,(byte)0x0);
    }

    public static byte[] cmd_getKey()
    {
        return makeBuffer((byte)0x80,(byte)0xB5,(byte)0x00,(byte)0x01,"".getBytes(),(byte)0x10);
    }

    public static byte[] cmd_translateKey(byte[] key)
    {
        byte[] tk = Arrays.copyOf(key, 16);
        return makeBuffer((byte)0x80,(byte)0x0A,(byte)0x02,(byte)0x00,tk,(byte)0x10);
    }

    public static byte[] getKTM(byte[] key) throws Exception
    {
        if(key != null)
        {
            byte[] KTM = Arrays.copyOf(key , 16);
            if(KTM != null)
                return KTM;
        }
        throw new Exception("cannot translate key");
    }

    public static byte[] makeBuffer(byte cla,byte ins,byte p1,byte p2,byte[] data,byte le)
    {
        byte[] APDUCommandHeader = {
                cla, // CLA Class
                ins, // INS Instruction
                p1, // P1  Parameter 1
                p2, // P2  Parameter 2
                (byte) data.length, // Length
        };

        byte[] APDUCommand = new byte[APDUCommandHeader.length + data.length + 1];
        System.arraycopy(APDUCommandHeader, 0, APDUCommand, 0, APDUCommandHeader.length);
        System.arraycopy(data, 0, APDUCommand, APDUCommandHeader.length, data.length);

        APDUCommand[APDUCommandHeader.length + data.length] = le;

        return APDUCommand;
    }

    public static EApduResponse unpack(byte[] data)
    {
        EApduResponse response = new EApduResponse();
        String resp = ByteUtil.hexString(data);
        String respStatus = resp.substring(resp.length()-4);
        String respData = resp.substring(0,resp.length()-4);

        response.setData(ByteUtil.hex2byte(respData));
        response.setStatus(ByteUtil.hex2byte(respStatus));
        response.setStatusString(respStatus);

        return response;
    }

    public static class EApduResponse {

        private byte[] data;
        private byte[] status;
        private String statusString;

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public byte[] getStatus() {
            return status;
        }

        public void setStatus(byte[] status) {
            this.status = status;
        }

        public String getStatusString() {
            return statusString;
        }

        public void setStatusString(String statusString) {
            this.statusString = statusString;
        }
    }
}

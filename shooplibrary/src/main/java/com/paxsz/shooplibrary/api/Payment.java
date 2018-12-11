package com.paxsz.shooplibrary.api;

import android.content.Context;

import com.pax.dal.IComm;
import com.pax.dal.IDAL;
import com.pax.dal.IDalCommManager;
import com.pax.dal.IIcc;
import com.pax.dal.IMag;
import com.pax.dal.IPed;
import com.pax.dal.IPrinter;
import com.pax.dal.ISys;
import com.pax.dal.entity.ECheckMode;
import com.pax.dal.entity.EPedKeyType;
import com.pax.dal.entity.EPedType;
import com.pax.dal.entity.ModemParam;
import com.pax.dal.exceptions.IccDevException;
import com.pax.dal.exceptions.PedDevException;
import com.pax.neptunelite.api.NeptuneLiteUser;
import com.paxsz.shooplibrary.api.enums.EnDeviceStatus;
import com.paxsz.shooplibrary.api.listener.IKeyInjectListener;


public class Payment  {

    private static Payment instance;
    private static final String TAG = "PAX.A920";

    private static EnDeviceStatus deviceStatus = EnDeviceStatus.NotReady;

    private static byte TLK_INDX = (byte) 0x01; //load key index
    private static byte TMK_INDX = (byte) 0x01; //master key index
    private static byte TIK_INDX = (byte) 0x02; //initial key index
    private static byte TPK_INDX = (byte) 0x03; //pin key index
    private static byte TDK_INDX = (byte) 0x04; //data key index
    private static byte TAK_INDX = (byte) 0x05; //mac key index

    private static String ALLOWED_PIN = "4,4,4"; //0 : no Pin, 4 or 6 digits, 0,4,6:no pin and 4 or 6 digit is allowed

    private static boolean IsReady = false;

    private static NeptuneLiteUser ppUser;
    private volatile static IDAL dal;
    private static IPed ped;
    private static IMag mag;
    private static IPrinter prn;
    private static IIcc icc;
    private static ISys sys;
    private static IDalCommManager connectionManager;
    private static IComm modemComm;
    private static ModemParam modemParam;


    private  Payment(){

    }

    public static Payment getInstance() {
        if (instance == null) {
            instance = new Payment();
        }
        return instance;
    }

    public synchronized boolean init(Context context) {
        if (dal != null)
            return true;
        try {
            ppUser = NeptuneLiteUser.getInstance();
            dal = ppUser.getDal(context);
            connectionManager = dal.getCommManager();
            ped = dal.getPed(EPedType.INTERNAL);
            mag = dal.getMag();
            prn = dal.getPrinter();
            icc = dal.getIcc();
            sys = dal.getSys();
            mag.reset();
            deviceStatus = EnDeviceStatus.Ready;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void injectTLK(String key, String KCV) throws Exception {
        ped.writeKey(EPedKeyType.TLK, (byte) 0x00, EPedKeyType.TLK, TLK_INDX, ByteUtil.hex2byte(key), ECheckMode.KCV_NONE, null);
    }


    private void injectTMK(String key, String KCV) throws Exception {
        ped.writeKey(EPedKeyType.TMK, (byte) 0x00, EPedKeyType.TMK, TMK_INDX, ByteUtil.hex2byte(key), ECheckMode.KCV_NONE, null);
    }


    public void injectTIK(String key, String KSN, String KCV) throws Exception {
        ped.writeTIK(TIK_INDX, (byte) 0x01, ByteUtil.hex2byte(key), ByteUtil.hex2byte(KSN), ECheckMode.KCV_ENCRYPT_0, ByteUtil.hex2byte(KCV));
    }


    public void injectTPK(String key, String KCV, boolean isDUKPT) throws Exception {
        if (isDUKPT)
            ped.writeKey(EPedKeyType.TLK, TLK_INDX, EPedKeyType.TPK, TPK_INDX, ByteUtil.hex2byte(key), ECheckMode.KCV_NONE, null);
        else
            ped.writeKey(EPedKeyType.TMK, TLK_INDX, EPedKeyType.TPK, TPK_INDX, ByteUtil.hex2byte(key), ECheckMode.KCV_NONE, null);
    }


    public void injectTDK(String key, String KCV, boolean isDUKPT) throws Exception {
        if (isDUKPT)
            ped.writeKey(EPedKeyType.TLK, TLK_INDX, EPedKeyType.TDK, TDK_INDX, ByteUtil.hex2byte(key), ECheckMode.KCV_NONE, null);
        else
            ped.writeKey(EPedKeyType.TMK, TLK_INDX, EPedKeyType.TDK, TDK_INDX, ByteUtil.hex2byte(key), ECheckMode.KCV_NONE, null);
    }


    public void injectTAK(String key, String KCV, boolean isDUKPT) throws Exception {
        if (isDUKPT)
            ped.writeKey(EPedKeyType.TLK, TLK_INDX, EPedKeyType.TAK, TAK_INDX, ByteUtil.hex2byte(key), ECheckMode.KCV_NONE, null);
        else
            ped.writeKey(EPedKeyType.TMK, TLK_INDX, EPedKeyType.TAK, TAK_INDX, ByteUtil.hex2byte(key), ECheckMode.KCV_NONE, null);
    }


    public boolean injectKeysByCard(IKeyInjectListener listener) {

        byte[] TKkey;
        byte[] resp;
        ApduHelper.EApduResponse isoRes;
        byte slot = (byte) 0;
        listener.onErase();
        boolean isTK = true;
        int timeout = 20000;
        int timePass = 0;
        try {
            icc.close(slot);
        } catch (IccDevException e) {
            e.printStackTrace();
        }
        try {
            ped.erase();
            listener.eraseCompleted();
            listener.insertCard(true);

            icc.light(true);

            //start reading tk and get key
            while (!Thread.interrupted()) {
                if (detectIccCard(slot)) {
                    break;
                }
                Thread.sleep(250);
                timePass += 250;
                if (timePass > timeout) {
                    throw new Exception("operation timeout");
                }
            }
            icc.init(slot);
            icc.autoResp(slot, true);
            timePass = 0;
            icc.light(false);

            listener.onCardReading(isTK);
            resp = icc.isoCommand(slot, ApduHelper.cmd_selectTkApplet());
            checkIsoResult(resp);

            String cardPin = listener.enterCardPin(isTK);
            resp = icc.isoCommand(slot, ApduHelper.cmd_verifyPin(cardPin.getBytes()));
            checkIsoResult(resp);

            listener.onCardReading(isTK);
            resp = icc.isoCommand(slot, ApduHelper.cmd_getKey());
            isoRes = checkIsoResult(resp);

            TKkey = isoRes.getData();
            if (TKkey == null) {
                listener.operationFailed();
                throw new Exception("cannot get key from TK");
            }
            icc.light(true);
            listener.takeCard(isTK);
            while (!Thread.interrupted()) {
                if (!detectIccCard(slot)) {
                    break;
                }
                Thread.sleep(250);
                timePass += 250;
                if (timePass > timeout) {
                    listener.operationFailed();
                    throw new Exception("operation timeout");
                }
            }
            timePass = 0;

            //start reading ti and translate key
            isTK = false;
            listener.insertCard(isTK);
            while (!Thread.interrupted()) {
                if (detectIccCard(slot)) {
                    break;
                }
                Thread.sleep(250);
                timePass += 250;
                if (timePass > timeout) {
                    listener.operationFailed();
                    throw new Exception("operation timeout");
                }
            }
            icc.init(slot);
            icc.autoResp(slot, true);
            icc.light(false);

            listener.onCardReading(isTK);
            resp = icc.isoCommand(slot, ApduHelper.cmd_selectTiApplet());
            checkIsoResult(resp);

            cardPin = listener.enterCardPin(isTK);
            resp = icc.isoCommand(slot, ApduHelper.cmd_verifyPin(cardPin.getBytes()));
            checkIsoResult(resp);

            listener.onCardReading(isTK);
            resp = icc.isoCommand(slot, ApduHelper.cmd_translateKey(TKkey));
            isoRes = checkIsoResult(resp);
            String KTM = ByteUtil.hexString(ApduHelper.getKTM(isoRes.getData()));

           // if (isDUKPT == true)
                injectTLK(KTM, null);
           // else
           //     injectTMK(KTM, null);

            listener.operationComplete();
            return true;
        } catch (Exception e) {
            listener.operationFailed();
            e.printStackTrace();
        }

        try {
            icc.light(false);
            icc.close(slot);
        } catch (IccDevException e) {
            e.printStackTrace();
        }

        listener.operationFailed();
        return false;
    }

    private boolean detectIccCard(byte slot) {
        boolean res = false;
        try {
            res = icc.detect(slot);
            return res;
        } catch (IccDevException e) {
            e.printStackTrace();
            return res;
        }
    }

    private ApduHelper.EApduResponse checkIsoResult(byte[] isoResp) throws Exception {
        if (isoResp != null) {
            ApduHelper.EApduResponse apduResp = ApduHelper.unpack(isoResp);
            if (apduResp.getStatusString().equals("9000")) {
                return apduResp;
            } else {
                throw new Exception(apduResp.getStatusString());
            }
        } else {
            throw new Exception("operation failed");
        }
    }


}

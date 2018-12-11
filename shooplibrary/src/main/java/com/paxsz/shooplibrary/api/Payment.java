package com.paxsz.shooplibrary.api;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.pax.dal.IComm;
import com.pax.dal.IDAL;
import com.pax.dal.IDalCommManager;
import com.pax.dal.IDeviceInfo;
import com.pax.dal.IIcc;
import com.pax.dal.IMag;
import com.pax.dal.IPed;
import com.pax.dal.IPrinter;
import com.pax.dal.ISys;
import com.pax.dal.entity.DUKPTResult;
import com.pax.dal.entity.EChannelType;
import com.pax.dal.entity.ECheckMode;
import com.pax.dal.entity.EDUKPTDesMode;
import com.pax.dal.entity.EDUKPTMacMode;
import com.pax.dal.entity.EDUKPTPinMode;
import com.pax.dal.entity.EPedDesMode;
import com.pax.dal.entity.EPedKeyType;
import com.pax.dal.entity.EPedMacMode;
import com.pax.dal.entity.EPedType;
import com.pax.dal.entity.EPinBlockMode;
import com.pax.dal.entity.ETermInfoKey;
import com.pax.dal.entity.ModemParam;
import com.pax.dal.entity.TrackData;
import com.pax.dal.exceptions.CommException;
import com.pax.dal.exceptions.IccDevException;
import com.pax.dal.exceptions.MagDevException;
import com.pax.dal.exceptions.PedDevException;
import com.pax.dal.exceptions.PrinterDevException;
import com.pax.neptunelite.api.NeptuneLiteUser;
import com.paxsz.shooplibrary.api.entity.ECardAndPinParam;
import com.paxsz.shooplibrary.api.entity.EDUKPTResult;
import com.paxsz.shooplibrary.api.entity.ETrackData;
import com.paxsz.shooplibrary.api.enums.EnDeviceStatus;
import com.paxsz.shooplibrary.api.enums.EnPedDesMode;
import com.paxsz.shooplibrary.api.enums.EnPrinterStatus;
import com.paxsz.shooplibrary.api.listener.IKeyInjectListener;
import com.paxsz.shooplibrary.api.listener.IPosCardPinEvent;
import com.paxsz.shooplibrary.api.listener.IPosChangeChannelProcess;
import com.paxsz.shooplibrary.api.listener.IPosPrinterEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


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

    private AsyncTask readCardAndPin;


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

    public EnDeviceStatus getStatus() {
        return deviceStatus;
    }


    public Map<String, String> getModuleSupported() {

        Map<String, String> support = new HashMap<String, String>();
        String KEYBOARD = null;
        String PRINTER = null;
        try {
            Map<Integer, IDeviceInfo.ESupported> supportedMap = dal.getDeviceInfo().getModuleSupported();
            KEYBOARD = supportedMap.get(IDeviceInfo.MODULE_KEYBOARD).toString();
            PRINTER = supportedMap.get(IDeviceInfo.MODULE_PRINTER).toString();
        } catch (Exception ex) {
        }
        String model="";
        try {
            model = sys.getTermInfo().get(ETermInfoKey.MODEL);
        }catch (Exception ex){}

        if (model.equals("A920")||model.equals("a920")) {
            support.put("KEYBOARD", "NO");
            support.put("PRINTER", "YES");

        }else
        {

            if (KEYBOARD != null) {
                support.put("KEYBOARD", KEYBOARD);
            } else {
                support.put("KEYBOARD", "NO");
            }

            if (PRINTER != null) {

                support.put("PRINTER", PRINTER);
            } else {
                support.put("PRINTER", "NO");
            }

        }


        return support;
    }




    public ArrayList<String> getConnectionSupported(){

        ArrayList<String> support=new ArrayList<>();

   /* IChannel channel= connectionManager.getChannel(EChannelType.MODEM);

        try {
            channel.enable();
            channel.isEnabled();
        } catch (ChannelException e) {

            Log.e("ChannelException",e.getMessage()+e.getErrMsg());
            e.printStackTrace();
        }*/

        String model="";
        try {
            model = sys.getTermInfo().get(ETermInfoKey.MODEL);
        }catch (Exception ex){}

        if (model.equals("A920")||model.equals("a920")||model.equals("A930")||model.equals("a930"))
        {
            support.add("GPRS");
            support.add("WIFI");

        }else if (model.equals("A80")||model.equals("a80"))
        {
            support.add("ETHERNET");
            support.add("WIFI");
            support.add("HDLC");
        }




        return support;
    }


    public int enableConnectionChannel(IPosChangeChannelProcess process, String type)
    {

        int status = -100;
        process.onStart();
        Log.e(">>>enableConChannel", type);
        boolean b= connectionManager.enableMultiPath();
        Log.e(">>>enableConChannel", b + "");


        if (1==0)
        {

            if (type.equals("GPRS"))
                status = connectionManager.enableChannelExclusive(EChannelType.MOBILE, 10);
            else if (type.equals("WIFI"))
                status = connectionManager.enableChannelExclusive(EChannelType.WIFI, 10);
            else if (type.equals("ETHERNET"))
                status = connectionManager.enableChannelExclusive(EChannelType.LAN, 10);
            else if (type.equals("HDLC"))
                status = connectionManager.enableChannelExclusive(EChannelType.MODEM, 10);
        }


        Log.e(">>>enableConChannel", status + "");
        if (status!=-1)
            process.onFinish();
        else
            process.OnError();
        return status;


    }

    public void modemDial(Properties modemProperties){

        try {
            modemComm.reset();
            modemComm.disconnect();

        }catch (Exception ex){}
        // if (modemComm.getConnectStatus() == IComm.EConnectStatus.DISCONNECTED) {
        try {
            //   modemComm.disconnect();
//                modemComm.reset();
            modemParam = new ModemParam();
            modemParam.setDp(0);
            modemParam.setDt1(20);
            modemParam.setDt2(0);
            modemParam.setHt(70);
            modemParam.setWt(5);
            modemParam.setChdt(0);
            modemParam.setDelayTime(1);
            modemParam.setTimeout(6);
            modemParam.setAsyncMode(0);
            modemParam.setSsetup(5);
            modemParam.setDialBlocking(true);
            modemParam.setTelNo1("88361693");
            //  modemParam.setTelNo1("09123473699");
            modemComm = connectionManager.getModemComm(modemParam);
            modemComm.connect();
        } catch (CommException e) {
            e.printStackTrace();
        }


        while (connectionManager.getModemStatus() != 0) {
            Log.e("modemDial", "not Connected!");
        }
        Log.e("Connected!","\n-----------------------------------------");
        Log.e("Connected!",connectionManager.getModemStatus()+"");
    }


    public int modemGetStatus(){

        return connectionManager.getModemStatus();
    }



    public void modemHangOff() {

        try {
            //    if (modemComm.getConnectStatus() == IComm.EConnectStatus.CONNECTED)
            {
                // modemComm.reset();
                //   modemComm.cancelRecv();
                modemComm.disconnect();
            }
        } catch (CommException e) {
            e.printStackTrace();
        }

    }


    public void modemSendData(byte[] data,int timeout) {


        try {
            //     if (modemComm.getConnectStatus() == IComm.EConnectStatus.CONNECTED)
            {
                if (timeout>0)
                    modemComm.setSendTimeout(timeout);




                modemComm.send(data);
            }


        } catch (CommException e) {
            e.printStackTrace();
        }

    }



    public byte[] modemRecvData(int len,int timeout)
    {

        try {
            //  if (modemComm.getConnectStatus() == IComm.EConnectStatus.CONNECTED)
            if (timeout > 0)
                modemComm.setRecvTimeout(timeout);

            byte recv[];
            while (true) {

                recv = modemComm.recvNonBlocking();
               /* if (len>0)
                    return modemComm.recv(len);
                else
                    return */

                Log.e("modemRecvData=",recv.length +"");
                if (recv.length > 0)
                    break;

            }

            return recv;
        } catch (CommException e) {
            e.printStackTrace();
        }


        return null;
    }




    @Nullable
    public String getSerial() {

        try {
            Map<ETermInfoKey, String> terminalInfo = dal.getSys().getTermInfo();
            return terminalInfo.get(ETermInfoKey.SN).trim();
        } catch (Exception exceptions) {
            exceptions.printStackTrace();
        }
        return null;
    }

    @Nullable
    public String getPAVersion() {
        return "V1.00.00";
    }


    public void restart() {

    }


    public void onPause() {
        try {
            if (mag != null)
                mag.close();
        } catch (MagDevException e) {
            e.printStackTrace();
        }
    }


    public void onResume() {

    }


    public void readCardAndPin(ECardAndPinParam cardAndPinParam, IPosCardPinEvent cardPinEvent, boolean isDUKPT) {
        readCardAndPin = new ReadCardAndPin(cardPinEvent,isDUKPT).execute();
    }


    public void cancelReadCardAndPin() {

        if (readCardAndPin != null)
            readCardAndPin.cancel(true);
        readCardAndPin = null;
    }

    public void showAlert(String message) {

    }


    public void secureResponse(String secureResponse) {

    }


    public void setDateTime(Date dateTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        String date = dateFormat.format(dateTime);
        System.out.printf("Device date set to: " + date);
        dal.getSys().setDate(date);
    }


    @Nullable

    public ETrackData readMagCard(int timeOutMs) {

        ETrackData trackDataResult = new ETrackData();
        try {
            //mag.reset();

            mag.open();

            while (!Thread.interrupted()) {
                if (mag.isSwiped()) {
                    TrackData trackData = null;
                    try {
                        trackData = mag.read();
                    } catch (Exception e) {
                        continue;
                    }
                    if (trackData == null || trackData.getResultCode() == 0) {
                        continue;
                    }
                    if ((trackData.getResultCode() & 0x01) == 0x01) {
                        trackDataResult.setTrack1(trackData.getTrack1());
                    }
                    if ((trackData.getResultCode() & 0x02) == 0x02) {
                        trackDataResult.setTrack2(trackData.getTrack2());
                        String[] Track2 = trackDataResult.getTrack2().split("=");
                        trackDataResult.setPAN(Track2[0]);
                    }
                    if ((trackData.getResultCode() & 0x04) == 0x04) {
                        trackDataResult.setTrack3(trackData.getTrack3());
                    }
                    if (trackDataResult.getTrack2() == null) {
                        mag.reset();
                        continue;
                    }
                    break;
                }
                Thread.sleep(250);

            }

        } catch (MagDevException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            mag.close();
        } catch (MagDevException e) {
            e.printStackTrace();
        }

        return trackDataResult;
    }


    public void resetMagCard() {

    }


    public void cancelReadCard() {

    }


    public void pinpadErase() throws Exception {
        ped.erase();
    }

    @Nullable
    public byte[] getPinBlock(String dataIn, int timeOut) throws Exception {

        byte[] dataInArray = dataIn.substring(dataIn.length() - 13, dataIn.length() - 1).getBytes();
        final byte[] data = new byte[16];
        data[0] = 0x00;
        data[1] = 0x00;
        data[2] = 0x00;
        data[3] = 0x00;
        ped.setIntervalTime(3000, 0);
        System.arraycopy(dataInArray, 0, data, 4, dataInArray.length);
        return ped.getPinBlock( TPK_INDX, ALLOWED_PIN, data, EPinBlockMode.ISO9564_0, timeOut);
    }

    @Nullable
    public EDUKPTResult getDUKPTPin(String dataIn, int timeoutMs) throws Exception {
        byte[] dataInArray = dataIn.substring(dataIn.length() - 13, dataIn.length() - 1).getBytes();
        final byte[] data = new byte[16];
        data[0] = 0x00;
        data[1] = 0x00;
        data[2] = 0x00;
        data[3] = 0x00;
        System.arraycopy(dataInArray, 0, data, 4, dataInArray.length);

        ped.setIntervalTime(3000, 0);

        EDUKPTResult result = new EDUKPTResult();
        DUKPTResult dresult = ped.getDUKPTPin(TIK_INDX, ALLOWED_PIN, data, EDUKPTPinMode.ISO9564_0_INC, timeoutMs);

        result.setKSN(dresult.getKsn());
        result.setResult(dresult.getResult());

        return result;

    }

    @Nullable
    public String getPinpadSN() {

        return null;
    }



    @Nullable
    public String inputStr(byte mode, byte min, byte max, int timeoutMs) {
        return null;
    }

    public void showStr(byte x, byte y, String message) {

    }

    public void clearScreen() {

    }

    public boolean isInjected() {
        try {
            calcDes(new byte[8], EnPedDesMode.ENCRYPT);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasMasterKey(boolean isDUKPT) {

        if (isDUKPT) {
            try {

                byte[] sampleKey = new byte[16];
                ped.writeKey(EPedKeyType.TLK, TLK_INDX, EPedKeyType.TAK, (byte) 0x10, sampleKey, ECheckMode.KCV_NONE, null);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else
        {
            try {

                byte[] sampleKey = new byte[16];
                ped.writeKey(EPedKeyType.TMK, TLK_INDX, EPedKeyType.TAK, (byte) 0x10, sampleKey, ECheckMode.KCV_NONE, null);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    @Nullable
    public String getPedSN() {
        try {
            return ped.getSN();
        } catch (PedDevException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public String getPedVersion() {
        return null;
    }

    @Nullable
    public byte[] getMac(byte[] dataIn) throws Exception {
        return ped.getMac(TAK_INDX, dataIn, EPedMacMode.MODE_02);
    }

    @Nullable
    public EDUKPTResult getDUPKTMac(byte[] dataIn) {
        EDUKPTResult result = new EDUKPTResult();
        try {
            DUKPTResult dresult = ped.getDUPKTMac(TAK_INDX, dataIn, EDUKPTMacMode.MODE_00);
            result.setKSN(dresult.getKsn());
            result.setResult(dresult.getResult());
            return result;
        } catch (Exception exceptions) {
            exceptions.printStackTrace();
        }
        return null;
    }

    @Nullable
    public byte[] calcDes(byte[] dataIn, EnPedDesMode mode) throws Exception {
        return ped.calcDes(TDK_INDX, dataIn, (mode.equals(EnPedDesMode.DECRYPT) ? (EPedDesMode.DECRYPT) : (EPedDesMode.ENCRYPT)));
    }

    @Nullable
    public EDUKPTResult calcDUKPTDes(byte[] dataIn, EnPedDesMode mode) {
        EDUKPTResult result = new EDUKPTResult();
        try {
            DUKPTResult res = ped.calcDUKPTDes(TIK_INDX, (byte) 0x02, null, dataIn, EDUKPTDesMode.ECB_ENCRYPTION);
            result.setResult(res.getResult());
            result.setKSN(res.getKsn());
        } catch (Exception exceptions) {
            exceptions.printStackTrace();
        }
        return result;
    }

    @Nullable
    public byte[] getKSN() {
        return new byte[0];
    }

    public void incDUKPTKsn() {

    }

    public void setExternalMode(int mode) {

    }


    public void print(Bitmap bitmap, @Nullable IPosPrinterEvent printerEvent) {

        Integer[] paperError = new Integer[]{240, 2, 4, 8};

        try {
            prn.init();

            if (prn.getStatus() != 0) {
                if (printerEvent != null)
                    printerEvent.onPrinterError("Printer not ready", false);
                return;
            }

            prn.printBitmap(bitmap);
            if (printerEvent != null)
                printerEvent.onPrintStarted();
            prn.start();
            int printerStatus = prn.getStatus();
            while (printerStatus == 1) {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                printerStatus = prn.getStatus();
            }
            if (printerEvent != null)
                if (printerStatus == 0) {
                    printerEvent.onPrintEnd();
                } else {
                    printerEvent.onPrinterError("error : " + printerStatus,
                            Arrays.asList(paperError).contains(printerStatus));
                }
        } catch (PrinterDevException e) {
            e.printStackTrace();
            if (printerEvent != null)
                printerEvent.onPrinterError(e.getMessage(), false);
        }

    }

    public EnPrinterStatus printerStatus() {
        try {
            prn.init();
            int status = prn.getStatus();

            switch (status) {
                case 0:
                    return EnPrinterStatus.Ready;
                case 1:
                    return EnPrinterStatus.Busy;
                case 2:
                    return EnPrinterStatus.PaperError;
                default:
                    return EnPrinterStatus.Error;
            }
        } catch (PrinterDevException e) {
            e.printStackTrace();
        }
        return EnPrinterStatus.Error;
    }


    private class ReadCardAndPin extends AsyncTask {
        private final IPosCardPinEvent cardPinEvent;
        private final boolean isDUKPT;

        public ReadCardAndPin(IPosCardPinEvent cardPinEvent,boolean isDUKPT) {
            this.cardPinEvent = cardPinEvent;
            this.isDUKPT = isDUKPT;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                if (deviceStatus.equals(EnDeviceStatus.Ready)) {
                    cardPinEvent.onPreCardRead();

                    resetMagCard();
                    ETrackData trackData = readMagCard(60000);
                    if (trackData != null && trackData.getTrack2() != null) {
                        try {
                            /*
                            EDUKPTResult cDesRes = calcDUKPTDes(
                                    indentString4Encrypt(trackData.getTrack2(), StringUtil.DEFAULT_ENCODE),
                                    EnPedDesMode.ENCRYPT);
                            if(cDesRes.getResult()!=null)
                                trackData.setEncTrack2(ByteUtil.hexString(cDesRes.getResult()));
                            */

                            cardPinEvent.onCardRead();
                        } catch (Exception e) {
                            e.printStackTrace();
                            cardPinEvent.onCardFailed("Read Card Error", e);
                            return false;
                        }

                        try {

                            cardPinEvent.onPrePinRead();

                            if (isDUKPT) {
                                EDUKPTResult pinData = getDUKPTPin(trackData.getPAN(), 60000);

                                trackData.setKSN(ByteUtil.hexString(pinData.getKSN()));
                                trackData.setPinBlock(ByteUtil.hexString(pinData.getResult()));
                            } else
                                trackData.setPinBlock(ByteUtil.hexString(getPinBlock(trackData.getPAN(), 60000)));


                            cardPinEvent.onPinRead();

                            cardPinEvent.onCardPinReadSucceed(trackData);
                            return true;

                        } catch (Exception e) {
                            e.printStackTrace();
                            cardPinEvent.onPinFailed("Read PIN Error", e);
                        }
                    } else {
                        cardPinEvent.onCardFailed("Read Card Error", null);
                    }
                } else {
                    cardPinEvent.onDeviceNotReady();
                }
            } catch (Exception e) {
                e.printStackTrace();
                cardPinEvent.onCardPinReadFailed("Fatal error!!", e);
            }
            return false;
        }

        @Override
        protected void onCancelled() {

            Log.e("ReadCard", "Cancelled");
            try {
                mag.close();
            } catch (MagDevException e) {
                e.printStackTrace();
            }

        }

    }
}

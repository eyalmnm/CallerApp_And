package com.em_projects.callerapp.telephony;

import android.content.Context;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.em_projects.callerapp.config.Constants;

import java.util.HashMap;
import java.util.List;

/**
 * Created by eyal muchtar on 31/07/2017.
 */

public class TelephonyUtils {
    private static final String TAG = "TelephonyUtils";

    private static TelephonyManager telephonyManager;

    public static HashMap<String, Integer> getTelephonyData(Context context) {
        if (null == telephonyManager) {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }

        List<CellInfo> cellInfos = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                cellInfos = telephonyManager.getAllCellInfo();
                if (null == cellInfos) {
                    int type = telephonyManager.getPhoneType();
                    if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                        GsmCellLocation c = (GsmCellLocation) telephonyManager.getCellLocation();
                        HashMap<String, Integer> cellDataHashMap = new HashMap<>();
                        String mcc_mnc = telephonyManager.getNetworkOperator();
                        cellDataHashMap.put(Constants.mcc, Integer.parseInt(mcc_mnc.substring(0, 3)));
                        cellDataHashMap.put(Constants.mnc, Integer.parseInt(mcc_mnc.substring(3, mcc_mnc.length())));
                        if (c != null) {
                            cellDataHashMap.put(Constants.lac, c.getLac());
                            cellDataHashMap.put(Constants.cid, c.getCid());
                            cellDataHashMap.put(Constants.psc, c.getPsc());
                        }
                        cellDataHashMap.put(Constants.radio, telephonyManager.getNetworkType()); // "GSM";
                        return cellDataHashMap;
                    } else if (type == TelephonyManager.NETWORK_TYPE_EVDO_A || type == TelephonyManager.NETWORK_TYPE_CDMA || type == TelephonyManager.NETWORK_TYPE_1xRTT) {
                        CdmaCellLocation location = (CdmaCellLocation) telephonyManager.getCellLocation();
                        HashMap<String, Integer> cellDataHashMap = new HashMap<>();
                        cellDataHashMap.put(Constants.cid, location.getBaseStationId());
                        cellDataHashMap.put(Constants.lac, location.getNetworkId());
                        cellDataHashMap.put(Constants.mcc, Integer.parseInt(telephonyManager.getNetworkOperator().substring(0, 3)));
                        cellDataHashMap.put(Constants.mnc, location.getNetworkId());
                        cellDataHashMap.put(Constants.radio, telephonyManager.getNetworkType()); // "CDMA";
                        return cellDataHashMap;
                    }
                } else if (0 < cellInfos.size()) {
                    for (CellInfo cellInfo : cellInfos) {
                        if (cellInfo instanceof CellInfoLte) {

                            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                            HashMap<String, Integer> cellDataHashMap = new HashMap<>();
                            if (!cellInfoLte.isRegistered()) continue;
                            cellDataHashMap.put(Constants.mcc, cellInfoLte.getCellIdentity().getMcc());
                            cellDataHashMap.put(Constants.mnc, cellInfoLte.getCellIdentity().getMnc());
                            cellDataHashMap.put(Constants.dbm, cellInfoLte.getCellSignalStrength().getDbm());
                            cellDataHashMap.put(Constants.lvl, cellInfoLte.getCellSignalStrength().getLevel());
                            cellDataHashMap.put(Constants.psc, cellInfoLte.getCellIdentity().getPci());
                            cellDataHashMap.put(Constants.lac, cellInfoLte.getCellIdentity().getTac());
                            cellDataHashMap.put(Constants.cid, cellInfoLte.getCellIdentity().getCi());
                            cellDataHashMap.put(Constants.cell_identity, cellInfoLte.getCellIdentity().getCi());
                            cellDataHashMap.put(Constants.radio, telephonyManager.getNetworkType()); // "CDMA";
                            return cellDataHashMap;
                        } else if (cellInfo instanceof CellInfoWcdma) {
                            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
                            HashMap<String, Integer> cellDataHashMap = new HashMap<>();
                            if (!cellInfoWcdma.isRegistered()) continue;
                            cellDataHashMap.put(Constants.mcc, cellInfoWcdma.getCellIdentity().getMcc());
                            cellDataHashMap.put(Constants.mnc, cellInfoWcdma.getCellIdentity().getMnc());
                            cellDataHashMap.put(Constants.dbm, cellInfoWcdma.getCellSignalStrength().getDbm());
                            cellDataHashMap.put(Constants.lvl, cellInfoWcdma.getCellSignalStrength().getLevel());
                            cellDataHashMap.put(Constants.lac, cellInfoWcdma.getCellIdentity().getLac());
                            cellDataHashMap.put(Constants.cid, cellInfoWcdma.getCellIdentity().getCid());
                            cellDataHashMap.put(Constants.psc, cellInfoWcdma.getCellIdentity().getPsc());
                            cellDataHashMap.put(Constants.cell_identity, cellInfoWcdma.getCellIdentity().getCid());
                            cellDataHashMap.put(Constants.radio, telephonyManager.getNetworkType()); // "CDMA";
                            return cellDataHashMap;
                        } else if (cellInfo instanceof CellInfoCdma) {
                            CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfo;
                            if (!cellInfoCdma.isRegistered()) continue;
                            HashMap<String, Integer> cellDataHashMap = new HashMap<>();
                            cellDataHashMap.put(Constants.mcc, cellInfoCdma.getCellIdentity().getNetworkId());
                            cellDataHashMap.put(Constants.mnc, cellInfoCdma.getCellIdentity().getSystemId());
                            cellDataHashMap.put(Constants.dbm, cellInfoCdma.getCellSignalStrength().getDbm());
                            cellDataHashMap.put(Constants.lvl, cellInfoCdma.getCellSignalStrength().getLevel());
                            cellDataHashMap.put(Constants.cell_identity, cellInfoCdma.getCellIdentity().getBasestationId());
                            cellDataHashMap.put(Constants.radio, telephonyManager.getNetworkType()); // "CDMA";
                            return cellDataHashMap;

                        } else if (cellInfo instanceof CellInfoGsm) {
                            CellInfoGsm cellInfoGSM = (CellInfoGsm) cellInfo;
                            if (!cellInfoGSM.isRegistered()) continue;
                            HashMap<String, Integer> cellDataHashMap = new HashMap<>();
                            cellDataHashMap.put(Constants.mcc, cellInfoGSM.getCellIdentity().getMcc());
                            cellDataHashMap.put(Constants.mnc, cellInfoGSM.getCellIdentity().getMnc());
                            cellDataHashMap.put(Constants.dbm, cellInfoGSM.getCellSignalStrength().getDbm());
                            cellDataHashMap.put(Constants.lvl, cellInfoGSM.getCellSignalStrength().getLevel());
                            cellDataHashMap.put(Constants.psc, cellInfoGSM.getCellIdentity().getPsc());
                            cellDataHashMap.put(Constants.cell_identity, cellInfoGSM.getCellIdentity().getCid());
                            cellDataHashMap.put(Constants.radio, telephonyManager.getNetworkType()); // "CDMA";
                            return cellDataHashMap;
                        }
                    }
                }
            }
        } catch (SecurityException e) {
            Log.e(TAG, "informServerCallEnded", e);
        }


        return null;
    }
}

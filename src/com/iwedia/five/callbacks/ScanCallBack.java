package com.iwedia.five.callbacks;

import android.util.Log;

import com.iwedia.dtv.scan.IScanCallback;
import com.iwedia.dtv.scan.ScanInstallStatus;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.five.dtv.DVBManager;

/**
 * CallBack for Channel Actions.
 */
public class ScanCallBack implements IScanCallback {
    private static final String TAG = "ScanCallBack";
    private DVBManager mDVBManager = null;
    private static ScanCallBack sInstance = null;

    public static ScanCallBack getInstance() {
        if (sInstance == null) {
            sInstance = new ScanCallBack();
        }
        return sInstance;
    }

    public static void destroyInstance() {
        sInstance = null;
    }

    private ScanCallBack() {
        mDVBManager = DVBManager.getInstance();
    }

    @Override
    public void antennaConnected(int arg0, boolean status) {
        Log.d(TAG, "antennaConnected: " + status + ", ROUTE: " + arg0);
        if (mDVBManager != null) {
            mDVBManager.antennaStateChanged(status, arg0);
        }
    }

    @Override
    public void installServiceDATAName(int arg0, String arg1) {
    }

    @Override
    public void installServiceDATANumber(int arg0, int arg1) {
    }

    @Override
    public void installServiceRADIOName(int arg0, String arg1) {
    }

    @Override
    public void installServiceRADIONumber(int arg0, int arg1) {
    }

    @Override
    public void installServiceTVName(int arg0, String arg1) {
    }

    @Override
    public void installServiceTVNumber(int arg0, int arg1) {
    }

    @Override
    public void installStatus(ScanInstallStatus arg0) {
    }

    @Override
    public void networkChanged(int arg0) {
    }

    @Override
    public void sat2ipServerDropped(int arg0) {
    }

    @Override
    public void scanFinished(int arg0) {
    }

    @Override
    public void scanNoServiceSpace(int arg0) {
    }

    @Override
    public void scanProgressChanged(int arg0, int arg1) {
    }

    @Override
    public void scanTunFrequency(int arg0, int arg1) {
    }

    @Override
    public void signalBer(int arg0, int arg1) {
    }

    @Override
    public void signalQuality(int arg0, int arg1) {
    }

    @Override
    public void signalStrength(int arg0, int arg1) {
    }

    @Override
    public void triggerStatus(int arg0) {
    }
}

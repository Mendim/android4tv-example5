package com.iwedia.five.dtv;

import com.iwedia.dtv.pvr.IPvrCallback;
import com.iwedia.dtv.pvr.IPvrControl;
import com.iwedia.dtv.pvr.TimeshiftInfo;
import com.iwedia.dtv.types.InternalException;

public class PvrManager {
    private IPvrControl mPvrControl;
    private int mPvrSpeed = PvrSpeedMode.PVR_SPEED_PAUSE;
    private int mSpeedIndexBackward = 0, mSpeedIndexForward = 0;
    private IPvrCallback mPvrCallback;
    private boolean timeShftActive = false, pvrActive = false;
    private static PvrManager instance = null;

    protected static PvrManager getInstance(IPvrControl pvrControl) {
        if (instance == null) {
            instance = new PvrManager(pvrControl);
        }
        return instance;
    }

    private PvrManager(IPvrControl pvrControl) {
        mPvrControl = pvrControl;
    }

    public void startTimeShift() throws InternalException {
        resetSpeedIndexes();
        mPvrControl.startTimeshift(DVBManager.getInstance()
                .getPlaybackRouteIDMain());
    }

    public void stopTimeShift() throws InternalException {
        resetSpeedIndexes();
        mPvrControl.stopTimeshift(DVBManager.getInstance()
                .getPlaybackRouteIDMain(), false);
    }

    public TimeshiftInfo getTimeShiftInfo() {
        return mPvrControl.getTimeshiftInfo(DVBManager.getInstance()
                .getPlaybackRouteIDMain());
    }

    public int getTimeShiftBufferSize() {
        return mPvrControl.getTimeshiftBufferSize();
    }

    public void fastForward() {
        mSpeedIndexBackward = 0;
        if (mSpeedIndexForward < PvrSpeedMode.SPEED_ARRAY_FORWARD.length) {
            setPvrSpeed(PvrSpeedMode.SPEED_ARRAY_FORWARD[mSpeedIndexForward]);
            mSpeedIndexForward++;
        }
    }

    public void rewind() {
        mSpeedIndexForward = 0;
        if (mSpeedIndexBackward < PvrSpeedMode.SPEED_ARRAY_REWIND.length) {
            setPvrSpeed(PvrSpeedMode.SPEED_ARRAY_REWIND[mSpeedIndexBackward]);
            mSpeedIndexBackward++;
        }
    }

    /**
     * Change PVR/Timeshift playback speed
     * 
     * @param speed
     *        use {@link PvrSpeedMode} constants
     */
    public void setPvrSpeed(int speed) {
        mPvrSpeed = speed;
        mPvrControl.controlSpeed(DVBManager.getInstance()
                .getPlaybackRouteIDMain(), speed);
    }

    public void registerPvrCallback(IPvrCallback callback) {
        mPvrCallback = callback;
        mPvrControl.registerCallback(callback);
    }

    public void unregisterPvrCallback() {
        if (mPvrCallback != null) {
            mPvrControl.unregisterCallback(mPvrCallback);
        }
    }

    /**
     * Starts one touch PVR
     * 
     * @throws InternalException
     */
    public void startOneTouchRecord() throws InternalException {
        resetSpeedIndexes();
        mPvrControl.createOnTouchRecord(DVBManager.getInstance()
                .getCurrentRecordRoute(), DVBManager.getInstance()
                .getCurrentLiveRoute() == DVBManager.getInstance()
                .getLiveRouteIp() ? 0 : (DVBManager.getInstance()
                .getCurrentChannelNumber() + (DVBManager.getInstance()
                .isIpAndSomeOtherTunerType() ? 1 : 0)));
    }

    public void stopPvr() {
        resetSpeedIndexes();
        mPvrControl.destroyRecord(0);
    }

    public void resetSpeedIndexes() {
        mSpeedIndexBackward = 0;
        mSpeedIndexForward = 0;
    }

    public int getPvrSpeed() {
        return mPvrSpeed;
    }

    public void setmPvrSpeedConst(int pvrSpeed) {
        this.mPvrSpeed = pvrSpeed;
    }

    public boolean isTimeShftActive() {
        return timeShftActive;
    }

    public void setTimeShftActive(boolean timeShftActive) {
        this.timeShftActive = timeShftActive;
    }

    public boolean isPvrActive() {
        return pvrActive;
    }

    public void setPvrActive(boolean pvrActive) {
        this.pvrActive = pvrActive;
    }
}

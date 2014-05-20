/*
 * Copyright (C) 2014 iWedia S.A. Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.iwedia.five.dtv;

import com.iwedia.dtv.pvr.IPvrCallback;
import com.iwedia.dtv.pvr.IPvrControl;
import com.iwedia.dtv.pvr.MediaInfo;
import com.iwedia.dtv.pvr.TimeshiftInfo;
import com.iwedia.dtv.types.InternalException;

import java.util.ArrayList;

/**
 * Class for PVR related functions.
 */
public class PvrManager {
    private IPvrControl mPvrControl;
    private int mPvrSpeed = PvrSpeedMode.PVR_SPEED_PAUSE;
    private int mSpeedIndexBackward = 0, mSpeedIndexForward = 0;
    private IPvrCallback mPvrCallback;
    private boolean timeShftActive = false, pvrActive = false,
            pvrPlaybackActive = false;
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

    /**
     * Starts timeshift operation.
     * 
     * @throws InternalException
     */
    public void startTimeShift() throws InternalException {
        resetSpeedIndexes();
        mPvrControl.startTimeshift(DVBManager.getInstance()
                .getPlaybackRouteIDMain());
    }

    /**
     * Stops timeshift operation.
     * 
     * @throws InternalException
     */
    public void stopTimeShift() throws InternalException {
        resetSpeedIndexes();
        mPvrControl.stopTimeshift(DVBManager.getInstance()
                .getPlaybackRouteIDMain(), false);
    }

    /**
     * Returns timeshift playback information.
     * 
     * @return Timeshift playback info.
     */
    public TimeshiftInfo getTimeShiftInfo() {
        return mPvrControl.getTimeshiftInfo(DVBManager.getInstance()
                .getPlaybackRouteIDMain());
    }

    /**
     * Returns size of timeshift buffer.
     * 
     * @return Size of timeshift buffer.
     */
    public int getTimeShiftBufferSize() {
        return mPvrControl.getTimeshiftBufferSize();
    }

    /**
     * Changes playback speed.
     */
    public void fastForward() {
        mSpeedIndexBackward = 0;
        if (mSpeedIndexForward < PvrSpeedMode.SPEED_ARRAY_FORWARD.length) {
            setPvrSpeed(PvrSpeedMode.SPEED_ARRAY_FORWARD[mSpeedIndexForward]);
            mSpeedIndexForward++;
        }
    }

    /**
     * Changes playback speed.
     */
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

    /**
     * Registers PVR callback.
     * 
     * @param callback
     *        Callback to register.
     */
    public void registerPvrCallback(IPvrCallback callback) {
        mPvrCallback = callback;
        mPvrControl.registerCallback(callback);
    }

    /**
     * Unregisters PVR callback.
     */
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

    /**
     * Stops one touch PVR recording.
     */
    public void stopPvr() {
        resetSpeedIndexes();
        mPvrControl.destroyRecord(0);
    }

    /**
     * Reset speed related indexes.
     */
    public void resetSpeedIndexes() {
        mSpeedIndexBackward = 0;
        mSpeedIndexForward = 0;
    }

    /**
     * Retrieves list of recorded media.
     * 
     * @return List of recorded media.
     */
    public ArrayList<MediaInfo> getPvrRecordings() {
        ArrayList<MediaInfo> records = new ArrayList<MediaInfo>();
        int numberOfMediaRecords = mPvrControl.updateMediaList();
        for (int i = 0; i < numberOfMediaRecords; i++) {
            records.add(mPvrControl.getMediaInfo(i));
        }
        return records;
    }

    /**
     * Delete PVR record.
     * 
     * @param index
     *        of record to delete.
     */
    public void deleteRecord(int index) {
        mPvrControl.deleteMedia(index);
    }

    /**
     * Starts PVR playback.
     * 
     * @param recordIndex
     *        Index of record to play.
     * @throws InternalException
     */
    public void startPlayback(int recordIndex) throws InternalException {
        mPvrControl.startPlayback(DVBManager.getInstance()
                .getPlaybackRouteIDMain(), recordIndex);
    }

    /**
     * Stops PVR playback.
     * 
     * @throws InternalException
     */
    public void stopPlayback() throws InternalException {
        mPvrControl.stopPlayback(DVBManager.getInstance()
                .getPlaybackRouteIDMain());
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

    public boolean isPvrPlaybackActive() {
        return pvrPlaybackActive;
    }

    public void setPvrPlaybackActive(boolean pvrPlaybackActive) {
        this.pvrPlaybackActive = pvrPlaybackActive;
    }
}

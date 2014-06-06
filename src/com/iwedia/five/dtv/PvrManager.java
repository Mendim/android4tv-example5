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

import android.view.SurfaceView;

import com.iwedia.dtv.audio.AudioTrack;
import com.iwedia.dtv.audio.IAudioControl;
import com.iwedia.dtv.display.IDisplayControl;
import com.iwedia.dtv.display.SurfaceBundle;
import com.iwedia.dtv.dtvmanager.IDTVManager;
import com.iwedia.dtv.pvr.IPvrCallback;
import com.iwedia.dtv.pvr.IPvrControl;
import com.iwedia.dtv.pvr.MediaInfo;
import com.iwedia.dtv.pvr.PvrRecordType;
import com.iwedia.dtv.pvr.PvrSortMode;
import com.iwedia.dtv.pvr.PvrSortOrder;
import com.iwedia.dtv.pvr.TimeshiftInfo;
import com.iwedia.dtv.subtitle.ISubtitleControl;
import com.iwedia.dtv.subtitle.SubtitleMode;
import com.iwedia.dtv.subtitle.SubtitleTrack;
import com.iwedia.dtv.teletext.ITeletextControl;
import com.iwedia.dtv.teletext.TeletextTrack;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.dtv.types.UserControl;

import java.util.ArrayList;
import java.util.Locale;

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
    private MediaInfo mCurrentRecord = null;
    private ITeletextControl mTeletextControl;
    private ISubtitleControl mSubtitleControl;
    private IAudioControl mAudioControl;
    private IDisplayControl mDisplayControl;
    private SurfaceView mSurfaceView = null;
    private boolean subtitleActive = false, teletextActive = false;
    private static PvrManager instance = null;

    protected static PvrManager getInstance(IDTVManager mDTVManager) {
        if (instance == null) {
            instance = new PvrManager(mDTVManager);
        }
        return instance;
    }

    private PvrManager(IDTVManager mDTVManager) {
        mPvrControl = mDTVManager.getPvrControl();
        mDisplayControl = mDTVManager.getDisplayControl();
        mTeletextControl = mDTVManager.getTeletextControl();
        mSubtitleControl = mDTVManager.getSubtitleControl();
        mAudioControl = mDTVManager.getAudioControl();
    }

    /**
     * Initialize teletext and subtitle drawing surface.
     * 
     * @param surfaceView
     *        to send to teletext and subtitle engine.
     * @param screenWidth
     *        Width of screen.
     * @param screenHeight
     *        Height of screen.
     * @throws IllegalArgumentException
     * @throws InternalException
     */
    public void initializeSubtitleAndTeletextDisplay(SurfaceView surfaceView)
            throws InternalException {
        mSurfaceView = surfaceView;
        SurfaceBundle surfaceBundle = new SurfaceBundle();
        surfaceBundle.setSurface(surfaceView.getHolder().getSurface());
        mDisplayControl.setVideoLayerSurface(1, surfaceBundle);
    }

    /**
     * Shows teletext dialog and send command to middleware to start drawing
     * 
     * @throws InternalException
     */
    public boolean showTeletext(int trackIndex) throws InternalException {
        mTeletextControl.setCurrentTeletextTrack(DVBManager.getInstance()
                .getPlaybackRouteIDMain(), trackIndex);
        if (mTeletextControl.getCurrentTeletextTrackIndex(DVBManager
                .getInstance().getPlaybackRouteIDMain()) >= 0) {
            teletextActive = true;
        }
        return teletextActive;
    }

    /**
     * Hide teletext
     * 
     * @throws InternalException
     */
    public void hideTeletext() throws InternalException {
        mTeletextControl.deselectCurrentTeletextTrack(DVBManager.getInstance()
                .getPlaybackRouteIDMain());
        if (mTeletextControl.getCurrentTeletextTrackIndex(DVBManager
                .getInstance().getPlaybackRouteIDMain()) < 0) {
            teletextActive = false;
        }
    }

    /**
     * Starts timeshift operation.
     * 
     * @throws InternalException
     */
    public void startTimeShift() throws InternalException {
        resetSpeedIndexes();
        mPvrSpeed = PvrSpeedMode.PVR_SPEED_PAUSE;
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
        mPvrSpeed = PvrSpeedMode.PVR_SPEED_PAUSE;
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
     * Sets PVR media path.
     * 
     * @param mediaPath
     *        Path to set.
     */
    public void setMediaPath(String mediaPath) {
        mPvrControl.setDevicePath(mediaPath);
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
     * Retrieves list of scheduled records.
     * 
     * @return List of scheduled records.
     */
    public ArrayList<Object> getPvrScheduledRecords() {
        ArrayList<Object> records = new ArrayList<Object>();
        int numberOfMediaRecords = mPvrControl.updateRecordList();
        for (int i = 0; i < numberOfMediaRecords; i++) {
            PvrRecordType type = mPvrControl.getRecordType(i);
            if (type == PvrRecordType.ONTOUCH) {
                records.add(mPvrControl.getOnTouchInfo(i));
            } else if (type == PvrRecordType.SMART) {
                records.add(mPvrControl.getSmartInfo(i));
            } else {
                records.add(mPvrControl.getTimerInfo(i));
            }
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
     * Delete scheduled PVR record.
     * 
     * @param index
     *        of scheduled record to delete.
     */
    public void deleteScheduledRecord(int index) {
        mPvrControl.destroyRecord(index);
    }

    /**
     * Starts PVR playback.
     * 
     * @param recordIndex
     *        Index of record to play.
     * @throws InternalException
     */
    public void startPlayback(int recordIndex) throws InternalException {
        resetSpeedIndexes();
        mPvrSpeed = PvrSpeedMode.PVR_SPEED_FORWARD_X1;
        mCurrentRecord = getPvrRecordings().get(recordIndex);
        mPvrControl.startPlayback(DVBManager.getInstance()
                .getPlaybackRouteIDMain(), recordIndex);
    }

    /**
     * Stops PVR playback.
     * 
     * @throws InternalException
     */
    public void stopPlayback() throws InternalException {
        resetSpeedIndexes();
        mPvrSpeed = PvrSpeedMode.PVR_SPEED_PAUSE;
        mCurrentRecord = null;
        /**
         * Hide teletext and subtitles if it is opened
         */
        if (isTeletextActive()) {
            hideTeletext();
        } else if (isSubtitleActive()) {
            hideSubtitles();
        }
        mPvrControl.stopPlayback(DVBManager.getInstance()
                .getPlaybackRouteIDMain());
    }

    /**
     * Sets desired sort mode.
     * 
     * @param order
     *        New sort mode to set.
     */
    public void setSortMode(PvrSortMode mode) {
        mPvrControl.setMediaListSortMode(mode);
    }

    /**
     * Returns active sort mode.
     */
    public PvrSortMode getSortMode() {
        return mPvrControl.getMediaListSortMode();
    }

    /**
     * Sets desired sort mode.
     * 
     * @param order
     *        New sort mode to set.
     */
    public void setScheduledSortMode(PvrSortMode mode) {
        mPvrControl.setRecordListSortMode(mode);
    }

    /**
     * Returns active sort mode.
     */
    public PvrSortMode getScheduledSortMode() {
        return mPvrControl.getRecordListSortMode();
    }

    /**
     * Sets desired sort order.
     * 
     * @param order
     *        New sort order to set.
     */
    public void setSortOrder(PvrSortOrder order) {
        mPvrControl.setMediaListSortOrder(order);
    }

    /**
     * Returns active sort order.
     */
    public PvrSortOrder getSortOrder() {
        return mPvrControl.getMediaListSortOrder();
    }

    /**
     * Sets desired sort order.
     * 
     * @param order
     *        New sort order to set.
     */
    public void setScheduledSortOrder(PvrSortOrder order) {
        mPvrControl.setRecordListSortOrder(order);
    }

    /**
     * Returns active sort order.
     */
    public PvrSortOrder getScheduledSortOrder() {
        return mPvrControl.getRecordListSortOrder();
    }

    /**
     * Returns teletext track by index.
     */
    public TeletextTrack getTeletextTrack(int index) {
        return mTeletextControl.getTeletextTrack(DVBManager.getInstance()
                .getPlaybackRouteIDMain(), index);
    }

    /**
     * Send pressed keycode to teletext engine.
     */
    public void sendTeletextInputCommand(int keyCode) {
        mTeletextControl.sendInputControl(DVBManager.getInstance()
                .getPlaybackRouteIDMain(), UserControl.PRESSED, keyCode);
    }

    /**
     * Get teletext track count.
     * 
     * @return Number of teletext tracks.
     */
    public int getTeletextTrackCount() {
        return mTeletextControl.getTeletextTrackCount(DVBManager.getInstance()
                .getPlaybackRouteIDMain());
    }

    /**
     * Convert teletext track type to human readable format.
     * 
     * @param type
     *        Teletext track type.
     * @return Converted string.
     */
    public String convertTeletextTrackTypeToHumanReadableFormat(int type) {
        switch (type) {
            case 1: {
                return "TTXT NORMAL";
            }
            case 2: {
                return "TTXT SUB";
            }
            case 3: {
                return "TTXT INFO";
            }
            case 4: {
                return "TTXT PROGRAM SCHEDULE";
            }
            case 5: {
                return "TTXT SUB HOH";
            }
            default:
                return "UNKNOWN";
        }
    }

    /**
     * Convert subtitle track mode to human readable format.
     * 
     * @param type
     *        Subtitle track mode.
     * @return Converted string.
     */
    public String convertSubtitleTrackModeToHumanReadableFormat(int modeIndex) {
        SubtitleMode mode = SubtitleMode.getFromValue(modeIndex);
        if (mode == SubtitleMode.TRANSLATION) {
            return "NORMAL";
        } else if (mode == SubtitleMode.HEARING_IMPAIRED) {
            return "HOH";
        }
        return "";
    }

    /**
     * Show subtitles on screen.
     * 
     * @param trackIndex
     *        Subtitle track to show.
     * @return True if subtitle is started, false otherwise.
     * @throws InternalException
     */
    public boolean showSubtitles(int trackIndex) throws InternalException {
        mSubtitleControl.setCurrentSubtitleTrack(DVBManager.getInstance()
                .getPlaybackRouteIDMain(), trackIndex);
        if (mSubtitleControl.getCurrentSubtitleTrackIndex(DVBManager
                .getInstance().getPlaybackRouteIDMain()) >= 0) {
            subtitleActive = true;
        }
        return subtitleActive;
    }

    /**
     * Hide started subtitle.
     * 
     * @throws InternalException
     */
    public void hideSubtitles() throws InternalException {
        mSubtitleControl.deselectCurrentSubtitleTrack(DVBManager.getInstance()
                .getPlaybackRouteIDMain());
        if (mSubtitleControl.getCurrentSubtitleTrackIndex(DVBManager
                .getInstance().getPlaybackRouteIDMain()) < 0) {
            subtitleActive = false;
        }
    }

    /**
     * Returns subtitle track by index.
     */
    public SubtitleTrack getSubtitleTrack(int index) {
        return mSubtitleControl.getSubtitleTrack(DVBManager.getInstance()
                .getPlaybackRouteIDMain(), index);
    }

    /**
     * Get subtitle track count.
     * 
     * @return Number of subtitle tracks.
     */
    public int getSubtitlesTrackCount() {
        return mSubtitleControl.getSubtitleTrackCount(DVBManager.getInstance()
                .getPlaybackRouteIDMain());
    }

    /**
     * Returns number of audio tracks for current channel.
     */
    public int getAudioLanguagesTrackCount() {
        return mAudioControl.getAudioTrackCount(DVBManager.getInstance()
                .getPlaybackRouteIDMain());
    }

    /**
     * Returns audio track by index.
     */
    public AudioTrack getAudioLanguage(int index) {
        return mAudioControl.getAudioTrack(DVBManager.getInstance()
                .getPlaybackRouteIDMain(), index);
    }

    /**
     * Sets audio track with desired index as active.
     */
    public void setAudioTrack(int index) throws InternalException {
        mAudioControl.setCurrentAudioTrack(DVBManager.getInstance()
                .getPlaybackRouteIDMain(), index);
    }

    /**
     * Returns TRUE if subtitle is active, FALSE otherwise.
     */
    public boolean isSubtitleActive() {
        if (mSubtitleControl.getCurrentSubtitleTrackIndex(DVBManager
                .getInstance().getPlaybackRouteIDMain()) < 0) {
            subtitleActive = false;
        } else {
            subtitleActive = true;
        }
        return subtitleActive;
    }

    /**
     * Returns TRUE if teletext is active, FALSE otherwise.
     */
    public boolean isTeletextActive() {
        if (mTeletextControl.getCurrentTeletextTrackIndex(DVBManager
                .getInstance().getPlaybackRouteIDMain()) < 0) {
            teletextActive = false;
        } else {
            teletextActive = true;
        }
        return teletextActive;
    }

    /**
     * @return Avalable audio languages for current service. If they are not
     *         available, it returns null.
     */
    public static String convertTrigramsToLanguage(String language) {
        String languageToDisplay;
        languageToDisplay = checkTrigrams(language);
        if (languageToDisplay.contains(" ")) {
            int indexOfSecondWord = languageToDisplay.indexOf(" ") + 1;
            languageToDisplay = languageToDisplay.substring(0, 1).toUpperCase(
                    new Locale(languageToDisplay))
                    + languageToDisplay.substring(1, indexOfSecondWord)
                    + languageToDisplay.substring(indexOfSecondWord,
                            indexOfSecondWord + 1).toUpperCase()
                    + languageToDisplay.substring(indexOfSecondWord + 1);
        } else {
            languageToDisplay = languageToDisplay.substring(0, 1).toUpperCase()
                    + languageToDisplay.substring(1);
        }
        return languageToDisplay;
    }

    /**
     * We must fix comedia and Android OS trigrams mismatch
     */
    private static String checkTrigrams(String language) {
        if (language.equals("fre")) {
            language = "fra";
        } else if (language.equals("sve")) {
            language = "swe";
        } else if (language.equals("dut") || language.equals("nla")) {
            language = "nl";
        } else if (language.equals("ger")) {
            language = "deu";
        } else if (language.equals("alb")) {
            language = "sqi";
        } else if (language.equals("arm")) {
            language = "hye";
        } else if (language.equals("baq")) {
            language = "eus";
        } else if (language.equals("chi")) {
            language = "zho";
        } else if (language.equals("cze")) {
            language = "ces";
        } else if (language.equals("per")) {
            language = "fas";
        } else if (language.equals("gae")) {
            language = "gla";
        } else if (language.equals("geo")) {
            language = "kat";
        } else if (language.equals("gre")) {
            language = "ell";
        } else if (language.equals("ice")) {
            language = "isl";
        } else if (language.equals("ice")) {
            language = "isl";
        } else if (language.equals("mac") || language.equals("mak")) {
            language = "mk";
        } else if (language.equals("may")) {
            language = "msa";
        } else if (language.equals("rum")) {
            language = "ron";
        } else if (language.equals("scr")) {
            language = "sr";
        } else if (language.equals("slo")) {
            language = "slk";
        } else if (language.equals("esl") || language.equals("esp")) {
            language = "spa";
        } else if (language.equals("wel")) {
            language = "cym";
        }
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        String languageToDisplay = Locale.getDefault().getDisplayLanguage();
        if (languageToDisplay.equals("qaa")) {
            languageToDisplay = "Original";
        }
        if (languageToDisplay.equals("mul")) {
            languageToDisplay = "Multiple";
        }
        if (languageToDisplay.equals("und")) {
            languageToDisplay = "Undefined";
        }
        return languageToDisplay;
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

    public MediaInfo getCurrentRecord() {
        return mCurrentRecord;
    }
}

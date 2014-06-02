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
package com.iwedia.five;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.iwedia.dtv.audio.AudioTrack;
import com.iwedia.dtv.pvr.IPvrCallback;
import com.iwedia.dtv.pvr.MediaInfo;
import com.iwedia.dtv.pvr.PvrEventMediaAdd;
import com.iwedia.dtv.pvr.PvrEventMediaRemove;
import com.iwedia.dtv.pvr.PvrEventPlaybackJump;
import com.iwedia.dtv.pvr.PvrEventPlaybackPosition;
import com.iwedia.dtv.pvr.PvrEventPlaybackSpeed;
import com.iwedia.dtv.pvr.PvrEventPlaybackStart;
import com.iwedia.dtv.pvr.PvrEventPlaybackStop;
import com.iwedia.dtv.pvr.PvrEventRecordAdd;
import com.iwedia.dtv.pvr.PvrEventRecordConflict;
import com.iwedia.dtv.pvr.PvrEventRecordPosition;
import com.iwedia.dtv.pvr.PvrEventRecordRemove;
import com.iwedia.dtv.pvr.PvrEventRecordResourceIssue;
import com.iwedia.dtv.pvr.PvrEventRecordStart;
import com.iwedia.dtv.pvr.PvrEventRecordStop;
import com.iwedia.dtv.pvr.PvrEventTimeshiftJump;
import com.iwedia.dtv.pvr.PvrEventTimeshiftPosition;
import com.iwedia.dtv.pvr.PvrEventTimeshiftSpeed;
import com.iwedia.dtv.pvr.PvrEventTimeshiftStart;
import com.iwedia.dtv.pvr.PvrEventTimeshiftStop;
import com.iwedia.dtv.reminder.IReminderCallback;
import com.iwedia.dtv.reminder.ReminderEventAdd;
import com.iwedia.dtv.reminder.ReminderEventRemove;
import com.iwedia.dtv.reminder.ReminderEventTrigger;
import com.iwedia.dtv.subtitle.SubtitleTrack;
import com.iwedia.dtv.teletext.TeletextTrack;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.five.dtv.ChannelInfo;
import com.iwedia.five.dtv.IPService;
import com.iwedia.five.dtv.PvrManager;
import com.iwedia.five.dtv.PvrSpeedMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PvrActivity extends DTVActivity {
    public static final String TAG = "MainActivity";
    /** URI For VideoView. */
    public static final String TV_URI = "tv://";
    /** Channel Number/Name View Duration in Milliseconds. */
    public static final int CHANNEL_VIEW_DURATION = 5000;
    /** Views needed in activity. */
    private LinearLayout mChannelContainer = null;
    private TextView mChannelNumber = null;
    private TextView mChannelName = null;
    /** Handler for sending action messages to update UI. */
    private UiHandler mHandler = null;
    /** Subtitle and teletext views */
    private SurfaceView mSurfaceView;
    private ChannelListDialog mChannelListDialog;
    private RecordListDialog mRecordListDialog;
    private ScheduledRecordListDialog mScheduledRecordListDialog;
    private ReminderListDialog mReminderListDialog;
    private AlertDialog mReminderDialog;
    /**
     * PVR and Time shift stuff.
     */
    private View mPvrInfoContainer;
    /** PVR callback */
    private IPvrCallback mPvrCallback = new IPvrCallback() {
        @Override
        public void eventTimeshiftStop(PvrEventTimeshiftStop arg0) {
            Log.d(TAG, "eventTimeshiftStop");
            mHandler.removeMessages(UiHandler.HIDE_PVR_INFO_MESSAGE);
            mHandler.sendEmptyMessageDelayed(UiHandler.HIDE_PVR_INFO_MESSAGE,
                    CHANNEL_VIEW_DURATION / 5);
            mDVBManager.getPvrManager().setTimeShftActive(false);
            try {
                mDVBManager.changeChannelByNumber(DTVActivity
                        .getLastWatchedChannelIndex());
            } catch (InternalException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void eventTimeshiftStart(PvrEventTimeshiftStart arg0) {
            Log.d(TAG, "eventTimeshiftStart");
            mDVBManager.getPvrManager().setTimeShftActive(true);
            Message.obtain(
                    mHandler,
                    UiHandler.REFRESH_PLAYBACK_SPEED,
                    PvrSpeedMode.converSpeedToString(mDVBManager
                            .getPvrManager().getPvrSpeed())).sendToTarget();
        }

        @Override
        public void eventTimeshiftSpeed(PvrEventTimeshiftSpeed arg0) {
            Log.d(TAG, "eventTimeshiftSpeed CHANGED " + arg0.getSpeed());
            mDVBManager.getPvrManager().setmPvrSpeedConst(arg0.getSpeed());
            Message.obtain(mHandler, UiHandler.REFRESH_PLAYBACK_SPEED,
                    PvrSpeedMode.converSpeedToString(arg0.getSpeed()))
                    .sendToTarget();
        }

        @Override
        public void eventTimeshiftPosition(PvrEventTimeshiftPosition arg0) {
            Log.d(TAG,
                    "TIME SHIFT PLAYBACK TIME POSITION CHANGED "
                            + arg0.getPlaybackTimePosition());
            Log.d(TAG,
                    "TIME SHIFT PLAYBACK SPACE POSITION CHANGED "
                            + arg0.getPlaybackSpacePosition());
            Log.d(TAG,
                    "TIME SHIFT RECORD SPACE POSITION CHANGED "
                            + arg0.getRecordSpacePosition());
            Log.d(TAG,
                    "TIME SHIFT RECORD TIME POSITION CHANGED "
                            + arg0.getRecordTimePosition());
            Log.d(TAG, "TIME SHIFT IS BEGIN " + arg0.isPlaybackBegin());
            Log.d(TAG, "TIME SHIFT IS END " + arg0.isPlaybackEnd());
            if (arg0.isPlaybackBegin()) {
                mDVBManager.getPvrManager().resetSpeedIndexes();
                mDVBManager.getPvrManager().setPvrSpeed(
                        PvrSpeedMode.PVR_SPEED_FORWARD_X1);
            } else if (arg0.isPlaybackEnd()) {
                mDVBManager.getPvrManager().resetSpeedIndexes();
                mDVBManager.getPvrManager().setPvrSpeed(
                        PvrSpeedMode.PVR_SPEED_FORWARD_X1);
            }
            /**
             * Calculate end time
             */
            // int timeshiftBufferSize = mDVBManager.getTimeShiftBufferSize();
            int endTime = 100;
            if (arg0.getRecordTimePosition() > endTime - 20) {
                endTime = arg0.getRecordTimePosition() + 20;
            }
            // int endTime = timeshiftBufferSize
            // * (arg0.getRecordTimePosition() - arg0
            // .getRecordTimePosition());
            mHandler.sendMessage(Message.obtain(mHandler,
                    UiHandler.REFRESH_TIMESHIFT_PLAYBACK,
                    new PvrTimeShiftPositionHolder(endTime, arg0)));
        }

        @Override
        public void eventTimeshiftJump(PvrEventTimeshiftJump arg0) {
        }

        @Override
        public void eventRecordStop(PvrEventRecordStop arg0) {
            Log.d(TAG, "\n\n\nRECORD STOPPED");
            mHandler.removeMessages(UiHandler.HIDE_PVR_INFO_MESSAGE);
            mHandler.sendEmptyMessageDelayed(UiHandler.HIDE_PVR_INFO_MESSAGE,
                    CHANNEL_VIEW_DURATION / 5);
            PvrActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PvrActivity.this, "Record stopped",
                            Toast.LENGTH_SHORT).show();
                }
            });
            mDVBManager.getPvrManager().setPvrActive(false);
        }

        @Override
        public void eventRecordStart(PvrEventRecordStart arg0) {
            Log.d(TAG, "\n\n\nRECORD STARTED");
            PvrActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PvrActivity.this, "Record started",
                            Toast.LENGTH_SHORT).show();
                }
            });
            mDVBManager.getPvrManager().setPvrActive(true);
            Message.obtain(mHandler, UiHandler.REFRESH_PLAYBACK_SPEED,
                    PvrSpeedMode.converSpeedToString(-1)).sendToTarget();
        }

        @Override
        public void eventRecordResourceIssue(PvrEventRecordResourceIssue arg0) {
        }

        @Override
        public void eventRecordRemove(PvrEventRecordRemove arg0) {
            Log.d(TAG, "\n\n\nRECORD REMOVED: " + arg0.getTitle());
        }

        @Override
        public void eventRecordPosition(PvrEventRecordPosition arg0) {
            Log.d(TAG, "PVR TIME POSITION CHANGED " + arg0.getTimePosition());
            Log.d(TAG, "PVR SPACE POSITION CHANGED " + arg0.getSpacePosition());
            mHandler.sendMessage(Message.obtain(mHandler,
                    UiHandler.REFRESH_PVR_PLAYBACK, arg0));
        }

        @Override
        public void eventRecordConflict(PvrEventRecordConflict arg0) {
        }

        @Override
        public void eventRecordAdd(PvrEventRecordAdd arg0) {
        }

        @Override
        public void eventPlaybackStop(PvrEventPlaybackStop arg0) {
            Log.d(TAG, "\n\n\nRECORD EVENT PLAYBACK STOPPED: ");
            mHandler.removeMessages(UiHandler.HIDE_PVR_INFO_MESSAGE);
            mHandler.sendEmptyMessageDelayed(UiHandler.HIDE_PVR_INFO_MESSAGE,
                    CHANNEL_VIEW_DURATION / 5);
            mDVBManager.getPvrManager().setPvrPlaybackActive(false);
            Message.obtain(mHandler, UiHandler.SHOW_RECORDS_DIALOG,
                    mRecordListDialog).sendToTarget();
        }

        @Override
        public void eventPlaybackStart(PvrEventPlaybackStart arg0) {
            Log.d(TAG, "\n\n\nRECORD EVENT PLAYBACK STARTED: ");
            mDVBManager.getPvrManager().setPvrPlaybackActive(true);
            Message.obtain(mHandler, UiHandler.HIDE_RECORDS_DIALOG,
                    mRecordListDialog).sendToTarget();
            Message.obtain(
                    mHandler,
                    UiHandler.REFRESH_PLAYBACK_SPEED,
                    PvrSpeedMode.converSpeedToString(mDVBManager
                            .getPvrManager().getPvrSpeed())).sendToTarget();
        }

        @Override
        public void eventPlaybackSpeed(PvrEventPlaybackSpeed arg0) {
            Log.d(TAG, "\n\n\nRECORD EVENT PLAYBACK SPEED: " + arg0.getSpeed());
            mDVBManager.getPvrManager().setmPvrSpeedConst(arg0.getSpeed());
            Message.obtain(mHandler, UiHandler.REFRESH_PLAYBACK_SPEED,
                    PvrSpeedMode.converSpeedToString(arg0.getSpeed()))
                    .sendToTarget();
        }

        @Override
        public void eventPlaybackPosition(
                PvrEventPlaybackPosition pvrEventPlaybackPosition) {
            Log.d(TAG, "\n\n\nRECORD EVENT PLAYBACK POSITION: "
                    + pvrEventPlaybackPosition.getTimePosition());
            if (pvrEventPlaybackPosition.isBegin()) {
                mDVBManager.getPvrManager().resetSpeedIndexes();
                mDVBManager.getPvrManager().setPvrSpeed(
                        PvrSpeedMode.PVR_SPEED_FORWARD_X1);
            } else if (pvrEventPlaybackPosition.isEnd()) {
                try {
                    mDVBManager.getPvrManager().stopPlayback();
                } catch (InternalException e) {
                    e.printStackTrace();
                }
            } else {
                MediaInfo playBackRecord = mDVBManager.getPvrManager()
                        .getCurrentRecord();
                Message.obtain(
                        mHandler,
                        UiHandler.REFRESH_PVR_RECORD_PLAYBACK,
                        new PvrPlaybackPositionHolder(playBackRecord,
                                pvrEventPlaybackPosition)).sendToTarget();
            }
        }

        @Override
        public void eventPlaybackJump(PvrEventPlaybackJump arg0) {
        }

        @Override
        public void eventMediaRemove(PvrEventMediaRemove arg0) {
            Log.d(TAG, "\n\n\nRECORD EVENT MEDIA REMOVE: " + arg0.getTitle());
        }

        @Override
        public void eventMediaAdd(PvrEventMediaAdd arg0) {
            Log.d(TAG, "\n\n\nRECORD EVENT MEDIA ADD: " + arg0.getTitle());
        }

        @Override
        public void eventDeviceError() {
        }
    };
    private IReminderCallback mReminderCallback = new IReminderCallback() {
        @Override
        public void reminderTrigger(ReminderEventTrigger arg0) {
            Log.d(TAG, "\n\n\nREMINDER TRIGGERED: " + arg0.getTitle());
            ChannelInfo info = mDVBManager.getChannelInfo(arg0
                    .getServiceIndex()
                    - (mDVBManager.isIpAndSomeOtherTunerType() ? 1 : 0));
            mReminderDialog.setMessage(arg0.getTitle() + " - ["
                    + info.getName() + "]");
            Message.obtain(mHandler, UiHandler.REMINDER_TRIGGERED,
                    mReminderDialog).sendToTarget();
        }

        @Override
        public void reminderRemove(ReminderEventRemove arg0) {
            Log.d(TAG, "\n\n\nREMINDER REMOVED: " + arg0.getTitle());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PvrActivity.this, "Reminder removed!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void reminderAdd(ReminderEventAdd arg0) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /** Initialize VideoView. */
        initializeVideoView();
        /** Initialize Channel Container. */
        initializeChannelContainer();
        /** Initialize PVR container */
        initializePvrInfoContainer();
        /** Initialize surface view. */
        initializeSurfaceView();
        /** Load default IP channel list. */
        initIpChannels();
        /** Initialize channel list dialog. */
        initializeDialogs();
        /** Initialize Handler. */
        mHandler = new UiHandler(mChannelContainer, mPvrInfoContainer,
                mSurfaceView);
        // /** Start DTV. */
        // try {
        // mDVBManager.startDTV(getLastWatchedChannelIndex());
        // } catch (IllegalArgumentException e) {
        // Toast.makeText(
        // this,
        // "Cant play service with index: "
        // + getLastWatchedChannelIndex(), Toast.LENGTH_SHORT)
        // .show();
        // } catch (InternalException e) {
        // /** Error with service connection. */
        // finishActivity();
        // }
        mDVBManager.getPvrManager().registerPvrCallback(mPvrCallback);
        mDVBManager.getReminderManager().registerCallback(mReminderCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /** Refresh surface view */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    mDVBManager.getPvrManager()
                            .initializeSubtitleAndTeletextDisplay(mSurfaceView);
                } catch (InternalException e) {
                    e.printStackTrace();
                }
                refreshSurfaceView(mSurfaceView);
            }
        }, 700);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mDVBManager.stopDTV();
        } catch (InternalException e) {
            e.printStackTrace();
        }
        sIpChannels = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_scan_usb: {
                ArrayList<IPService> ipChannels = new ArrayList<IPService>();
                loadIPChannelsFromExternalStorage(ipChannels);
                sIpChannels = ipChannels;
                return true;
            }
            case R.id.menu_records: {
                mRecordListDialog.show();
                return true;
            }
            case R.id.menu_scheduled_records: {
                mScheduledRecordListDialog.show();
                return true;
            }
            case R.id.menu_reminders: {
                mReminderListDialog.show();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initialize IP
     */
    private void initIpChannels() {
        ContextWrapper contextWrapper = new ContextWrapper(this);
        String path = contextWrapper.getFilesDir() + "/"
                + DTVActivity.IP_CHANNELS;
        sIpChannels = new ArrayList<IPService>();
        DTVActivity.readFile(this, path, sIpChannels);
    }

    /**
     * Initialize VideoView and Set URI.
     * 
     * @return Instance of VideoView.
     */
    private void initializeVideoView() {
        VideoView videoView = ((VideoView) findViewById(R.id.videoView));
        videoView.setVideoURI(Uri.parse(TV_URI));
        videoView.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });
        videoView.start();
    }

    /**
     * Initialize surface view.
     */
    private void initializeSurfaceView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        mSurfaceView.setVisibility(View.VISIBLE);
        mSurfaceView.setZOrderOnTop(true);
    }

    /**
     * Initialize dialogs with channel list and PVR recordings.
     */
    private void initializeDialogs() {
        mChannelListDialog = new ChannelListDialog(this);
        mRecordListDialog = new RecordListDialog(this);
        mScheduledRecordListDialog = new ScheduledRecordListDialog(this);
        mReminderListDialog = new ReminderListDialog(this);
        OnCancelListener listener = new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                refreshSurfaceView(mSurfaceView);
            }
        };
        mChannelListDialog.setOnCancelListener(listener);
        mRecordListDialog.setOnCancelListener(listener);
        mScheduledRecordListDialog.setOnCancelListener(listener);
        mReminderListDialog.setOnCancelListener(listener);
        /**
         * Initialize alert dialog.
         */
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Reminder triggered");
        builderSingle.setNegativeButton("Close",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        mReminderDialog = builderSingle.create();
    }

    /**
     * Initialize LinearLayout and TextViews.
     */
    private void initializeChannelContainer() {
        mChannelContainer = (LinearLayout) findViewById(R.id.linearlayout_channel_container);
        mChannelContainer.setVisibility(View.GONE);
        mChannelNumber = (TextView) findViewById(R.id.textview_channel_number);
        mChannelName = (TextView) findViewById(R.id.textview_channel_name);
    }

    /**
     * Initialize PVR information layout.
     */
    private void initializePvrInfoContainer() {
        mPvrInfoContainer = findViewById(R.id.pvr_info_container);
    }

    /**
     * Show Channel Name and Number of Current Channel on Channel Change.
     * 
     * @param channelInfo
     *        Object for displaying.
     */
    private void showChannelInfo(ChannelInfo channelInfo) {
        if (channelInfo != null) {
            mChannelNumber.setText("" + channelInfo.getNumber());
            mChannelName.setText(channelInfo.getName());
            mChannelContainer.setVisibility(View.VISIBLE);
            mHandler.removeMessages(UiHandler.HIDE_CHANNEL_INFO_VIEW_MESSAGE);
            mHandler.sendEmptyMessageDelayed(
                    UiHandler.HIDE_CHANNEL_INFO_VIEW_MESSAGE,
                    CHANNEL_VIEW_DURATION);
        }
    }

    /**
     * Shows PVR information layout.
     */
    private void showPvrInfo() {
        mPvrInfoContainer.setVisibility(View.VISIBLE);
        // mHandler.removeMessages(UiHandler.HIDE_PVR_INFO_MESSAGE);
        // mHandler.sendEmptyMessageDelayed(UiHandler.HIDE_PVR_INFO_MESSAGE,
        // CHANNEL_VIEW_DURATION);
    }

    /**
     * Listener For Keys.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "KEY PRESSED " + keyCode);
        if (mDVBManager.getPvrManager().isPvrActive() && !isPvrKey(keyCode)) {
            Toast.makeText(this, "Not supported in PVR!", Toast.LENGTH_SHORT)
                    .show();
            return true;
        }
        if (mDVBManager.getPvrManager().isTimeShftActive()
                && !isTimeShiftKey(keyCode)) {
            Toast.makeText(this, "Not supported in time shift!",
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        if (mDVBManager.getPvrManager().isPvrPlaybackActive()
                && !isPvrPlaybackKey(keyCode)) {
            Toast.makeText(this, "Not supported in PVR playback!",
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        /**
         * Disable non Teletext keys
         */
        if (mDVBManager.getPvrManager().isPvrPlaybackActive()
                && mDVBManager.getPvrManager().isTeletextActive()
                && !isTeletextKey(keyCode)) {
            return true;
        }
        switch (keyCode) {
        /** Open Channel List. */
            case KeyEvent.KEYCODE_DPAD_CENTER: {
                mChannelListDialog.show();
                return true;
            }
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
            case KeyEvent.KEYCODE_PROG_RED:
            case KeyEvent.KEYCODE_PROG_GREEN:
            case KeyEvent.KEYCODE_PROG_BLUE:
            case KeyEvent.KEYCODE_PROG_YELLOW: {
                if (mDVBManager.getPvrManager().isTeletextActive()) {
                    mDVBManager.getPvrManager().sendTeletextInputCommand(
                            keyCode);
                    return true;
                }
                break;
            }
            /** TELETEXT KEY */
            case KeyEvent.KEYCODE_T:
            case KeyEvent.KEYCODE_F5: {
                if (mDVBManager.getPvrManager().isPvrPlaybackActive()) {
                    /** TTX is already active */
                    if (mDVBManager.getPvrManager().isTeletextActive()) {
                        try {
                            mDVBManager.getPvrManager().hideTeletext();
                        } catch (InternalException e) {
                            e.printStackTrace();
                        }
                    }
                    /** Show TTX */
                    else {
                        /** Hide subtitles if they are active */
                        if (mDVBManager.getPvrManager().isSubtitleActive()) {
                            try {
                                mDVBManager.getPvrManager().hideSubtitles();
                            } catch (InternalException e) {
                                e.printStackTrace();
                            }
                        }
                        int trackCount = mDVBManager.getPvrManager()
                                .getTeletextTrackCount();
                        if (trackCount > 0) {
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                    this, android.R.layout.simple_list_item_1);
                            for (int i = 0; i < trackCount; i++) {
                                TeletextTrack track = mDVBManager
                                        .getPvrManager().getTeletextTrack(i);
                                String type = mDVBManager
                                        .getPvrManager()
                                        .convertTeletextTrackTypeToHumanReadableFormat(
                                                track.getType());
                                arrayAdapter.add(PvrManager
                                        .convertTrigramsToLanguage(track
                                                .getName())
                                        + " [" + type + "]");
                            }
                            createListDIalog("Select teletext track",
                                    arrayAdapter, new OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            try {
                                                if (mDVBManager.getPvrManager()
                                                        .isPvrPlaybackActive()) {
                                                    if (mDVBManager
                                                            .getPvrManager()
                                                            .showTeletext(which)) {
                                                        Toast.makeText(
                                                                PvrActivity.this,
                                                                "Teletext started",
                                                                Toast.LENGTH_SHORT)
                                                                .show();
                                                    } else {
                                                        Toast.makeText(
                                                                PvrActivity.this,
                                                                "Teletext is not available",
                                                                Toast.LENGTH_SHORT)
                                                                .show();
                                                    }
                                                }
                                            } catch (InternalException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "No teletext available!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                return true;
            }
            /** SUBTITLES KEY. */
            case KeyEvent.KEYCODE_S:
            case KeyEvent.KEYCODE_CAPTIONS: {
                if (mDVBManager.getPvrManager().isPvrPlaybackActive()) {
                    /** Hide subtitles. */
                    if (mDVBManager.getPvrManager().isSubtitleActive()) {
                        try {
                            mDVBManager.getPvrManager().hideSubtitles();
                            Toast.makeText(this, "Subtitle stopped",
                                    Toast.LENGTH_SHORT).show();
                        } catch (InternalException e) {
                            e.printStackTrace();
                        }
                    }
                    /** Show subtitles. */
                    else {
                        int trackCount = mDVBManager.getPvrManager()
                                .getSubtitlesTrackCount();
                        if (trackCount > 0) {
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                    this, android.R.layout.simple_list_item_1);
                            for (int i = 0; i < trackCount; i++) {
                                SubtitleTrack track = mDVBManager
                                        .getPvrManager().getSubtitleTrack(i);
                                arrayAdapter
                                        .add(PvrManager
                                                .convertTrigramsToLanguage(track
                                                        .getName())
                                                + " ["
                                                + mDVBManager
                                                        .getPvrManager()
                                                        .convertSubtitleTrackModeToHumanReadableFormat(
                                                                track.getType())
                                                + "]");
                            }
                            createListDIalog("Select subtitle track",
                                    arrayAdapter, new OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            try {
                                                if (mDVBManager.getPvrManager()
                                                        .isPvrPlaybackActive()) {
                                                    if (mDVBManager
                                                            .getPvrManager()
                                                            .showSubtitles(
                                                                    which)) {
                                                        Toast.makeText(
                                                                PvrActivity.this,
                                                                "Subtitle started",
                                                                Toast.LENGTH_SHORT)
                                                                .show();
                                                    } else {
                                                        Toast.makeText(
                                                                PvrActivity.this,
                                                                "Subtitle is not available",
                                                                Toast.LENGTH_SHORT)
                                                                .show();
                                                    }
                                                }
                                            } catch (InternalException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Subtitle is not available",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                return true;
            }
            /**
             * AUDIO LANGUAGES
             */
            case KeyEvent.KEYCODE_A:
            case KeyEvent.KEYCODE_F6: {
                if (mDVBManager.getPvrManager().isPvrPlaybackActive()) {
                    int trackCount = mDVBManager.getPvrManager()
                            .getAudioLanguagesTrackCount();
                    if (trackCount > 0) {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                this, android.R.layout.simple_list_item_1);
                        for (int i = 0; i < trackCount; i++) {
                            AudioTrack track = mDVBManager.getPvrManager()
                                    .getAudioLanguage(i);
                            arrayAdapter.add(track.getName() + " "
                                    + track.getLanguage() + "   ["
                                    + track.getAudioDigitalType() + "]["
                                    + track.getAudioChannleCfg() + "]");
                        }
                        createListDIalog("Select audio track", arrayAdapter,
                                new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        try {
                                            if (mDVBManager.getPvrManager()
                                                    .isPvrPlaybackActive()) {
                                                mDVBManager.getPvrManager()
                                                        .setAudioTrack(which);
                                            }
                                        } catch (InternalException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Audio tracks are not available",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
            /**
             * Change Channel Up (Using of KEYCODE_F4 is just workaround because
             * KeyEvent.KEYCODE_CHANNEL_UP is not mapped on remote control).
             */
            case KeyEvent.KEYCODE_F4:
            case KeyEvent.KEYCODE_CHANNEL_UP: {
                try {
                    showChannelInfo(mDVBManager.changeChannelUp());
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InternalException e) {
                    e.printStackTrace();
                }
                return true;
            }
            /**
             * Change Channel Down (Using of KEYCODE_F3 is just workaround
             * because KeyEvent.KEYCODE_CHANNEL_DOWN is not mapped on remote
             * control).
             */
            case KeyEvent.KEYCODE_F3:
            case KeyEvent.KEYCODE_CHANNEL_DOWN: {
                try {
                    showChannelInfo(mDVBManager.changeChannelDown());
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InternalException e) {
                    e.printStackTrace();
                }
                return true;
            }
            /**
             * SHOW INFORMATION SCREEN
             */
            case KeyEvent.KEYCODE_INFO: {
                if (mDVBManager.getPvrManager().isTimeShftActive()
                        || mDVBManager.getPvrManager().isPvrActive()
                        || mDVBManager.getPvrManager().isPvrPlaybackActive()) {
                    showPvrInfo();
                    return true;
                }
                int currentChannel = mDVBManager.getCurrentChannelNumber();
                if (currentChannel > 0) {
                    showChannelInfo(mDVBManager.getChannelInfo(currentChannel));
                }
                return true;
            }
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE: {
                if (mDVBManager.getPvrManager().isTimeShftActive()
                        || mDVBManager.getPvrManager().isPvrPlaybackActive()) {
                    if (mDVBManager.getPvrManager().getPvrSpeed() == PvrSpeedMode.PVR_SPEED_PAUSE) {
                        mDVBManager.getPvrManager().setPvrSpeed(
                                PvrSpeedMode.PVR_SPEED_FORWARD_X1);
                    } else {
                        mDVBManager.getPvrManager().setPvrSpeed(
                                PvrSpeedMode.PVR_SPEED_PAUSE);
                    }
                } else {
                    try {
                        mDVBManager.getPvrManager().startTimeShift();
                    } catch (InternalException e) {
                        e.printStackTrace();
                    }
                }
                showPvrInfo();
                return true;
            }
            case KeyEvent.KEYCODE_MEDIA_STOP: {
                if (mDVBManager.getPvrManager().isPvrPlaybackActive()) {
                    try {
                        mDVBManager.getPvrManager().stopPlayback();
                    } catch (InternalException e) {
                        e.printStackTrace();
                    }
                } else if (mDVBManager.getPvrManager().isTimeShftActive()) {
                    try {
                        mDVBManager.getPvrManager().stopTimeShift();
                    } catch (InternalException e) {
                        e.printStackTrace();
                    }
                } else if (mDVBManager.getPvrManager().isPvrActive()) {
                    mDVBManager.getPvrManager().stopPvr();
                }
                ((TextView) mPvrInfoContainer
                        .findViewById(R.id.textViewPlaybackSpeed)).setText("");
                return true;
            }
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD: {
                if (mDVBManager.getPvrManager().isTimeShftActive()
                        || mDVBManager.getPvrManager().isPvrPlaybackActive()) {
                    mDVBManager.getPvrManager().fastForward();
                    showPvrInfo();
                }
                return true;
            }
            case KeyEvent.KEYCODE_MEDIA_REWIND: {
                if (mDVBManager.getPvrManager().isTimeShftActive()
                        || mDVBManager.getPvrManager().isPvrPlaybackActive()) {
                    mDVBManager.getPvrManager().rewind();
                    showPvrInfo();
                }
                return true;
            }
            case KeyEvent.KEYCODE_MEDIA_RECORD: {
                if (!mDVBManager.getPvrManager().isPvrActive()
                        && !mDVBManager.getPvrManager().isTimeShftActive()) {
                    try {
                        mDVBManager.getPvrManager().startOneTouchRecord();
                        showPvrInfo();
                    } catch (InternalException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
            default: {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Clear surface view with transparency.
     * 
     * @param surface
     *        Surface view to refresh.
     */
    private static void refreshSurfaceView(SurfaceView surface) {
        if (surface.getVisibility() == View.VISIBLE) {
            Canvas canvas = surface.getHolder().lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                surface.getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * Handler for sending action messages to update UI.
     */
    private static class UiHandler extends Handler {
        /** Message ID for Hiding Channel Number/Name View. */
        public static final int HIDE_CHANNEL_INFO_VIEW_MESSAGE = 0,
                HIDE_PVR_INFO_MESSAGE = 1, REFRESH_TIMESHIFT_PLAYBACK = 2,
                REFRESH_PVR_PLAYBACK = 3, SHOW_RECORDS_DIALOG = 4,
                HIDE_RECORDS_DIALOG = 5, REFRESH_PVR_RECORD_PLAYBACK = 6,
                REFRESH_PLAYBACK_SPEED = 7, REMINDER_TRIGGERED = 8;;
        private View mChannelContainer, mPvrContainer;
        /** PVR info container views */
        private TextView mPvrInfoPosition, mPvrInfoAvailablePosition,
                mPvrPlaybackSpeed;
        private ProgressBar mPvrProgressBar;
        private static final SimpleDateFormat sFormat = new SimpleDateFormat(
                "HH:mm:ss");
        private SurfaceView mSurface;

        public UiHandler(View channelContainer, View pvrContainer,
                SurfaceView surface) {
            mSurface = surface;
            mChannelContainer = channelContainer;
            mPvrContainer = pvrContainer;
            mPvrInfoPosition = (TextView) mPvrContainer
                    .findViewById(R.id.textViewPosition);
            mPvrInfoAvailablePosition = (TextView) mPvrContainer
                    .findViewById(R.id.textViewAvailablePosition);
            mPvrProgressBar = (ProgressBar) mPvrContainer
                    .findViewById(R.id.progressBar);
            mPvrPlaybackSpeed = (TextView) mPvrContainer
                    .findViewById(R.id.textViewPlaybackSpeed);
            mPvrProgressBar.setMax(100);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HIDE_CHANNEL_INFO_VIEW_MESSAGE: {
                    mChannelContainer.setVisibility(View.GONE);
                    refreshSurfaceView(mSurface);
                    break;
                }
                case HIDE_PVR_INFO_MESSAGE: {
                    mPvrContainer.setVisibility(View.GONE);
                    refreshSurfaceView(mSurface);
                    break;
                }
                case REFRESH_TIMESHIFT_PLAYBACK: {
                    PvrTimeShiftPositionHolder object = (PvrTimeShiftPositionHolder) msg.obj;
                    int numberOfSeconds = object.getPositionObject()
                            .getPlaybackTimePosition();
                    Date date = new Date(numberOfSeconds * 1000);
                    mPvrInfoPosition.setText(sFormat.format(date));
                    mPvrProgressBar.setProgress(numberOfSeconds);
                    numberOfSeconds = object.getPositionObject()
                            .getRecordTimePosition();
                    mPvrProgressBar.setSecondaryProgress(numberOfSeconds);
                    numberOfSeconds = object.getEndTime();
                    date = new Date(numberOfSeconds * 1000);
                    mPvrInfoAvailablePosition.setText(sFormat.format(date));
                    mPvrProgressBar.setMax(numberOfSeconds);
                    break;
                }
                case REFRESH_PVR_PLAYBACK: {
                    PvrEventRecordPosition object = (PvrEventRecordPosition) msg.obj;
                    int numberOfSeconds = object.getTimePosition();
                    Date date = new Date(numberOfSeconds * 1000);
                    mPvrInfoPosition.setText(sFormat.format(date));
                    mPvrProgressBar.setProgress(numberOfSeconds);
                    mPvrProgressBar.setSecondaryProgress(0);
                    numberOfSeconds = object.getSpacePosition();
                    date = new Date(numberOfSeconds * 1000);
                    mPvrInfoAvailablePosition.setText(sFormat.format(date));
                    mPvrProgressBar.setMax(numberOfSeconds);
                    break;
                }
                case REFRESH_PVR_RECORD_PLAYBACK: {
                    PvrPlaybackPositionHolder holder = (PvrPlaybackPositionHolder) msg.obj;
                    int numberOfSeconds = holder.getPositionObject()
                            .getTimePosition();
                    Date date = new Date(numberOfSeconds * 1000);
                    mPvrInfoPosition.setText(sFormat.format(date));
                    mPvrProgressBar.setProgress(numberOfSeconds);
                    numberOfSeconds = holder.getPvrMediaInfo().getDuration();
                    date = new Date(numberOfSeconds * 1000);
                    mPvrInfoAvailablePosition.setText(sFormat.format(date));
                    mPvrProgressBar.setMax(numberOfSeconds);
                    break;
                }
                case REFRESH_PLAYBACK_SPEED: {
                    String speed = (String) msg.obj;
                    Log.d(TAG, "PLAYBACK SPEED: " + speed);
                    mPvrPlaybackSpeed.setText(speed);
                    break;
                }
                case SHOW_RECORDS_DIALOG: {
                    Dialog dialog = (Dialog) msg.obj;
                    dialog.show();
                    break;
                }
                case HIDE_RECORDS_DIALOG: {
                    Dialog dialog = (Dialog) msg.obj;
                    dialog.cancel();
                    break;
                }
                case REMINDER_TRIGGERED: {
                    Dialog dialog = (Dialog) msg.obj;
                    dialog.show();
                    break;
                }
            }
        }
    }

    /**
     * If key is for PVR record handling.
     * 
     * @param keyCode
     *        to check.
     * @return True if it is ok, false otherwise.
     */
    private boolean isPvrKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_INFO:
            case KeyEvent.KEYCODE_MEDIA_STOP: {
                return true;
            }
            default:
                return false;
        }
    }

    /**
     * If key is for Teletext engine handling.
     * 
     * @param keyCode
     *        to check.
     * @return True if it is ok, false otherwise.
     */
    private boolean isTeletextKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
            case KeyEvent.KEYCODE_PROG_RED:
            case KeyEvent.KEYCODE_PROG_GREEN:
            case KeyEvent.KEYCODE_PROG_BLUE:
            case KeyEvent.KEYCODE_PROG_YELLOW:
            case KeyEvent.KEYCODE_F5:
            case KeyEvent.KEYCODE_T:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_STOP: {
                return true;
            }
            default:
                return false;
        }
    }

    /**
     * If key is for PVR playback handling.
     * 
     * @param keyCode
     *        to check.
     * @return True if it is ok, false otherwise.
     */
    private boolean isPvrPlaybackKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_INFO:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_STOP:
                /** Teletext keys */
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
            case KeyEvent.KEYCODE_PROG_RED:
            case KeyEvent.KEYCODE_PROG_GREEN:
            case KeyEvent.KEYCODE_PROG_BLUE:
            case KeyEvent.KEYCODE_PROG_YELLOW:
            case KeyEvent.KEYCODE_F5:
            case KeyEvent.KEYCODE_T:
                /** Audio languages key */
            case KeyEvent.KEYCODE_A:
            case KeyEvent.KEYCODE_F6:
                /** Subtitle languages key */
            case KeyEvent.KEYCODE_S:
            case KeyEvent.KEYCODE_CAPTIONS: {
                return true;
            }
            default:
                return false;
        }
    }

    /**
     * If key is for time shift handling.
     * 
     * @param keyCode
     *        to check.
     * @return True if it is ok, false otherwise.
     */
    private boolean isTimeShiftKey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_INFO:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_STOP: {
                return true;
            }
            default:
                return false;
        }
    }

    /**
     * Create alert dialog with entries
     * 
     * @param title
     * @param arrayAdapter
     * @param listClickListener
     */
    private void createListDIalog(String title,
            final ArrayAdapter<String> arrayAdapter,
            DialogInterface.OnClickListener listClickListener) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle(title);
        builderSingle.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builderSingle.setAdapter(arrayAdapter, listClickListener);
        builderSingle.show();
    }

    /**
     * Class that contains information needed for displaying while time shift is
     * active.
     */
    private class PvrTimeShiftPositionHolder {
        private int mEndTime;
        private PvrEventTimeshiftPosition mPositionObject;

        public PvrTimeShiftPositionHolder(int mEndTime,
                PvrEventTimeshiftPosition mPositionObject) {
            super();
            this.mEndTime = mEndTime;
            this.mPositionObject = mPositionObject;
        }

        public int getEndTime() {
            return mEndTime;
        }

        public PvrEventTimeshiftPosition getPositionObject() {
            return mPositionObject;
        }
    }

    /**
     * Class that contains information needed for displaying while PVR playback
     * is active.
     */
    private class PvrPlaybackPositionHolder {
        private MediaInfo mPvrMediaInfo;
        private PvrEventPlaybackPosition mPositionObject;

        public PvrPlaybackPositionHolder(MediaInfo pvrMediaInfo,
                PvrEventPlaybackPosition mPositionObject) {
            super();
            this.mPvrMediaInfo = pvrMediaInfo;
            this.mPositionObject = mPositionObject;
        }

        public MediaInfo getPvrMediaInfo() {
            return mPvrMediaInfo;
        }

        public PvrEventPlaybackPosition getPositionObject() {
            return mPositionObject;
        }
    }
}

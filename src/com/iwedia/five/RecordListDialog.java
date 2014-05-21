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
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.iwedia.dtv.pvr.MediaInfo;
import com.iwedia.dtv.pvr.PvrSortMode;
import com.iwedia.dtv.pvr.PvrSortOrder;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.five.dtv.DVBManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Dialog that contains list of PVR records.
 */
public class RecordListDialog extends Dialog implements OnItemSelectedListener,
        OnItemClickListener, android.view.View.OnClickListener {
    private TextView mTitle, mDescription, mStartTime, mDuration, mSize;
    private ListView mListViewRecords;
    private ArrayList<MediaInfo> mRecords;
    private Context mContext;
    private static final SimpleDateFormat sFormatDate = new SimpleDateFormat(
            "HH:mm:ss yyyy-MM-dd");
    private static final SimpleDateFormat sFormat = new SimpleDateFormat(
            "HH:mm:ss");

    public RecordListDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        mContext = context;
        setContentView(R.layout.record_list_dialog);
        /**
         * Initialize views.
         */
        mTitle = (TextView) findViewById(R.id.textViewMediaTitle);
        mDescription = (TextView) findViewById(R.id.textViewMediaDescription);
        mStartTime = (TextView) findViewById(R.id.textViewMediaStartTime);
        mDuration = (TextView) findViewById(R.id.textViewMediaDuration);
        mSize = (TextView) findViewById(R.id.textViewMediaSize);
        mListViewRecords = (ListView) findViewById(R.id.listViewRecords);
        /**
         * Set listeners
         */
        mListViewRecords.setOnItemSelectedListener(this);
        mListViewRecords.setOnItemClickListener(this);
        findViewById(R.id.buttonSortByDateAsc).setOnClickListener(this);
        findViewById(R.id.buttonSortByDateDesc).setOnClickListener(this);
        findViewById(R.id.buttonSortByDurationAsc).setOnClickListener(this);
        findViewById(R.id.buttonSortByDurationDesc).setOnClickListener(this);
        findViewById(R.id.buttonSortByNameAsc).setOnClickListener(this);
        findViewById(R.id.buttonSortByNameDesc).setOnClickListener(this);
    }

    @Override
    public void show() {
        updateRecords();
        super.show();
    }

    /**
     * Create alert dialog with entries
     * 
     * @param title
     * @param arrayAdapter
     * @param listClickListener
     */
    private void createAlertDIalog(final int indexOfRecord) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(mContext);
        builderSingle.setTitle("Choose action");
        builderSingle.setPositiveButton("Play",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            DVBManager.getInstance().getPvrManager()
                                    .startPlayback(indexOfRecord);
                        } catch (InternalException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
        builderSingle.setNegativeButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DVBManager.getInstance().getPvrManager()
                                .deleteRecord(indexOfRecord);
                        updateRecords();
                        dialog.dismiss();
                    }
                });
        builderSingle.show();
    }

    /**
     * Refresh records list.
     */
    private void updateRecords() {
        mRecords = DVBManager.getInstance().getPvrManager().getPvrRecordings();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_list_item_1);
        for (int i = 0; i < mRecords.size(); i++) {
            MediaInfo info = mRecords.get(i);
            arrayAdapter.add(info.getTitle()
                    + (info.isIncomplete() ? " <!>" : ""));
        }
        mListViewRecords.setAdapter(arrayAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        createAlertDIalog(arg2);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
        MediaInfo record = mRecords.get(arg2);
        mTitle.setText(record.getTitle());
        mDescription
                .setText(record.getDescription().equals("") ? "No information available"
                        : record.getDescription());
        mStartTime.setText(sFormatDate.format(record.getStartTime()
                .getCalendar().getTime()));
        mDuration
                .setText(sFormat.format(new Date(record.getDuration() * 1000)));
        mSize.setText(humanReadableByteCount(record.getSize(), true));
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    /**
     * Convert mega bytes to human readable format.
     * 
     * @param mb
     *        Number of mega bytes.
     * @param si
     * @return Human readable format
     */
    public static String humanReadableByteCount(long mb, boolean si) {
        long bytes = mb * 1024 * 1024;
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
                + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSortByDateAsc: {
                DVBManager.getInstance().getPvrManager()
                        .setSortMode(PvrSortMode.SORT_BY_DATE);
                DVBManager.getInstance().getPvrManager()
                        .setSortOrder(PvrSortOrder.SORT_ASCENDING);
                break;
            }
            case R.id.buttonSortByDateDesc: {
                DVBManager.getInstance().getPvrManager()
                        .setSortMode(PvrSortMode.SORT_BY_DATE);
                DVBManager.getInstance().getPvrManager()
                        .setSortOrder(PvrSortOrder.SORT_DESCENDING);
                break;
            }
            case R.id.buttonSortByDurationAsc: {
                DVBManager.getInstance().getPvrManager()
                        .setSortMode(PvrSortMode.SORT_BY_DURATION);
                DVBManager.getInstance().getPvrManager()
                        .setSortOrder(PvrSortOrder.SORT_ASCENDING);
                break;
            }
            case R.id.buttonSortByDurationDesc: {
                DVBManager.getInstance().getPvrManager()
                        .setSortMode(PvrSortMode.SORT_BY_DURATION);
                DVBManager.getInstance().getPvrManager()
                        .setSortOrder(PvrSortOrder.SORT_DESCENDING);
                break;
            }
            case R.id.buttonSortByNameAsc: {
                DVBManager.getInstance().getPvrManager()
                        .setSortMode(PvrSortMode.SORT_BY_NAME);
                DVBManager.getInstance().getPvrManager()
                        .setSortOrder(PvrSortOrder.SORT_ASCENDING);
                break;
            }
            case R.id.buttonSortByNameDesc: {
                DVBManager.getInstance().getPvrManager()
                        .setSortMode(PvrSortMode.SORT_BY_NAME);
                DVBManager.getInstance().getPvrManager()
                        .setSortOrder(PvrSortOrder.SORT_DESCENDING);
                break;
            }
            default:
                break;
        }
        updateRecords();
    }
}

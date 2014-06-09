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
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.iwedia.dtv.pvr.MediaInfo;
import com.iwedia.dtv.pvr.OnTouchInfo;
import com.iwedia.dtv.pvr.PvrSortMode;
import com.iwedia.dtv.pvr.PvrSortOrder;
import com.iwedia.dtv.pvr.SmartInfo;
import com.iwedia.dtv.pvr.TimerInfo;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.five.dtv.DVBManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Dialog that contains list of PVR records.
 */
public class ScheduledRecordListDialog extends ListDialog {
    /** List of scheduled records. */
    private ArrayList<Object> mRecords;

    public ScheduledRecordListDialog(Context context) {
        super(context);
    }

    /**
     * Create alert dialog with entries
     * 
     * @param title
     * @param arrayAdapter
     * @param listClickListener
     */
    protected void createAlertDIalog(final int indexOfRecord) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(mContext);
        builderSingle.setTitle("Delete record?");
        builderSingle.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DVBManager.getInstance().getPvrManager()
                                .deleteScheduledRecord(indexOfRecord);
                        dialog.dismiss();
                    }
                });
        builderSingle.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builderSingle.show();
    }

    /**
     * Refresh records list.
     */
    protected void updateRecords() {
        mRecords = DVBManager.getInstance().getPvrManager()
                .getPvrScheduledRecords();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_list_item_1);
        for (int i = 0; i < mRecords.size(); i++) {
            Object record = mRecords.get(i);
            if (record instanceof SmartInfo) {
                SmartInfo smartInfo = (SmartInfo) record;
                arrayAdapter.add(smartInfo.getTitle());
            } else if (record instanceof TimerInfo) {
                TimerInfo timerInfo = (TimerInfo) record;
                arrayAdapter.add(timerInfo.getTitle());
            } else if (record instanceof OnTouchInfo) {
                OnTouchInfo onTouchInfo = (OnTouchInfo) record;
                arrayAdapter.add(onTouchInfo.getTitle());
            }
        }
        mListViewRecords.setAdapter(arrayAdapter);
        if(arrayAdapter.getCount()==0){
            nothingSelected();
        }
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
    protected void itemSelected(int index) {
        Object record = mRecords.get(index);
        if (record instanceof SmartInfo) {
            SmartInfo smartInfo = (SmartInfo) record;
            mTitle.setText(smartInfo.getTitle());
            mDescription
                    .setText(smartInfo.getDescription().equals("") ? "No information available"
                            : smartInfo.getDescription());
            mStartTime.setText(sFormatDate.format(smartInfo.getStartTime()
                    .getCalendar().getTime()));
            mDuration.setText(sFormat.format(new Date(smartInfo.getEndTime()
                    .getCalendar().getTime().getTime()
                    - smartInfo.getStartTime().getCalendar().getTime()
                            .getTime())));
            mSize.setText("No information available");
        } else if (record instanceof TimerInfo) {
            TimerInfo timerInfo = (TimerInfo) record;
            mTitle.setText(timerInfo.getTitle());
            mDescription
                    .setText(timerInfo.getDescription().equals("") ? "No information available"
                            : timerInfo.getDescription());
            mStartTime.setText(sFormatDate.format(timerInfo.getStartTime()
                    .getCalendar().getTime()));
            mDuration.setText(sFormat.format(new Date(timerInfo.getEndTime()
                    .getCalendar().getTime().getTime()
                    - timerInfo.getStartTime().getCalendar().getTime()
                            .getTime())));
            mSize.setText("No information available");
        } else if (record instanceof OnTouchInfo) {
            OnTouchInfo onTouchInfo = (OnTouchInfo) record;
            mTitle.setText(onTouchInfo.getTitle());
            mDescription
                    .setText(onTouchInfo.getDescription().equals("") ? "No information available"
                            : onTouchInfo.getDescription());
            mStartTime.setText(sFormatDate.format(onTouchInfo.getStartTime()
                    .getCalendar().getTime()));
            mDuration.setText("No information available");
            mSize.setText("No information available");
        }
    }

    @Override
    protected void nothingSelected() {
        mTitle.setText("");
        mDescription.setText("");
        mStartTime.setText("");
        mDuration.setText("");
        mSize.setText("");
    }

    @Override
    protected void buttonSortByDateAscClicked() {
        DVBManager.getInstance().getPvrManager()
                .setScheduledSortMode(PvrSortMode.SORT_BY_DATE);
        DVBManager.getInstance().getPvrManager()
                .setScheduledSortOrder(PvrSortOrder.SORT_ASCENDING);
    }

    @Override
    protected void buttonSortByDateDescClicked() {
        DVBManager.getInstance().getPvrManager()
                .setScheduledSortMode(PvrSortMode.SORT_BY_DATE);
        DVBManager.getInstance().getPvrManager()
                .setScheduledSortOrder(PvrSortOrder.SORT_DESCENDING);
    }

    @Override
    protected void buttonSortByDurationAscClicked() {
        DVBManager.getInstance().getPvrManager()
                .setScheduledSortMode(PvrSortMode.SORT_BY_DURATION);
        DVBManager.getInstance().getPvrManager()
                .setScheduledSortOrder(PvrSortOrder.SORT_ASCENDING);
    }

    @Override
    protected void buttonSortByDurationDescClicked() {
        DVBManager.getInstance().getPvrManager()
                .setScheduledSortMode(PvrSortMode.SORT_BY_DURATION);
        DVBManager.getInstance().getPvrManager()
                .setScheduledSortOrder(PvrSortOrder.SORT_DESCENDING);
    }

    @Override
    protected void buttonSortByNameAscClicked() {
        DVBManager.getInstance().getPvrManager()
                .setScheduledSortMode(PvrSortMode.SORT_BY_NAME);
        DVBManager.getInstance().getPvrManager()
                .setScheduledSortOrder(PvrSortOrder.SORT_ASCENDING);
    }

    @Override
    protected void buttonSortByNameDescClicked() {
        DVBManager.getInstance().getPvrManager()
                .setScheduledSortMode(PvrSortMode.SORT_BY_NAME);
        DVBManager.getInstance().getPvrManager()
                .setScheduledSortOrder(PvrSortOrder.SORT_DESCENDING);
    }
}

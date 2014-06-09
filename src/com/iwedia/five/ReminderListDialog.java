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

import com.iwedia.dtv.pvr.PvrSortMode;
import com.iwedia.dtv.pvr.PvrSortOrder;
import com.iwedia.dtv.reminder.ReminderSmartInfo;
import com.iwedia.dtv.reminder.ReminderTimerInfo;
import com.iwedia.five.dtv.DVBManager;

import java.util.ArrayList;

/**
 * Dialog that contains list of PVR records.
 */
public class ReminderListDialog extends ListDialog {
    /** List of scheduled records. */
    private ArrayList<Object> mReminders;

    public ReminderListDialog(Context context) {
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
        builderSingle.setTitle("Delete reminder?");
        builderSingle.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DVBManager.getInstance().getReminderManager()
                                .deleteReminder(indexOfRecord);
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
        mReminders = DVBManager.getInstance().getReminderManager()
                .getReminders();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_list_item_1);
        for (int i = 0; i < mReminders.size(); i++) {
            Object reminder = mReminders.get(i);
            if (reminder instanceof ReminderSmartInfo) {
                ReminderSmartInfo smartInfo = (ReminderSmartInfo) reminder;
                arrayAdapter.add(smartInfo.getTitle());
            } else if (reminder instanceof ReminderTimerInfo) {
                ReminderTimerInfo timerInfo = (ReminderTimerInfo) reminder;
                arrayAdapter.add(timerInfo.getTitle());
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
        Object reminder = mReminders.get(index);
        if (reminder instanceof ReminderSmartInfo) {
            ReminderSmartInfo smartInfo = (ReminderSmartInfo) reminder;
            mTitle.setText(smartInfo.getTitle());
            mDescription
                    .setText(smartInfo.getDescription().equals("") ? "No information available"
                            : smartInfo.getDescription());
            mStartTime.setText(sFormatDate.format(smartInfo.getTime()
                    .getCalendar().getTime()));
            mDuration.setText("No information available");
            mSize.setText("No information available");
        } else if (reminder instanceof ReminderTimerInfo) {
            ReminderTimerInfo timerInfo = (ReminderTimerInfo) reminder;
            mTitle.setText(timerInfo.getTitle());
            mDescription.setText("No information available");
            mStartTime.setText(sFormatDate.format(timerInfo.getTime()
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
        DVBManager.getInstance().getReminderManager()
                .setSortMode(PvrSortMode.SORT_BY_DATE);
        DVBManager.getInstance().getReminderManager()
                .setSortOrder(PvrSortOrder.SORT_ASCENDING);
    }

    @Override
    protected void buttonSortByDateDescClicked() {
        DVBManager.getInstance().getReminderManager()
                .setSortMode(PvrSortMode.SORT_BY_DATE);
        DVBManager.getInstance().getReminderManager()
                .setSortOrder(PvrSortOrder.SORT_DESCENDING);
    }

    @Override
    protected void buttonSortByDurationAscClicked() {
        DVBManager.getInstance().getReminderManager()
                .setSortMode(PvrSortMode.SORT_BY_DURATION);
        DVBManager.getInstance().getReminderManager()
                .setSortOrder(PvrSortOrder.SORT_ASCENDING);
    }

    @Override
    protected void buttonSortByDurationDescClicked() {
        DVBManager.getInstance().getReminderManager()
                .setSortMode(PvrSortMode.SORT_BY_DURATION);
        DVBManager.getInstance().getReminderManager()
                .setSortOrder(PvrSortOrder.SORT_DESCENDING);
    }

    @Override
    protected void buttonSortByNameAscClicked() {
        DVBManager.getInstance().getReminderManager()
                .setSortMode(PvrSortMode.SORT_BY_NAME);
        DVBManager.getInstance().getReminderManager()
                .setSortOrder(PvrSortOrder.SORT_ASCENDING);
    }

    @Override
    protected void buttonSortByNameDescClicked() {
        DVBManager.getInstance().getReminderManager()
                .setSortMode(PvrSortMode.SORT_BY_NAME);
        DVBManager.getInstance().getReminderManager()
                .setSortOrder(PvrSortOrder.SORT_DESCENDING);
    }
}

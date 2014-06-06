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
public class RecordListDialog extends ListDialog {
    /** List of recorded media */
    private ArrayList<MediaInfo> mRecords;

    public RecordListDialog(Context context) {
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
    protected void updateRecords() {
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
        MediaInfo record = mRecords.get(index);
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
                .setSortMode(PvrSortMode.SORT_BY_DATE);
        DVBManager.getInstance().getPvrManager()
                .setSortOrder(PvrSortOrder.SORT_ASCENDING);
    }

    @Override
    protected void buttonSortByDateDescClicked() {
        DVBManager.getInstance().getPvrManager()
                .setSortMode(PvrSortMode.SORT_BY_DATE);
        DVBManager.getInstance().getPvrManager()
                .setSortOrder(PvrSortOrder.SORT_DESCENDING);
    }

    @Override
    protected void buttonSortByDurationAscClicked() {
        DVBManager.getInstance().getPvrManager()
                .setSortMode(PvrSortMode.SORT_BY_DURATION);
        DVBManager.getInstance().getPvrManager()
                .setSortOrder(PvrSortOrder.SORT_ASCENDING);
    }

    @Override
    protected void buttonSortByDurationDescClicked() {
        DVBManager.getInstance().getPvrManager()
                .setSortMode(PvrSortMode.SORT_BY_DURATION);
        DVBManager.getInstance().getPvrManager()
                .setSortOrder(PvrSortOrder.SORT_DESCENDING);
    }

    @Override
    protected void buttonSortByNameAscClicked() {
        DVBManager.getInstance().getPvrManager()
                .setSortMode(PvrSortMode.SORT_BY_NAME);
        DVBManager.getInstance().getPvrManager()
                .setSortOrder(PvrSortOrder.SORT_ASCENDING);
    }

    @Override
    protected void buttonSortByNameDescClicked() {
        DVBManager.getInstance().getPvrManager()
                .setSortMode(PvrSortMode.SORT_BY_NAME);
        DVBManager.getInstance().getPvrManager()
                .setSortOrder(PvrSortOrder.SORT_DESCENDING);
    }
}

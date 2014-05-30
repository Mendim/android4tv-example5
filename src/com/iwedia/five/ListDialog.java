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

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Dialog that contains list of records.
 */
public abstract class ListDialog extends Dialog implements
        OnItemSelectedListener, OnItemClickListener,
        android.view.View.OnClickListener {
    protected TextView mTitle, mDescription, mStartTime, mDuration, mSize;
    protected ListView mListViewRecords;
    protected Context mContext;
    protected static final SimpleDateFormat sFormatDate = new SimpleDateFormat(
            "HH:mm:ss yyyy-MM-dd");
    protected static final SimpleDateFormat sFormat = new SimpleDateFormat(
            "HH:mm:ss");

    public ListDialog(Context context) {
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
     * Create alert dialog with entries.
     * 
     * @param title
     * @param arrayAdapter
     * @param listClickListener
     */
    protected abstract void createAlertDIalog(final int indexOfRecord);

    /**
     * Refresh records list.
     */
    protected abstract void updateRecords();

    /**
     * Refresh record description.
     */
    protected abstract void itemSelected(int index);

    /**
     * Sort by date ascending click listener.
     */
    protected abstract void buttonSortByDateAscClicked();

    /**
     * Sort by date descending click listener.
     */
    protected abstract void buttonSortByDateDescClicked();

    /**
     * Sort by duration ascending click listener.
     */
    protected abstract void buttonSortByDurationAscClicked();

    /**
     * Sort by duration descending click listener.
     */
    protected abstract void buttonSortByDurationDescClicked();

    /**
     * Sort by name ascending click listener.
     */
    protected abstract void buttonSortByNameAscClicked();

    /**
     * Sort by name descending click listener.
     */
    protected abstract void buttonSortByNameDescClicked();

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        createAlertDIalog(arg2);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
        itemSelected(arg2);
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
                buttonSortByDateAscClicked();
                break;
            }
            case R.id.buttonSortByDateDesc: {
                buttonSortByDateDescClicked();
                break;
            }
            case R.id.buttonSortByDurationAsc: {
                buttonSortByDurationAscClicked();
                break;
            }
            case R.id.buttonSortByDurationDesc: {
                buttonSortByDurationDescClicked();
                break;
            }
            case R.id.buttonSortByNameAsc: {
                buttonSortByNameAscClicked();
                break;
            }
            case R.id.buttonSortByNameDesc: {
                buttonSortByNameDescClicked();
                break;
            }
            default:
                break;
        }
        updateRecords();
    }
}

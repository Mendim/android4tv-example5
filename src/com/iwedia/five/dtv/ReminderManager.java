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

import android.util.Log;

import com.iwedia.dtv.dtvmanager.IDTVManager;
import com.iwedia.dtv.pvr.PvrSortMode;
import com.iwedia.dtv.pvr.PvrSortOrder;
import com.iwedia.dtv.reminder.IReminderCallback;
import com.iwedia.dtv.reminder.IReminderControl;
import com.iwedia.dtv.reminder.ReminderType;

import java.util.ArrayList;

public class ReminderManager {
    public static final String TAG = "DVBManager";
    private IReminderControl mReminderControl;
    private IReminderCallback mReminderCallback;
    private static ReminderManager sInstance;

    protected static ReminderManager getInstance(IDTVManager mDTVManager) {
        if (sInstance == null) {
            sInstance = new ReminderManager(mDTVManager);
        }
        return sInstance;
    }

    private ReminderManager(IDTVManager mDTVManager) {
        mReminderControl = mDTVManager.getReminderControl();
    }

    /**
     * Register reminder callback.
     * 
     * @param callback
     *        Callback to register.
     */
    public void registerCallback(IReminderCallback callback) {
        mReminderCallback = callback;
        mReminderControl.registerCallback(callback);
    }

    /**
     * Unregisters previously setted callback.
     */
    public void unregisterCallback() {
        try {
            mReminderControl.unregisterCallback(mReminderCallback);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Reminder callback is not setted at all!");
        }
    }

    /**
     * Retrieves list of reminders.
     * 
     * @return List of reminders.
     */
    public ArrayList<Object> getReminders() {
        ArrayList<Object> reminders = new ArrayList<Object>();
        int numberOfReminders = mReminderControl.updateList();
        for (int i = 0; i < numberOfReminders; i++) {
            ReminderType type = mReminderControl.getType(i);
            if (type == ReminderType.REMINDER_SMART) {
                reminders.add(mReminderControl.getSmartInfo(i));
            } else if (type == ReminderType.REMINDER_TIMER) {
                reminders.add(mReminderControl.getTimerInfo(i));
            }
        }
        return reminders;
    }

    public void deleteReminder(int index) {
        mReminderControl.destroy(index);
    }

    /**
     * Sets desired sort mode.
     * 
     * @param order
     *        New sort mode to set.
     */
    public void setSortMode(PvrSortMode mode) {
        mReminderControl.setListSortMode(mode);
    }

    /**
     * Returns active sort mode.
     */
    public PvrSortMode getSortMode() {
        return mReminderControl.getListSortMode();
    }

    /**
     * Sets desired sort order.
     * 
     * @param order
     *        New sort order to set.
     */
    public void setSortOrder(PvrSortOrder order) {
        mReminderControl.setListSortOrder(order);
    }

    /**
     * Returns active sort order.
     */
    public PvrSortOrder getSortOrder() {
        return mReminderControl.getListSortOrder();
    }
}

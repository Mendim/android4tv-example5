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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Broadcast receiver for media mounted/unmounted events.
 */
public class MediaMountedReceiver extends BroadcastReceiver {
    private static final String TAG = "MediaMountedReceiver";
    private static MediaCallback mMediaCallback = null;

    public interface MediaCallback {
        public void mediaMounted(String mediaPath);

        public void mediaUnmounted(String mediaPath);
    }

    private static MediaMountedReceiver sInstance = null;

    public static MediaMountedReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new MediaMountedReceiver();
        }
        return sInstance;
    }

    public MediaMountedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "\n\n\n\n\n\n INTENT RECEIVED: " + intent.toString());
        String path = intent.getData().getPath();
        if (mMediaCallback != null) {
            if (intent.getAction().contains("MEDIA_MOUNTED")) {
                mMediaCallback.mediaMounted(path);
            } else if (intent.getAction().contains("MEDIA_EJECT")
                    || intent.getAction().contains("MEDIA_UNMOUNTED")) {
                mMediaCallback.mediaUnmounted(path);
            }
        }
    }

    public void setMediaCallback(MediaCallback mMediaCallback) {
        MediaMountedReceiver.mMediaCallback = mMediaCallback;
    }
}

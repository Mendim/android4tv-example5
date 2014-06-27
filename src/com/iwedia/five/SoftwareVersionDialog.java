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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.RemoteException;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.iwedia.dtv.swupdate.SWVersionType;
import com.iwedia.five.dtv.DVBManager;

/**
 * Channel List Activity.
 */
public class SoftwareVersionDialog extends Dialog {
    public static final String TAG = "SoftwareVersionDialog";
    private DVBManager mDVBManager = null;

    public SoftwareVersionDialog(Context context, int width, int height) {
        super(context, R.style.DialogTransparent);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Hide Status Bar of Android.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
        setContentView(R.layout.software_version_dialog);
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;
        mDVBManager = DVBManager.getInstance();
        /** Initialize GridView. */
        initialize(context);
    }

    /**
     * Initialize GridView (Channel List) and set click listener to it.
     * 
     * @throws RemoteException
     *         If connection error happens.
     */
    private void initialize(Context context) {
        TextView textView = (TextView) findViewById(R.id.text_view_application_version);
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            textView.setText(pInfo.versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        textView = (TextView) findViewById(R.id.text_view_framework_version);
        textView.setText(mDVBManager.getSwVersion(SWVersionType.MAL));
        textView = (TextView) findViewById(R.id.text_view_middleware_version);
        textView.setText(mDVBManager.getSwVersion(SWVersionType.MWL));
        textView = (TextView) findViewById(R.id.text_view_sdk_version);
        textView.setText(Build.VERSION.RELEASE);
    }
}

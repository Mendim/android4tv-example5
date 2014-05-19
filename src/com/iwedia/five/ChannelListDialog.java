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
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.iwedia.dtv.types.InternalException;
import com.iwedia.five.adapters.ChannelListAdapter;
import com.iwedia.five.dtv.DVBManager;

/**
 * Channel List Activity.
 */
public class ChannelListDialog extends Dialog implements OnItemClickListener {
    public static final String TAG = "ChannelListActivity";
    private GridView mChannelList;

    public ChannelListDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        setContentView(R.layout.channel_list_activity);
        /** Initialize GridView. */
        initializeChannelList(context);
    }

    /**
     * Initialize GridView (Channel List) and set click listener to it.
     * 
     * @throws RemoteException
     *         If connection error happens.
     */
    private void initializeChannelList(Context context) {
        mChannelList = (GridView) findViewById(R.id.gridview_channellist);
        mChannelList.setOnItemClickListener(this);
        mChannelList.setAdapter(new ChannelListAdapter(context, DVBManager
                .getInstance().getChannelNames()));
        mChannelList.setSelection(DVBManager.getInstance()
                .getCurrentChannelNumber());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        try {
            DVBManager.getInstance().changeChannelByNumber(position);
            cancel();
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }
}

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
package com.iwedia.five.adapters;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iwedia.five.R;

import java.util.ArrayList;

/**
 * Adapter for Channel List GridView.
 */
public class ChannelListAdapter extends BaseAdapter {
    private final String TAG = "ChannelListAdapter";
    private LayoutInflater mLayoutInflater = null;
    private ArrayList<String> mChannelNames = null;

    public ChannelListAdapter(Context context, ArrayList<String> channelNames) {
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        mChannelNames = channelNames;
    }

    @Override
    public int getCount() {
        return mChannelNames.size();
    }

    @Override
    public Object getItem(int position) throws IndexOutOfBoundsException {
        return mChannelNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChannelHolder lHolder = null;
        if (null == convertView) {
            convertView = mLayoutInflater.inflate(R.layout.channel_list_item,
                    null);
            lHolder = new ChannelHolder(convertView);
            convertView.setTag(lHolder);
        } else {
            lHolder = (ChannelHolder) convertView.getTag();
        }
        setChannelItemView(position, lHolder);
        return convertView;
    }

    /**
     * Populate grid view element with appropriate data.
     * 
     * @param position
     *        Grid view element position.
     * @param holder
     *        View holder instance for this element.
     */
    private void setChannelItemView(int position, ChannelHolder holder) {
        holder.getItemChannelName().setText(mChannelNames.get(position));
        holder.getItemChannelNumber().setText(String.valueOf(position + 1));
    }

    /**
     * Holder for GridView Elements.
     */
    private class ChannelHolder {
        private TextView mItemChannelNumber = null;
        private TextView mItemChannelName = null;

        protected ChannelHolder(View view) {
            mItemChannelNumber = (TextView) view
                    .findViewById(R.id.textview_channel_number);
            mItemChannelName = (TextView) view
                    .findViewById(R.id.textview_channel_name);
        }

        protected TextView getItemChannelNumber() {
            return mItemChannelNumber;
        }

        protected TextView getItemChannelName() {
            return mItemChannelName;
        }
    }
}

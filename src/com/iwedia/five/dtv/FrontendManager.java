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

import java.util.ArrayList;

public class FrontendManager {
    private static final String TAG = "FrontendManager";
    private ArrayList<Frontend> mFrontends;
    private static FrontendManager sInstance;

    private static FrontendManager getInstance() {
        if (sInstance == null) {
            sInstance = new FrontendManager();
        }
        return sInstance;
    }

    public FrontendManager() {
        mFrontends = new ArrayList<Frontend>();
    }

    /**
     * Adds frontend to frontend list.
     * 
     * @param frontend
     */
    public static void frontendFound(Frontend frontend) {
        boolean exist = false;
        for (int i = 0; i < getInstance().mFrontends.size(); i++) {
            if (frontend.getFrontendIndex() == getInstance().mFrontends.get(i)
                    .getFrontendIndex()) {
                exist = true;
            }
        }
        if (!exist) {
            getInstance().mFrontends.add(frontend);
        }
        Log.d(TAG, getInstance().mFrontends.toString());
    }

    /**
     * Returns route by frontend index.
     * 
     * @param frontendIndex
     * @return Live route or -1 if it is not found.
     */
    public static int getLiveRouteByFrontendIndex(int frontendIndex) {
        for (int i = 0; i < getInstance().mFrontends.size(); i++) {
            if (frontendIndex == getInstance().mFrontends.get(i)
                    .getFrontendIndex()) {
                return getInstance().mFrontends.get(i).getRoute();
            }
        }
        return -1;
    }

    /**
     * Set current antenna state.
     * 
     * @param frontendIndex
     * @param state
     *        Connected or not.
     */
    public static void setAntennaState(int frontendIndex, boolean state) {
        for (int i = 0; i < getInstance().mFrontends.size(); i++) {
            if (frontendIndex == getInstance().mFrontends.get(i)
                    .getFrontendIndex()) {
                getInstance().mFrontends.get(i).setAntennaConnnected(state);
            }
        }
    }

    /**
     * Is antenna connected for desired route;
     * 
     * @param routeId
     *        Desired route;
     * @return True if antenna is connected, False otherwise.
     */
    public static boolean getAntennaState(int routeId) {
        for (int i = 0; i < getInstance().mFrontends.size(); i++) {
            if (routeId == getInstance().mFrontends.get(i).getRoute()) {
                return getInstance().mFrontends.get(i).isAntennaConnnected();
            }
        }
        return true;
    }
}

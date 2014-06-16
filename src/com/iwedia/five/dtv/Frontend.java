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

/**
 * Class that holds live route and appropriate frontend index.
 */
public class Frontend {
    private int mRoute;
    private int mFrontendIndex;
    private boolean antennaConnnected;

    public Frontend(int mRoute, int mFrontendIndex) {
        this.mRoute = mRoute;
        this.mFrontendIndex = mFrontendIndex;
        antennaConnnected = true;
    }

    @Override
    public String toString() {
        return "Frontend [mRoute=" + mRoute + ", mFrontendIndex="
                + mFrontendIndex + ", antennaConnnected=" + antennaConnnected
                + "]";
    }

    public int getRoute() {
        return mRoute;
    }

    public int getFrontendIndex() {
        return mFrontendIndex;
    }

    public boolean isAntennaConnnected() {
        return antennaConnnected;
    }

    public void setAntennaConnnected(boolean antennaConnnected) {
        this.antennaConnnected = antennaConnnected;
    }
}

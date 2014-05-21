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
 * IP service holder.
 */
public class IPService {
    private String mName;
    private String mUrl;

    public IPService(String mName, String mUrl) {
        super();
        this.mName = mName;
        this.mUrl = mUrl;
    }

    public String getName() {
        return mName;
    }

    public String getUrl() {
        return mUrl;
    }

    @Override
    public String toString() {
        return "IPService [mName=" + mName + ", mUrl=" + mUrl + "]";
    }
}

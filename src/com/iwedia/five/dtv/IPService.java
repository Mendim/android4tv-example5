package com.iwedia.five.dtv;

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

package com.hlz.net;

/**
 * QQ：1430261583
 * Created by Hanlizhi on 2016/11/18.
 */

/**
 * 一次请求的数据集
 */
public class URLData {
    private String key;
    private long expires;
    private String netType;
    private String url;
    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}


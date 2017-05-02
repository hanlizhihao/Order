package com.hlz.net;

/**一次请求的数据集
 * QQ：1430261583
 * Created by Hanlizhi on 2016/11/18.
 */
public class URLData {
    private String key;
    private long expires;
    private String netType;
    private String url;
    public long getExpires() {
        return expires;
    }

    void setExpires(long expires) {
        this.expires = expires;
    }

    public String getNetType() {
        return netType;
    }

    void setNetType(String netType) {
        this.netType = netType;
    }

    public String getUrl() {
        return url;
    }

    void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {

        return key;
    }

    void setKey(String key) {
        this.key = key;
    }

}


package com.hlz.net;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.hlz.order.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * QQ：1430261583
 * Created by Hanlizhi on 2016/11/18.
 * 更新该文件，设计为单例模式的UrlManager，优化节点查找方式，减少解析xml的次数
 */

public class UrlManager {
    private Map<String,URLData> urls=null;
    private UrlManager(){

    }
    public URLData findURL(final Context activity, final String findKey){
        if (urls==null) {
            urls=new HashMap<>();
            final XmlResourceParser xmlParser=activity.getResources().getXml(R.xml.url);
            int eventCode;//对应文档的节点码
            try {
                eventCode=xmlParser.getEventType();//判断是不是到文档的最后
                while (eventCode!= XmlPullParser.END_DOCUMENT){//循环读取文档的节点
                    switch (eventCode){
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG://一个节点的开始
                            if ("Node".equals(xmlParser.getName())){
                                URLData urlData=new URLData();
                                String key=xmlParser.getAttributeValue(null,"Key");
                                urlData.setKey(key.trim());
                                urlData.setExpires(Long.parseLong(xmlParser.getAttributeValue(null,"Expires")));
                                urlData.setNetType(xmlParser.getAttributeValue(null,"NetType"));
                                urlData.setUrl(xmlParser.getAttributeValue(null,"Url"));
                                urls.put(key,urlData);
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            break;
                        default:
                            break;
                    }
                    eventCode=xmlParser.next();
                }
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            } finally {
                xmlParser.close();
            }
            return urls.get(findKey);
        }else{
            return urls.get(findKey);
        }
    }
    //单例模式
    public static UrlManager getUrlManager(){
        return UrlManagerHolder.mInstance;
    }
    private static class UrlManagerHolder{
        private static final UrlManager mInstance=new UrlManager();
    }
}


package com.data.collector.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HttpUtils {

    private static HttpUtils instance;
    private CloseableHttpClient hc;
    
    public HttpUtils(){
        hc = HttpClients.createDefault();
    }
    
    
    public static HttpUtils getInstance(){
        if(instance ==null){
            instance = new HttpUtils();
        }
        return instance;
    }
    
    public void getArticleListPage() throws ClientProtocolException, IOException{
        
        HttpPost hp = new HttpPost("http://www.ahzfcg.gov.cn/mhxt/MhxtSearchBulletinController.zc?method=bulletinChannelRightDown");
        hp.setHeader("Content-Type", "text/html; charset=utf-8");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        
        BasicNameValuePair channelCodeParam = new BasicNameValuePair("channelCode", "sjcg");
        BasicNameValuePair bTypeParam = new BasicNameValuePair("bType", "03");
        BasicNameValuePair areaCodeParam = new BasicNameValuePair("areaCode", "");
        BasicNameValuePair typeParam = new BasicNameValuePair("type", "00");
        BasicNameValuePair keyParam = new BasicNameValuePair("key", "学校");
        BasicNameValuePair bStartDateParam = new BasicNameValuePair("bStartDate", "2013-12-05");
        BasicNameValuePair bEndDateParam = new BasicNameValuePair("bEndDate", "2017-09-25");
        BasicNameValuePair proTypeParam = new BasicNameValuePair("proType", "");
        BasicNameValuePair categoryParam = new BasicNameValuePair("", "");
        BasicNameValuePair areaCodeNameParam = new BasicNameValuePair("areaCodeName", "-----全部----");
        BasicNameValuePair pageNoParam = new BasicNameValuePair("pageNo", "1");
        BasicNameValuePair pageSizeParam = new BasicNameValuePair("pageSize", "1000");
        parameters.add(channelCodeParam);
        parameters.add(bTypeParam);
        parameters.add(areaCodeParam);
        parameters.add(typeParam);
        parameters.add(keyParam);
        parameters.add(bStartDateParam);
        parameters.add(bEndDateParam);
        parameters.add(proTypeParam);
        parameters.add(categoryParam);
        parameters.add(areaCodeNameParam);
        parameters.add(pageNoParam);
        parameters.add(pageSizeParam);
        
        HttpEntity he = new UrlEncodedFormEntity(parameters);
        hp.setEntity(he);
        CloseableHttpResponse hres = hc.execute(hp);
        
        hres.getStatusLine().getStatusCode();
        if(hres.getStatusLine().getStatusCode() ==200){
            InputStream is = hres.getEntity().getContent();
            
            Document doc = Jsoup.parse(is, "UTF-8", "http://www.ahzfcg.gov.cn/");
            Elements linksE = doc.select("li > a");
            List<String> links = linksE.eachAttr("href");
        }
    }
    
}

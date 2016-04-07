package com.softisland.common.utils;

import com.softisland.common.utils.bean.SoftCookie;
import com.softisland.common.utils.bean.SoftHeader;
import com.softisland.common.utils.bean.SoftHttpResponse;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liwx on 2015/10/28.
 */
public class HttpClientUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    public static final String SunX509 = "SunX509";
    public static final String JKS = "JKS";
    public static final String PKCS12 = "PKCS12";
    public static final String TLS = "TLS";
    /**
	 * 默认：请求获取数据的超时时间，单位毫秒。
	 */
	private static final int defaultSocketTimeout = 3000;
	/**
	 * 默认：设置连接超时时间，单位毫秒。
	 */
	private static final int defaultConnectTimeout = 2000;
	
    /**
     * 通过GET方式获取指定URL的内容
     * @param url
     * @return
     * @throws Exception
     */
    public static SoftHttpResponse getUrlContent(String url, SoftCookie[] softCookies)throws Exception{
        CloseableHttpClient httpclient = getHttpClient(softCookies);
        //CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);

        return getResponseContent(response);
    }

    public static SoftHttpResponse getUrlContent(String url)throws Exception{

        return getUrlContent(url,null);
    }

    /**
     * 获取header和返回内容
     * @param url
     * @return
     * @throws Exception
     */
    public static SoftHttpResponse getHeadersAndContent(String url,SoftCookie[] softCookies)throws Exception{
        CloseableHttpClient httpclient = getHttpClient(softCookies);

        //CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        return getResponseHeaderAndContent(response);

    }

    /**
     * 获取header和返回内容
     * @param response
     * @return
     * @throws IOException
     */
    private static SoftHttpResponse getResponseHeaderAndContent(CloseableHttpResponse response) throws IOException {
        SoftHttpResponse res = getResponseContent(response);
        Header[] headers = response.getAllHeaders();
        if(null != headers){
            Map<String,String> map = new HashMap<>();
            for(Header h : headers){
                if(map.containsKey(h.getName())){
                    map.put(h.getName(),map.get(h.getName())+";"+h.getValue());
                }else{
                    map.put(h.getName(),h.getValue());
                }
            }
            res.setHeaders(map);
        }
        return res;
    }

    /**
     * 返回请求内容
     * @param response
     * @return
     * @throws Exception
     */
    public static SoftHttpResponse getResponseContent(CloseableHttpResponse response)throws IOException{
        try {
            SoftHttpResponse res = new SoftHttpResponse(response.getStatusLine().getStatusCode(),EntityUtils.toString(response.getEntity(),"utf-8"));
            return res;
        }finally {
            response.close();
        }
    }



    /**
     * 向指定的URL发送请求
     * @param url
     * @param paraMap 参数
     * @return
     * @throws Exception
     */
    public static SoftHttpResponse postParamsToUrl(String url,Map<String,String> paraMap,SoftCookie[] cookies)throws Exception{
        return postParamsToUrl(url,paraMap,cookies,null);
    }

    public static SoftHttpResponse postParamsToUrl(String url,Map<String,String> paraMap,SoftCookie[] cookies,SoftHeader[] headers)throws IOException{
        CloseableHttpClient httpclient = getHttpClient(cookies);
        HttpPost httpPost = new HttpPost(url);

        org.apache.http.client.config.RequestConfig requestConfig = org.apache.http.client.config.RequestConfig.custom()
                .setSocketTimeout(defaultSocketTimeout)
                .setConnectTimeout(defaultConnectTimeout)
                //.setProxy(getProxy())
                .build();
        httpPost.setConfig(requestConfig);

        if(null != headers){
            for(SoftHeader header : headers){
                httpPost.setHeader(header.getName(),header.getValue());
            }
        }
        try {
            if(null != paraMap && !paraMap.isEmpty()){
                List<NameValuePair> nvps = new ArrayList<>(paraMap.size());
                paraMap.keySet().forEach(v->nvps.add(new BasicNameValuePair(v, paraMap.get(v))));
                httpPost.setEntity(new UrlEncodedFormEntity(nvps,"utf-8"));
            }
        } catch (UnsupportedEncodingException e) {
            log.error("<不支持的转码>",e);
            e.printStackTrace();
        }

        CloseableHttpResponse response = httpclient.execute(httpPost);

        return getResponseContent(response);
    }

    /**
     * 返回header和内容
     * @param url
     * @param paraMap
     * @return
     * @throws Exception
     */
    public static SoftHttpResponse postParamsToUrl1(String url,Map<String,String> paraMap,SoftCookie[] cookies)throws Exception{
        CloseableHttpClient httpclient = getHttpClient(cookies);

        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<>(paraMap.size());
        paraMap.keySet().forEach(v->nvps.add(new BasicNameValuePair(v, paraMap.get(v))));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps,"utf-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("<不支持的转码>",e);
            e.printStackTrace();
        }

        CloseableHttpResponse response = httpclient.execute(httpPost);

        return getResponseHeaderAndContent(response);
    }

    /**
     * 获取httclient
     * @param cookies
     * @return
     */
    private static CloseableHttpClient getHttpClient(SoftCookie[] cookies){
        CloseableHttpClient httpclient;
        if(null == cookies || cookies.length == 0){
            httpclient = HttpClients.createDefault();
        }else{
            CookieStore cookieStore = new BasicCookieStore();
            for(SoftCookie softCookie : cookies){
                BasicClientCookie cookie = new BasicClientCookie(softCookie.getName(),softCookie.getValue());
                cookie.setDomain(softCookie.getDomain());
                cookie.setPath("/");
                cookieStore.addCookie(cookie);
            }
            httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        }
        return httpclient;
    }



    /**
     * 发送json数据到指定的URL
     * @param url
     * @param json
     * @return
     * @throws Exception
     */
    public static SoftHttpResponse postJsonDataToUrl(String url,String json)throws Exception{
        return postJsonDataToUrl(url,json,null);
    }

    public static SoftHttpResponse postJsonDataToUrl(String url,String json,SoftCookie[] softCookies)throws Exception{

        return postJsonDataToUrl(url,json,softCookies,null);
    }

    public static SoftHttpResponse postJsonDataToUrl(String url, String json, SoftCookie[] softCookies, SoftHeader[] headers)throws Exception{

        CloseableHttpClient httpclient = getHttpClient(softCookies);

        HttpPost httpPost = new HttpPost(url);
        if(null != headers){
            for(SoftHeader header : headers){
                httpPost.setHeader(header.getName(),header.getValue());
            }
        }

        StringEntity entity = new StringEntity(json,"utf-8");//解决中文乱码问题
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json; charset=utf-8");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(httpPost);

        return getResponseContent(response);
    }


    /**
     * get HttpURLConnection
     * @param strUrl url��ַ
     * @return HttpURLConnection
     * @throws IOException
     */
    public static HttpURLConnection getHttpURLConnection(String strUrl)
            throws IOException {
        URL url = new URL(strUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url
                .openConnection();
        return httpURLConnection;
    }

    /**
     * get HttpsURLConnection
     * @param strUrl url��ַ
     * @return HttpsURLConnection
     * @throws IOException
     */
    public static HttpsURLConnection getHttpsURLConnection(String strUrl)
            throws IOException {
        URL url = new URL(strUrl);
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url
                .openConnection();
        return httpsURLConnection;
    }

    public static String getURL(String strUrl) {

        if(null != strUrl) {
            int indexOf = strUrl.indexOf("?");
            if(-1 != indexOf) {
                return strUrl.substring(0, indexOf);
            }

            return strUrl;
        }

        return strUrl;

    }


    public static String getQueryString(String strUrl) {

        if(null != strUrl) {
            int indexOf = strUrl.indexOf("?");
            if(-1 != indexOf) {
                return strUrl.substring(indexOf+1, strUrl.length());
            }

            return "";
        }

        return strUrl;
    }

    public static Map queryString2Map(String queryString) {
        if(null == queryString || "".equals(queryString)) {
            return null;
        }

        Map m = new HashMap();
        String[] strArray = queryString.split("&");
        for(int index = 0; index < strArray.length; index++) {
            String pair = strArray[index];
            HttpClientUtil.putMapByPair(pair, m);
        }

        return m;

    }

    public static void putMapByPair(String pair, Map m) {

        if(null == pair || "".equals(pair)) {
            return;
        }

        int indexOf = pair.indexOf("=");
        if(-1 != indexOf) {
            String k = pair.substring(0, indexOf);
            String v = pair.substring(indexOf+1, pair.length());
            if(null != k && !"".equals(k)) {
                m.put(k, v);
            }
        } else {
            m.put(pair, "");
        }
    }

    /**
     * @param reader
     * @return String
     * @throws IOException
     */
    public static String bufferedReader2String(BufferedReader reader) throws IOException {
        StringBuffer buf = new StringBuffer();
        String line = null;
        while( (line = reader.readLine()) != null) {
            buf.append(line);
            buf.append("\r\n");
        }

        return buf.toString();
    }

    /**
     * 用get方法调用一个url
     * 返回json字符串
     */
    public static SoftHttpResponse getJson(String url)throws IOException{
        return getJson(url,null);
    }

    /**
     * 通过代理获取steam库存
     * @param steamId
     * @return
     * @throws IOException
     */
    public static SoftHttpResponse getSteamStock(String steamId)throws IOException{
        SoftCookie cookie = new SoftCookie();
        cookie.setDomain("steamcommunity.com");
        cookie.setName("Steam_Language");
        cookie.setValue("schinese");
        cookie.setPath("/");
        CloseableHttpClient httpclient = getHttpClient(new SoftCookie[]{cookie});
        String url = "http://steamcommunity.com/profiles/"+steamId+"/inventory/json/730/2/";
        HttpGet httpGet = new HttpGet(new String(url.getBytes(), "UTF-8"));
        //CN|782be65066aff833853feb3d472d9f55
        org.apache.http.client.config.RequestConfig requestConfig = org.apache.http.client.config.RequestConfig.custom()
                .setSocketTimeout(defaultSocketTimeout)
                .setConnectTimeout(defaultConnectTimeout)
                .setProxy(getProxy())
                .build();

        httpGet.setConfig(requestConfig);

        return getResponseContent(httpclient.execute(httpGet));
    }

    /**
     * 代理IP
     */
    private static final String[] hosts = new String[]{
            "23.95.142.167:13228",
            "23.95.142.198:13228",
            "23.95.142.196:13228",
            "23.95.142.165:13228",
            "23.95.142.111:13228",
            "192.227.199.252:13228",
            "192.227.199.216:13228",
            "192.227.199.182:13228",
            "192.227.199.149:13228",
            "192.227.199.27:13228"
    };

    private static HttpHost getProxy(){
        int i = new Random().nextInt(10);
        HttpHost proxy = new HttpHost(hosts[i].split(":")[0],Integer.parseInt(hosts[i].split(":")[1]));
        return proxy;
    }

    /**
     * 使用GET方法获取指定URL的内容
     * @param url
     * @param softCookies
     * @return
     * @throws IOException
     */
    public static SoftHttpResponse getJson(String url,SoftCookie[] softCookies)throws IOException{
        CloseableHttpClient httpclient = getHttpClient(softCookies);

        HttpGet httpGet = new HttpGet(new String(url.getBytes(), "UTF-8"));

        org.apache.http.client.config.RequestConfig requestConfig = org.apache.http.client.config.RequestConfig.custom()
                .setSocketTimeout(defaultSocketTimeout)
                .setConnectTimeout(defaultConnectTimeout)
                .build();

        httpGet.setConfig(requestConfig);

        CloseableHttpResponse response = httpclient.execute(httpGet);
        return getResponseContent(response);
    }
    
    public static void main1(String[] args) throws Exception{
        /*String url = "http://218.70.34.166:8999/otos/service/proxy";
        String str = "{\n" +
                "\"serviceName\": \"otos_deli_order_sign\",\n" +
                "\"callType\": \"001\",\n" +
                "\"params\": {\n" +
                "\"deliId\": \"18983177733\",\n" +
                "\"ucode\": \"\",\n" +
                "\"orderId\": \"order1510281855590003\",\n" +
                "\"merFlowId\": \"\",\n" +
                "\"userId\": \"\"\n" +
                "}\n" +
                "} ";
        Map<String,String> map = new HashMap<>();
        map.put("jsonRequest",str);
        String ss = new HttpClientUtil().postParamsToUrl(url,map);
        System.out.println(ss);*/
//        String url = "http://localhost:8082/logistics/do";
//        String str = "{\"service_name\":\"login_service\",\"user_name\":\"1898317773d3\",\"user_pwd\":\"123456\"}";
//        Map<String,String> map = new HashMap<>();
//        map.put("jsonRequest",str);
//        String ss = new HttpClientUtil().postParamsToUrl(url,map);
//        System.out.println(ss);
    	
    	
    	String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=RUloRbJO3iEJTFTWcx5TyovU80pklXkpcjU6XsolixIdeg5AFk6bVyx3hJcnbk7NO97DvPqA4mdSwJGdggHypPFBcYLlDg7KfL52yXy6nnds-E3scc0qRwjo2rhIEBXBBDEjAEATVW&openid=ojKTjwDVRuIz5TVxPpC6VS3X4_DE&lang=zh_CN";
    	String ss = new HttpClientUtil().getJson(url).getContent();
    	System.out.println(ss);

        String str = "\uD83D\uDE08222座樹123山d雕33ds3ad";
        Pattern pattern = Pattern.compile("([\\u4E00-\\u9FA5a-z0-9]+)");

        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            System.out.println(matcher.group(1));
        }


    	
    }
}

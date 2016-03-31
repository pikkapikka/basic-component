package com.softisland.common.utils.bean;

import java.util.Map;

/**
 * Created by liwx on 16/3/21.
 */
public class SoftHttpResponse {
    /**
     * 返回的HTTP状态码
     */
    private int status;
    /**
     * 返回的内容
     */
    private String content;
    /**
     * 返回的headers
     */
    private Map<String,String> headers;

    public SoftHttpResponse(){}

    public SoftHttpResponse(int status,String content){
        this.status = status;
        this.content = content;
    }

    public SoftHttpResponse(int status,String content,Map<String,String> headers){
        this.status = status;
        this.content = content;
        this.headers = headers;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}

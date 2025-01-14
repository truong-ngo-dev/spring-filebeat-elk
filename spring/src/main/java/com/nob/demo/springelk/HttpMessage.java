package com.nob.demo.springelk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nob.utils.JsonUtils;

public class HttpMessage {
    private ServiceHeader header;
    private String body;

    public String toString() {
        try {
            return JsonUtils.toJson(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpMessage(ServiceHeader header, String body) {
        this.header = header;
        this.body = body;
    }

    public ServiceHeader getHeader() {
        return header;
    }

    public void setHeader(ServiceHeader header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

package com.nob.demo.springelk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nob.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Objects;
import java.util.UUID;

public class ServiceHeader {
    private String contextPath;
    private String uri;
    private String httpMethod;
    private String clientMessageId;
    private String traceId;
    private Long timestamp;
    private String srcAppIp;
    private int srcAppPort;
    private String destAppIp;
    private int destAppPort;
    private String authorization;

    public ServiceHeader() {
    }

    public ServiceHeader(String contextPath, String uri, String httpMethod, String clientMessageId, String traceId, Long timestamp, String srcAppIp, int srcAppPort, String destAppIp, int destAppPort, String authorization) {
        this.contextPath = contextPath;
        this.uri = uri;
        this.httpMethod = httpMethod;
        this.clientMessageId = clientMessageId;
        this.traceId = traceId;
        this.timestamp = timestamp;
        this.srcAppIp = srcAppIp;
        this.srcAppPort = srcAppPort;
        this.destAppIp = destAppIp;
        this.destAppPort = destAppPort;
        this.authorization = authorization;
    }

    public static ServiceHeader of(HttpServletRequest request) {
        String clientMessageId = request.getHeader("clientMessageId");
        if (clientMessageId == null) clientMessageId = UUID.randomUUID().toString();
        String traceId = request.getHeader("traceId");
        if (traceId == null) traceId = UUID.randomUUID().toString();
        Object auth = request.getHeader("Authorization");
        String authorization = Objects.nonNull(auth) ? "<<Not intent to log>>" : null;
        Object uriObj = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String uri = uriObj != null ? (String) uriObj : "/";
        ServiceHeader header = new ServiceHeader();
        header.setUri(uri);
        header.setSrcAppIp(request.getRemoteAddr());
        header.setDestAppIp(request.getLocalAddr());
        header.setSrcAppPort(request.getRemotePort());
        header.setDestAppPort(request.getLocalPort());
        header.setHttpMethod(request.getMethod());
        header.setClientMessageId(clientMessageId);
        header.setTraceId(traceId);
        header.setAuthorization(authorization);
        return header;
    }

    public String toString() {
        try {
            return JsonUtils.toJson(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getClientMessageId() {
        return clientMessageId;
    }

    public void setClientMessageId(String clientMessageId) {
        this.clientMessageId = clientMessageId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSrcAppIp() {
        return srcAppIp;
    }

    public void setSrcAppIp(String srcAppIp) {
        this.srcAppIp = srcAppIp;
    }

    public int getSrcAppPort() {
        return srcAppPort;
    }

    public void setSrcAppPort(int srcAppPort) {
        this.srcAppPort = srcAppPort;
    }

    public String getDestAppIp() {
        return destAppIp;
    }

    public void setDestAppIp(String destAppIp) {
        this.destAppIp = destAppIp;
    }

    public int getDestAppPort() {
        return destAppPort;
    }

    public void setDestAppPort(int destAppPort) {
        this.destAppPort = destAppPort;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
}

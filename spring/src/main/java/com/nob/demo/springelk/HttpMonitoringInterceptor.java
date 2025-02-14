package com.nob.demo.springelk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nob.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

@Component
@RestControllerAdvice
public class HttpMonitoringInterceptor extends RequestBodyAdviceAdapter implements HandlerInterceptor, ResponseBodyAdvice<Object> {

    private static final Logger log = LoggerFactory.getLogger(HttpMonitoringInterceptor.class);

    private static final String SERVICE_HEADER = "serviceHeader";

    private static final String CLIENT_MESSAGE_ID = "clientMessageId";

    private static final String TRACE_ID = "traceId";

    private static final String LOG_TYPE = "logType";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        ServiceHeader header = ServiceHeader.of(request);
        request.setAttribute(SERVICE_HEADER, header);
        MDC.put(SERVICE_HEADER, header.toString());
        MDC.put(CLIENT_MESSAGE_ID, header.getClientMessageId());
        MDC.put(TRACE_ID, header.getTraceId());
        MDC.put(LOG_TYPE, "Request");
        log.info("Receive request with uri: {}", request.getRequestURI() + "?" + request.getQueryString());
        return true;
    }

    @Override
    @SuppressWarnings("all")
    public Object afterBodyRead(@NonNull Object body, @NonNull HttpInputMessage inputMessage, @NonNull MethodParameter parameter, @NonNull Type targetType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        ServiceHeader header = null;
        String json = null;
        try {
            json = JsonUtils.toJson(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("Request body: {}", json);
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        ServiceHeader header;
        try {
            header = JsonUtils.fromJson(MDC.get(SERVICE_HEADER), ServiceHeader.class);
            assert header != null;
            response.addHeader(CLIENT_MESSAGE_ID, header.getClientMessageId());
            response.addHeader(TRACE_ID, header.getTraceId());
            log.info("Return response with status: {}", response.getStatus());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Type targetType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType, @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        if (Objects.nonNull(body)) {
            String json = null;
            try {
                json = JsonUtils.toJson(sanitizeByteArrayFields(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            MDC.remove(LOG_TYPE);
            MDC.put(LOG_TYPE, "Response");
            log.info("Response body: {}", json);
        }
        return null;
    }

    private Object sanitizeByteArrayFields(Object body) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> map = mapper.convertValue(body, new TypeReference<>() {});
            map.replaceAll((key, value) -> {
                if (value instanceof byte[]) {
                    return "binary data omitted";
                }
                return value;
            });
            return map;
        } catch (IllegalArgumentException e) {
            log.error("Error sanitizing byte array fields: {}", e.getMessage(), e);
            return body;
        }
    }
}

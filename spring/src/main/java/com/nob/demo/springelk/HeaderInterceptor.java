package com.nob.demo.springelk;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class HeaderInterceptor implements HandlerInterceptor {

    private static final String CLIENT_MESSAGE_ID_HEADER = "clientMessageId";

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String clientMessageId = request.getHeader(CLIENT_MESSAGE_ID_HEADER);
        if (clientMessageId != null) {
            MDC.put(CLIENT_MESSAGE_ID_HEADER, clientMessageId);
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        MDC.remove(CLIENT_MESSAGE_ID_HEADER);
    }
}

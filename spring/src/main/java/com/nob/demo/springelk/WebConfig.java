package com.nob.demo.springelk;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final HttpMonitoringInterceptor httpMonitoringInterceptor;

    public WebConfig(HttpMonitoringInterceptor httpMonitoringInterceptor) {
        this.httpMonitoringInterceptor = httpMonitoringInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(httpMonitoringInterceptor);
    }
}

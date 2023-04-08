package org.wgcisotto.request.logger.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.wgcisotto.request.logger.configuration.HttpLoggerProperties;
import org.wgcisotto.request.logger.model.HttpLoggerDTO;
import org.wgcisotto.request.logger.service.HttpTrafficService;
import org.wgcisotto.request.logger.utils.HttpLogUtil;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;


/**
 * Since Http GET and DELETE requests has no body content they are not intercepted by
 * RequestBodyAdviceAdapter. This class intents to handle those requests
 */
public class LogHttpRequestInterceptor implements HandlerInterceptor, WebMvcConfigurer {

    @Autowired
    private HttpLoggerProperties httpLoggerProperties;

    @Autowired
    private HttpTrafficService logService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(DispatcherType.REQUEST.name().equals(request.getDispatcherType().name())
            && (request.getMethod().equals(HttpMethod.GET.name()) || request.getMethod().equals(HttpMethod.DELETE.name()))){
            Objects.requireNonNull(HttpLoggerDTO.builder()
                            .uri(request.getRequestURI())
                            .method(request.getMethod())
                            .requestHeaders(HttpLogUtil.buildHeadersMap(request).toString())
                    .build());
        }
        return true;
    }
}

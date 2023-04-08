package org.wgcisotto.request.logger.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.wgcisotto.request.logger.configuration.HttpLoggerProperties;
import org.wgcisotto.request.logger.model.HttpLoggerDTO;
import org.wgcisotto.request.logger.service.HttpTrafficService;
import org.wgcisotto.request.logger.utils.HttpLogUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class LogRequestControllerAdvice extends RequestBodyAdviceAdapter {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private HttpTrafficService httpTrafficService;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        httpTrafficService.request(HttpLoggerDTO.builder()
                        .uri(httpServletRequest.getRequestURI())
                        .method(httpServletRequest.getMethod())
                        .requestHeaders(HttpLogUtil.buildHeadersMap(httpServletRequest).toString())
                        .requestBody(HttpLogUtil.getJsonObjectString(body))
                .build());
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }
}

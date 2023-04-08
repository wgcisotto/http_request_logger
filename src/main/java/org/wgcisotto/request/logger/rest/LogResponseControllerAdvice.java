package org.wgcisotto.request.logger.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.wgcisotto.request.logger.model.HttpLoggerDTO;
import org.wgcisotto.request.logger.service.HttpTrafficService;
import org.wgcisotto.request.logger.utils.HttpLogUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Order()
@ControllerAdvice
public class LogResponseControllerAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private HttpTrafficService httpTrafficService;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if(request instanceof ServletServerHttpRequest && response instanceof ServletServerHttpResponse){
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
            httpTrafficService.response(HttpLoggerDTO.builder()
                            .uri(servletRequest.getRequestURI())
                            .method(servletRequest.getMethod())
                            .responseHeaders(HttpLogUtil.buildHeadersMap(servletResponse).toString())
                            .responseBody(HttpLogUtil.getJsonObjectString(body))
                            .responseHttpStatus(HttpStatus.valueOf(servletResponse.getStatus()))
                    .build());
        }
        return body;
    }
}

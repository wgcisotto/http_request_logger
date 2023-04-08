package org.wgcisotto.request.logger.configuration;

import feign.Logger;
import feign.Request;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.wgcisotto.request.logger.model.HttpLoggerDTO;
import org.wgcisotto.request.logger.service.HttpTrafficService;
import org.wgcisotto.request.logger.utils.ResponseWrapper;

import java.io.IOException;

import static feign.Util.*;

public class CustomFeignLogger extends Logger {

    @Autowired
    private HttpTrafficService httpTrafficService;

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        String method = request.method();;
        String url = request.url();
        String headers = request.headers().toString();
        String body = getPayloadMessage(request);
        httpTrafficService.logFeignRequest(HttpLoggerDTO.builder()
                        .method(method)
                        .uri(url)
                        .requestHeaders(headers)
                        .requestBody(body)
                .build());
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        int statusCode = response.status();
        String headers = response.headers().toString();
        ResponseWrapper responseWrapper = new ResponseWrapper(response);
        String body = getPayloadMessage(responseWrapper.getResponse());
        httpTrafficService.logFeignResponse(HttpLoggerDTO.builder()
                        .responseHttpStatus(HttpStatus.valueOf(statusCode))
                        .requestExecutionTimeMillis(elapsedTime)
                        .responseHeaders(headers)
                        .responseBody(body)
                .build());
        return responseWrapper.getResponse();
    }

    private String getPayloadMessage(Request request) {
        if(request.body() == null){
            return "";
        }
        return request.charset() != null ? new String(request.body(), request.charset()) : new String(request.body());
    }

    private String getPayloadMessage(Response response) throws IOException {
        if (response.body() == null){
            return "";
        }
        byte[] bodyData = toByteArray(response.body().asInputStream());
        return decodeOrDefault(bodyData, UTF_8, "Binary data");
    }

    @Override
    protected void log(String s, String s1, Object... objects) {
        //ignore default implementation of feign logger
    }
}

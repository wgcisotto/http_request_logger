package org.wgcisotto.request.logger.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.wgcisotto.request.logger.model.HttpLoggerDTO;
import org.wgcisotto.request.logger.service.HttpTrafficService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RestTemplateLoggerInterceptor implements ClientHttpRequestInterceptor {

    @Autowired
    private HttpTrafficService httpTrafficService;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] requestBody, ClientHttpRequestExecution execution) throws IOException {
        Long requestExecTimeMillis = System.currentTimeMillis();
        HttpLoggerDTO httpLoggerDTO = HttpLoggerDTO.builder()
                .requestHeaders(request.getHeaders().toString())
                .requestBody(new String(requestBody))
                .uri(request.getURI().toString())
                .method(Objects.requireNonNull(request.getMethod()).toString())
                .build();
        httpTrafficService.logRestTemplateRequest(httpLoggerDTO);
        ClientHttpResponse response = execution.execute(request, requestBody);
        httpLoggerDTO.setRequestTimeMillis(requestExecTimeMillis);
        httpLoggerDTO.setResponseBody(new BufferedReader(new InputStreamReader(response.getBody()))
                .lines().collect(Collectors.joining("")));
        httpLoggerDTO.setResponseHeaders(response.getHeaders().toString());
        httpLoggerDTO.setMethod(request.getMethod().toString());
        httpLoggerDTO.setResponseHttpStatus(response.getStatusCode());
        httpLoggerDTO.setUri(request.getURI().toString());
        httpTrafficService.logRestTemplateResponse(httpLoggerDTO);
        return response;
    }
}

package org.wgcisotto.request.logger.service;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.wgcisotto.request.logger.model.HttpLoggerDTO;
import org.wgcisotto.request.logger.utils.MDCUtils;

import java.util.concurrent.CompletableFuture;

@Service
public interface HttpTrafficService {

    String REQUEST_FORMATTER = "RequestHeaders=[%s] RequestBody=[%s]";
    String REQUEST_FORMATTER_PREFIX = "%s Request %s %s";
    String RESPONSE_FORMATTER = "ResponseHeaders=[%s] ResponseBody=[%s]";
    String RESPONSE_FORMATTER_PREFIX = "%s Response %s %s (%s ms) %s";

    String FEIGN_URL = "FEIGN_URL";
    String FEIGN_METHOD = "FEIGN_METHOD";
    String FEIGN_REQUEST_HEADERS = "FEIGN_REQUEST_HEADERS";
    String FEIGN_REQUEST_BODY = "FEIGN_REQUEST_BODY";
    String FEIGN_REQUEST_TIME_MILLIS = "FEIGN_REQUEST_TIME_MILLIS";

    void logInFile(String logFileMessage);

    boolean shouldLogInFile(String uri);

    boolean shouldLogInDb(String uri);

    boolean shouldLogFeignRequestInDb();

    boolean shouldLogFeignRequestInFile();

    boolean shouldLogRestTemplateRequestInDb();

    boolean shouldLogRestTemplateRequestInFile();

    Logger getLogger();

    CompletableFuture<? extends Object> saveAsync(HttpLoggerDTO httpLoggerDTO);

    default void request(HttpLoggerDTO httpLoggerDTO){
        boolean logFile = shouldLogInFile(httpLoggerDTO.getUri());
        boolean loginDb = shouldLogInDb(httpLoggerDTO.getUri());
        if(!logFile && loginDb){
            return;
        }
        String logDbMessage = String.format(REQUEST_FORMATTER,
                httpLoggerDTO.getRequestHeaders(),
                httpLoggerDTO.getRequestBody());
        String logFileMessage = String.format(REQUEST_FORMATTER_PREFIX,
                httpLoggerDTO.getMethod(),
                MDCUtils.getWsOperationPath(),
                logDbMessage);
        if(logFile){
            logInFile(logFileMessage);
        }
        if(loginDb){
            MDCUtils.put(MDCUtils.REQUEST_HEADER, httpLoggerDTO.getRequestHeaders());
            MDCUtils.put(MDCUtils.REQUEST_BODY, httpLoggerDTO.getRequestBody());
        }

    }

    default void response(HttpLoggerDTO httpLoggerDTO) {
        boolean logFile = shouldLogInFile(httpLoggerDTO.getUri());
        boolean loginDb = shouldLogInDb(httpLoggerDTO.getUri());
        if(!logFile && loginDb){
            return;
        }
        long execTime = System.currentTimeMillis() - MDCUtils.getRequestMilli();
        String logResponseMessage = String.format(RESPONSE_FORMATTER,
                httpLoggerDTO.getRequestHeaders(),
                httpLoggerDTO.getRequestBody());
        String logFileMessage = String.format(RESPONSE_FORMATTER_PREFIX,
                httpLoggerDTO.getMethod(),
                httpLoggerDTO.getResponseHttpStatus(),
                MDCUtils.getWsOperationPath(),
                execTime,
                logResponseMessage);
        if(logFile){
            logInFile(logFileMessage);
        }
        if(loginDb){
            httpLoggerDTO.setRequestExecutionTimeMillis(execTime);
            httpLoggerDTO.setRequestTimeMillis(MDCUtils.getRequestMilli());
            httpLoggerDTO.setTransactionId(MDCUtils.getTransactionId());
            httpLoggerDTO.setRequestHeaders(MDCUtils.getRequestHeader());
            httpLoggerDTO.setRequestBody(MDCUtils.getRequestBody());
            httpLoggerDTO.setOperation(MDCUtils.getWsOperationPath());
            saveAsync(httpLoggerDTO);
        }
    }

    default void logRestTemplateRequest(HttpLoggerDTO httpLoggerDTO){
        if(!shouldLogRestTemplateRequestInFile() &&
            !shouldLogRestTemplateRequestInDb()) {
            return;
        }
        String logMessage = String.format(REQUEST_FORMATTER,
                httpLoggerDTO.getRequestHeaders(),
                httpLoggerDTO.getRequestBody());
        String logFileMessage = String.format(REQUEST_FORMATTER_PREFIX,
                httpLoggerDTO.getMethod(),
                httpLoggerDTO.getUri(),
                logMessage);
        if(shouldLogRestTemplateRequestInFile()){
            logInFile(logFileMessage);
        }
    }

    default void logRestTemplateResponse(HttpLoggerDTO httpLoggerDTO){
        if(!shouldLogRestTemplateRequestInFile() &&
                !shouldLogRestTemplateRequestInDb()) {
            return;
        }
        long execTime = System.currentTimeMillis() - httpLoggerDTO.getRequestTimeMillis();
        String logResponseMessage = String.format(RESPONSE_FORMATTER,
                httpLoggerDTO.getResponseHeaders(),
                httpLoggerDTO.getResponseBody());
        String logFileMessage = String.format(RESPONSE_FORMATTER_PREFIX,
                httpLoggerDTO.getMethod(),
                httpLoggerDTO.getResponseHttpStatus(),
                httpLoggerDTO.getUri(),
                execTime,
                logResponseMessage);
        if(shouldLogRestTemplateRequestInFile()){
            logInFile(logFileMessage);
        }
        if(shouldLogRestTemplateRequestInDb()){
            httpLoggerDTO.setRequestExecutionTimeMillis(execTime);
            httpLoggerDTO.setTransactionId(MDCUtils.getTransactionId());
            httpLoggerDTO.setOperation(httpLoggerDTO.getMethod() + " " + httpLoggerDTO.getUri());
            saveAsync(httpLoggerDTO);
        }
    }

    default void logFeignRequest(HttpLoggerDTO httpLoggerDTO) {
        if(!shouldLogFeignRequestInFile() &&
            !shouldLogFeignRequestInDb()){
            return;
        }
        String method = httpLoggerDTO.getMethod();
        String url = httpLoggerDTO.getUri();
        MDCUtils.put(FEIGN_URL, url);
        MDCUtils.put(FEIGN_METHOD, method);
        MDCUtils.put(FEIGN_REQUEST_TIME_MILLIS, String.valueOf(System.currentTimeMillis()));
        String headers = httpLoggerDTO.getRequestHeaders();
        String body = httpLoggerDTO.getRequestBody();
        String logDbMessage = String.format(REQUEST_FORMATTER,
                headers,
                body);
        String logFileMessage = String.format(REQUEST_FORMATTER_PREFIX,
                method,
                url,
                logDbMessage);
        if(shouldLogFeignRequestInFile()){
            logInFile(logFileMessage);
        }
        if(shouldLogFeignRequestInDb()){
            MDCUtils.put(FEIGN_REQUEST_HEADERS, headers);
            MDCUtils.put(FEIGN_REQUEST_BODY, body);
        }
    }

    default void logFeignResponse(HttpLoggerDTO httpLoggerDTO) {
        if(!shouldLogFeignRequestInFile() &&
                !shouldLogFeignRequestInDb()){
            return;
        }
        HttpStatus statusCode = httpLoggerDTO.getResponseHttpStatus();
        String responseHeaders = httpLoggerDTO.getResponseHeaders();;
        String body = httpLoggerDTO.getResponseBody();
        Long elapsedTime = httpLoggerDTO.getRequestExecutionTimeMillis();
        String logResponseMessage = String.format(RESPONSE_FORMATTER,
                responseHeaders,
                body);
        String logFileMessage = String.format(RESPONSE_FORMATTER_PREFIX,
                MDC.get(FEIGN_URL),
                statusCode,
                MDC.get(FEIGN_METHOD),
                elapsedTime,
                logResponseMessage);
        if(shouldLogFeignRequestInFile()){
            logInFile(logFileMessage);
        }
        if(shouldLogFeignRequestInDb()){
            httpLoggerDTO.setRequestExecutionTimeMillis(elapsedTime);
            httpLoggerDTO.setRequestTimeMillis(Long.valueOf(MDC.get(FEIGN_REQUEST_TIME_MILLIS)));
            httpLoggerDTO.setTransactionId(MDCUtils.getTransactionId());
            httpLoggerDTO.setRequestHeaders(MDC.get(FEIGN_REQUEST_HEADERS));
            httpLoggerDTO.setResponseBody(MDC.get(FEIGN_REQUEST_BODY));
            httpLoggerDTO.setMethod(MDC.get(FEIGN_METHOD));
            httpLoggerDTO.setUri(MDC.get(FEIGN_URL));
            httpLoggerDTO.setOperation(MDC.get(FEIGN_METHOD) + " " + MDC.get(FEIGN_URL));
            saveAsync(httpLoggerDTO);
        }

    }


}

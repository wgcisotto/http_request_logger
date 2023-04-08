package org.wgcisotto.request.logger.utils;

import org.slf4j.MDC;

import java.util.Objects;

public class MDCUtils {

    public static final String REQUEST_MILLI = "REQUEST_MILLI";
    public static final String TRANSACTION_ID = "TRANSACTION_ID";
    public static final String REQUEST_HEADER = "REQUEST_HEADER";
    public static final String REQUEST_BODY = "REQUEST_BODY";
    public static final String WS_OPERATION_PATH = "WS_OPERATION_PATH";

    public static String getTransactionId(){
        return MDC.get(TRANSACTION_ID);
    }

    public static Long getRequestMilli(){
        return Long.parseLong(MDC.get(REQUEST_MILLI));
    }

    public static String getRequestHeader(){
        return MDC.get(REQUEST_HEADER);
    }

    public static String getRequestBody(){
        return MDC.get(REQUEST_BODY);
    }

    public static String getWsOperationPath(){
        return MDC.get(WS_OPERATION_PATH);
    }

    public static void put(String key, String value){
        if(Objects.nonNull(key) && Objects.nonNull(value)){
            MDC.put(key, value);
        }
    }

}

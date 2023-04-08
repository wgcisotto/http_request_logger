package org.wgcisotto.request.logger.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class HttpLoggerDTO {

    private String transactionId;
    private String operation;
    private String uri;
    private String method;
    private String requestHeaders;
    private String requestBody;
    private String responseHeaders;
    private String responseBody;
    private HttpStatus responseHttpStatus;
    //private String xRequestId;
    private Long requestExecutionTimeMillis;
    private Long requestTimeMillis;

}
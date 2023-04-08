package org.wgcisotto.request.logger.utils;

import feign.Response;
import feign.Util;
import org.apache.catalina.filters.AddDefaultCharsetFilter;

import java.io.IOException;

public class ResponseWrapper {

    private Response response;
    private byte[] bodyData;

    public ResponseWrapper(Response response) throws IOException {
        this.response = response;
        this.bodyData = Util.toByteArray(response.body().asInputStream());
    }

    public Response getResponse() {
        return response.toBuilder()
                .body(bodyData)
                .build();
    }

}

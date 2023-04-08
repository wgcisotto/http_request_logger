package org.wgcisotto.request.logger.configuration;

import feign.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignLoggerFactory;

public class CustomFeignLoggerFactory implements FeignLoggerFactory {

    @Autowired
    private CustomFeignLogger customFeignLogger;

    @Override
    public Logger create(Class<?> type) {
        return customFeignLogger;
    }
}

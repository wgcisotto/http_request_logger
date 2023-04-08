package org.wgcisotto.request.logger.configuration;

import feign.Logger;
import feign.Request;
import org.springframework.cloud.openfeign.FeignLoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientLoggerConfiguration {

    @Bean
    public FeignLoggerFactory feignLoggerFactory() {
        return new CustomFeignLoggerFactory();
    }

    @Bean
    public Logger.Level feignLoggerLever(){
        return Logger.Level.FULL;
    }

    @Bean
    public Request.Options feignRequestOptions() {
        return new Request.Options();
    }

}

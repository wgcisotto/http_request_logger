package org.wgcisotto.request.logger.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ws-logger")
public class HttpLoggerProperties {

    @Autowired
    private FeignLoggerProperties feignLoggerProps;

    @Autowired
    private RestTemplateLoggerProperties restTemplateLoggerProps;

    @Autowired
    private ApplicationLoggerProperties applicationLoggerProps;

}

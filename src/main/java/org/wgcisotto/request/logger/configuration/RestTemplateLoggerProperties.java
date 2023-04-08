package org.wgcisotto.request.logger.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "http-logger.rest-template-request")
public class RestTemplateLoggerProperties {

    private boolean dbLoggingEnabled;
    private boolean fileLoggingDisabled;

}

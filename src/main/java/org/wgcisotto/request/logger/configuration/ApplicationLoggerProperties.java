package org.wgcisotto.request.logger.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "http-logger.api-request")
public class ApplicationLoggerProperties {

    private List<String> fileUriPatterns;
    private List<String> dbUriPatterns;

    public List<String> getFileUriPatterns() {
        return fileUriPatterns == null ? new ArrayList<>() : fileUriPatterns;
    }

    public List<String> getDbUriPatterns() {
        return dbUriPatterns == null ? new ArrayList<>() : dbUriPatterns;
    }
}

package org.wgcisotto.request.logger.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.wgcisotto.request.logger.configuration.HttpLoggerProperties;

@Slf4j
public abstract class AbstractHttpTrafficService implements HttpTrafficService {

    @Autowired
    private HttpLoggerProperties properties;

    @Override
    public boolean shouldLogInFile(String uri) {
        return properties.getApplicationLoggerProps().getFileUriPatterns().stream()
                .anyMatch(path -> new AntPathMatcher().match(path, uri));
    }

    @Override
    public boolean shouldLogInDb(String uri) {
        return properties.getApplicationLoggerProps().getDbUriPatterns().stream()
                .anyMatch(path -> new AntPathMatcher().match(path, uri));
    }

    @Override
    public boolean shouldLogFeignRequestInDb() {
        return properties.getFeignLoggerProps().isDbLoggingEnabled();
    }

    @Override
    public boolean shouldLogFeignRequestInFile() {
        return properties.getFeignLoggerProps().isFileLoggingDisabled();
    }

    @Override
    public boolean shouldLogRestTemplateRequestInDb() {
        return properties.getRestTemplateLoggerProps().isDbLoggingEnabled();
    }

    @Override
    public boolean shouldLogRestTemplateRequestInFile() {
        return properties.getRestTemplateLoggerProps().isFileLoggingDisabled();
    }

    @Override
    public void logInFile(String logFileMessage) {
        getLogger().info(logFileMessage);
    }

    @Override
    public Logger getLogger() {
        return log;
    }

}

package org.wgcisotto.request.logger.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.wgcisotto.request.logger.configuration.CustomDateTimeSerializer;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HttpLogUtil {

    public static Map<String, String> buildHeadersMap(HttpServletRequest request){
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    public static <T> String getJsonObjectString(T object){
        if(object == null){
            return Strings.EMPTY;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        SimpleModule module = new SimpleModule();
        module.addSerializer(DateTime.class, new CustomDateTimeSerializer());
        mapper.registerModule(module);
        ObjectWriter writer = mapper.writer();
        try {
            return writer.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Some problem occurred parsing output message for logging...", e);
            return "/! Failed to convert Object to Json String /! -> " + e.getMessage();
        }
    }

    public static Map<String, String> buildHeadersMap(HttpServletResponse response){
        return response.getHeaderNames()
                .stream()
                .collect(Collectors.toMap(header -> header, response::getHeader));
    }

    public static String getServletRequestUrl(ServletRequest request){
        if(!(request instanceof HttpServletRequest)) {
            return "Not an instance of HttpServletRequest";
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        StringBuilder operation = new StringBuilder();
        operation.append(httpServletRequest.getMethod()).append(" ");
        operation.append(httpServletRequest.getRequestURI());
        String queryString = httpServletRequest.getQueryString();
        if(queryString == null){
            return operation.toString();
        }
        return operation.append("?").append(queryString).toString();
    }

}
package org.wgcisotto.request.logger.filter;


import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.wgcisotto.request.logger.utils.HttpLogUtil;
import org.wgcisotto.request.logger.utils.MDCUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogTransactionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            //TODO: maybe this should be handled by te application (creation of the transactionId)
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String transactionId = UUID.randomUUID().toString();
            MDCUtils.put(MDCUtils.TRANSACTION_ID, transactionId);
            MDCUtils.put(MDCUtils.WS_OPERATION_PATH, HttpLogUtil.getServletRequestUrl(request));
            MDCUtils.put(MDCUtils.REQUEST_MILLI, String.valueOf(System.currentTimeMillis()));
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.setHeader("X_TransactionID", transactionId);
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}

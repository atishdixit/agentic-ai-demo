package com.example.chatapp.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Tags every request with a request id (read from X-Request-Id if the caller sends one,
 * generated otherwise), available to every log line via Log4j2's ThreadContext and echoed
 * back as a response header - same pattern as CorrelationIdFilter in the
 * employee-support-system project, kept as a single small file here since this app is
 * intentionally not split into a multi-module reactor.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "X-Request-Id";
    public static final String MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = request.getHeader(HEADER_NAME);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        response.setHeader(HEADER_NAME, requestId);
        ThreadContext.put(MDC_KEY, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            ThreadContext.remove(MDC_KEY);
        }
    }
}

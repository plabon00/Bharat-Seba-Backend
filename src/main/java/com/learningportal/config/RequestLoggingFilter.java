package com.learningportal.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        
        // Skip logging for actuator or swagger to avoid spam
        if (!req.getRequestURI().contains("/actuator") && !req.getRequestURI().contains("/swagger")) {
            System.out.println("INCOMING REQUEST: " + req.getMethod() + " " + req.getRequestURI());
        }
        
        chain.doFilter(request, response);
    }
}

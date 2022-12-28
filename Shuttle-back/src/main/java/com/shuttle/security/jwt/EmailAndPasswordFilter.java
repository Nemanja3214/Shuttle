package com.shuttle.security.jwt;

import jakarta.servlet.*;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class EmailAndPasswordFilter  extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }
}

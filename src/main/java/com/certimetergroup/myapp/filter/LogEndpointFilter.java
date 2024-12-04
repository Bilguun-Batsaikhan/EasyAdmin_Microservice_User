package com.certimetergroup.myapp.filter;

import com.certimetergroup.myapp.resourcemodel.User;
import com.certimetergroup.myapp.service.JWTService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LogEndpointFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(LogEndpointFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Wrapping the response to capture the status
        StatusCaptureResponseWrapper responseWrapper = new StatusCaptureResponseWrapper(res);

        LOG.info("--------- START - GET {}?{} ------", req.getRequestURI(), req.getQueryString());
        LOG.info("Endpoint: {}", req.getRequestURI());
        LOG.info("Method: {}", req.getMethod());
        LOG.info("Query string: {}", req.getQueryString());

        chain.doFilter(request, responseWrapper);

        // Log the status after the chain is executed
        LOG.info("Response status: {}", responseWrapper.getStatus());
        LOG.info("---------- END - GET {} ------", req.getRequestURI());
    }
}

class StatusCaptureResponseWrapper extends HttpServletResponseWrapper {

    private int status;

    public StatusCaptureResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void setStatus(int sc) {
        super.setStatus(sc);
        this.status = sc;
    }

    @Override
    public void sendError(int sc) throws IOException {
        super.sendError(sc);
        this.status = sc;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        super.sendError(sc, msg);
        this.status = sc;
    }

    @Override
    public int getStatus() {
        return this.status != 0 ? this.status : super.getStatus();
    }
}

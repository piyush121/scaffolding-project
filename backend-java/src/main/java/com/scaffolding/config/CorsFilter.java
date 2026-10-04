package com.scaffolding.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.util.regex.Pattern;

/** Allows the frontend (any http://localhost port) to call the API from the browser. */
@Provider
public class CorsFilter implements ContainerResponseFilter {

    private static final Pattern ALLOWED_ORIGIN = Pattern.compile("^http://localhost(:\\d+)?$");

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        String origin = request.getHeaderString("Origin");
        if (origin == null || !ALLOWED_ORIGIN.matcher(origin).matches()) {
            return;
        }
        response.getHeaders().putSingle("Access-Control-Allow-Origin", origin);
        response.getHeaders().putSingle("Vary", "Origin");
        response.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
        response.getHeaders().putSingle("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        String requestedHeaders = request.getHeaderString("Access-Control-Request-Headers");
        if (requestedHeaders != null) {
            response.getHeaders().putSingle("Access-Control-Allow-Headers", requestedHeaders);
        }
        response.getHeaders().putSingle("Access-Control-Max-Age", "3600");
    }
}

package com.marruky.Http;

import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private String body;
    private Map<String, String> headers;

    public HttpRequest(){}

    public HttpRequest(String method, String path, String body, Map<String, String> headers) {
        setMethod(method);
        setPath(path);
        setBody(body);
        setHeaders(headers);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException("Method must be signed");
        }
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Path must be signed");
        }
        this.path = path;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}

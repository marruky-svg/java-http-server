package com.marruky.Http;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpResponse {
    private int statusCode;
    private String statusText;
    private Map<String, String> headers;
    private String body;

    public HttpResponse(int statusCode, String statusText, Map<String, String> headers, String body) {
        setStatusCode(statusCode);
        setStatusText(statusText);
        setHeaders(headers);
        setBody(body);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public byte[] toBytes() {
        byte[] bodyBytes = body.getBytes();
        StringBuilder response = new StringBuilder("HTTP/1.1 " + statusCode + " " + statusText + "\r\n");
        response.append("Content-Length:").append(bodyBytes.length).append("\r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().equals("Content-Length")) {
                continue;
            }
            response.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        response.append("\r\n");
        response.append(body);
        return response.toString().getBytes(StandardCharsets.UTF_8);

    }
}

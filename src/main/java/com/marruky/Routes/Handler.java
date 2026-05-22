package com.marruky.Routes;

import com.marruky.Http.HttpRequest;
import com.marruky.Http.HttpResponse;

@FunctionalInterface
public interface Handler {
    public HttpResponse handle(HttpRequest request);
}


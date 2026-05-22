package com.marruky.Routes;

import com.marruky.Http.HttpRequest;
import com.marruky.Http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private Map<String, Map<String, Handler>> route;

    public Router() {
        route = new HashMap<>();
    }

    public void register(String path, String method, Handler handler) {
        if (route.containsKey(path)) {
            route.get(path).put(method, handler);
        } else {
            Map<String, Handler> handlerMap = new HashMap<>();
            handlerMap.put(method, handler);
            route.put(path, handlerMap);
        }
    }

    public HttpResponse dispatch(HttpRequest request) {
        if (!route.containsKey(request.getPath())) {
            return new HttpResponse(404, "Not Found", new HashMap<>(), "");
        }
        if (!route.get(request.getPath()).containsKey(request.getMethod())) {
            return new HttpResponse(405, "Method not allowed", new HashMap<>(), "");
        }
        return route.get(request.getPath()).get(request.getMethod()).handle(request);
    }
}


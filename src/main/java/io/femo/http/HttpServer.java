package io.femo.http;

import java.io.IOException;

/**
 * Created by felix on 2/24/16.
 */
public abstract class HttpServer {

    public abstract HttpServer listen(int port, boolean ssl) throws IOException;

    public abstract HttpServer route(String method, String path, HttpHandler handler);

    public HttpServer get(String path, HttpHandler handler) {
        return route("GET", path, handler);
    }

    public HttpServer post(String path, HttpHandler handler) {
        return route("POST", path, handler);
    }

    public HttpServer put(String path, HttpHandler handler) {
        return route("PUT", path, handler);
    }

    public HttpServer delete(String path, HttpHandler handler) {
        return route("DELETE", path, handler);
    }
}

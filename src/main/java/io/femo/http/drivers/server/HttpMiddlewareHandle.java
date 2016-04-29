package io.femo.http.drivers.server;

import io.femo.http.*;
import static io.femo.http.HttpRoutable.joinPaths;

/**
 * Created by felix on 4/26/16.
 */
public class HttpMiddlewareHandle implements HttpHandle {

    private String path;
    private HttpMiddleware httpMiddleware;

    @Override
    public boolean matches(HttpRequest request) {
        if(path != null) {
            if(!request.path().equals(path)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean handle(HttpRequest request, HttpResponse response) throws HttpHandleException {
        httpMiddleware.handle(request, response);
        return false;
    }

    @Override
    public void parentPath(String path) {
        this.path = joinPaths(path, this.path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpMiddleware getHttpMiddleware() {
        return httpMiddleware;
    }

    public void setHttpMiddleware(HttpMiddleware httpMiddleware) {
        this.httpMiddleware = httpMiddleware;
    }
}

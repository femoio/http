package io.femo.http.drivers.server;

import io.femo.http.HttpHandleException;
import io.femo.http.HttpHandler;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;
import static io.femo.http.HttpRoutable.joinPaths;

/**
 * Created by felix on 4/25/16.
 */
public class HttpHandlerHandle implements HttpHandle {

    private String method;
    private String path;
    private HttpHandler handler;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpHandler getHandler() {
        return handler;
    }

    public void setHandler(HttpHandler handler) {
        this.handler = handler;
    }

    public boolean matches(HttpRequest request) {
        if(method != null) {
            if(!request.method().equalsIgnoreCase(method)) {
                return false;
            }
        }
        if(path != null) {
            if(!request.path().equals(path)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean handle(HttpRequest request, HttpResponse response) throws HttpHandleException {
        return handler.handle(request, response);
    }

    @Override
    public void parentPath(String path) {
        if(this.path != null)
            this.path = joinPaths(path, this.path);
    }

    @Override
    public void prependPath(String path) {
        this.path = joinPaths(path, this.path);
    }
}

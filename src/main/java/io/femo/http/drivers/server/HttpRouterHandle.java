package io.femo.http.drivers.server;

import io.femo.http.HttpHandleException;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;
import io.femo.http.HttpRouter;

/**
 * Created by Felix Resch on 29-Apr-16.
 */
public class HttpRouterHandle implements HttpHandle {

    private HttpRouter httpRouter;

    @Override
    public boolean matches(HttpRequest request) {
        return httpRouter.matches(request);
    }

    @Override
    public boolean handle(HttpRequest request, HttpResponse response) throws HttpHandleException {
        return httpRouter.handle(request, response);
    }

    @Override
    public void parentPath(String path) {
        httpRouter.parentPath(path);
    }

    @Override
    public void prependPath(String path) {
        this.httpRouter.prependPath(path);
    }

    public void setRouter(HttpRouter httpRouter) {
        this.httpRouter = httpRouter;
    }

    public HttpRouter router() {
        return this.httpRouter;
    }
}

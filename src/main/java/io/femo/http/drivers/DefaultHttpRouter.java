package io.femo.http.drivers;

import io.femo.http.*;
import io.femo.http.drivers.server.HttpHandlerHandle;
import io.femo.http.drivers.server.HttpHandlerStack;
import io.femo.http.drivers.server.HttpMiddlewareHandle;
import io.femo.http.drivers.server.HttpRouterHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.femo.http.HttpRoutable.joinPaths;

/**
 * Created by Felix Resch on 29-Apr-16.
 */
public class DefaultHttpRouter implements HttpRouter {

    private static Logger LOGGER = LoggerFactory.getLogger("HTTP");

    private String parentPath;
    private HttpHandlerStack httpHandlerStack;

    public DefaultHttpRouter() {
        this.httpHandlerStack = new HttpHandlerStack();
    }

    @Override
    public HttpRouter parentPath(String path) {
        this.parentPath = path;
        httpHandlerStack.parentPath(path);
        return this;
    }

    @Override
    public boolean handle(HttpRequest request, HttpResponse response) throws HttpHandleException {
        return httpHandlerStack.handle(request, response);
    }

    @Override
    public HttpRouter use(HttpMiddleware handler) {
        HttpMiddlewareHandle handle = new HttpMiddlewareHandle();
        handle.setHttpMiddleware(handler);
        httpHandlerStack.submit(handle);
        return this;
    }

    @Override
    public HttpRouter use(String path, HttpMiddleware handler) {
        HttpMiddlewareHandle handle = new HttpMiddlewareHandle();
        handle.setPath(path);
        handle.setHttpMiddleware(handler);
        httpHandlerStack.submit(handle);
        return this;
    }

    @Override
    public HttpRouter use(HttpHandler handler) {
        if(handler instanceof HttpRouter) {
            HttpRouterHandle handle = new HttpRouterHandle();
            ((HttpRouter) handler).parentPath(parentPath);
            handle.setRouter((HttpRouter) handler);
            httpHandlerStack.submit(handle);
        } else {
            HttpHandlerHandle handle = new HttpHandlerHandle();
            handle.setHandler(handler);
            httpHandlerStack.submit(handle);
        }
        return this;
    }

    @Override
    public HttpRouter use(String path, HttpHandler httpHandler) {
        if(httpHandler instanceof HttpRouter) {
            HttpRouterHandle handle = new HttpRouterHandle();
            ((HttpRouter) httpHandler).parentPath(joinPaths(parentPath, path));
            handle.setRouter((HttpRouter) httpHandler);
            httpHandlerStack.submit(handle);
        } else {
            HttpHandlerHandle handle = new HttpHandlerHandle();
            handle.setHandler(httpHandler);
            handle.setPath(path);
            httpHandlerStack.submit(handle);
        }
        return this;
    }

    @Override
    public HttpRouter use(String method, String path, HttpHandler httpHandler) {
        if(httpHandler instanceof HttpRouter) {
            LOGGER.warn("Attempting to bind router with method {}. Ignoring", method.toUpperCase());
            return this;
        }
        HttpHandlerHandle handle = new HttpHandlerHandle();
        handle.setHandler(httpHandler);
        handle.setMethod(method);
        handle.setPath(path);
        httpHandlerStack.submit(handle);
        return this;
    }

    @Override
    public HttpRouter after(HttpMiddleware middleware) {
        httpHandlerStack.submitAfter(middleware);
        return this;
    }

    @Override
    public boolean matches(HttpRequest httpRequest) {
        return httpHandlerStack.matches(httpRequest);
    }
}

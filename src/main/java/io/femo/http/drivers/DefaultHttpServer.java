package io.femo.http.drivers;

import io.femo.http.*;
import io.femo.http.drivers.server.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import static io.femo.http.HttpRoutable.joinPaths;

/**
 * Created by felix on 2/24/16.
 */
public class DefaultHttpServer implements HttpServer {

    private int port;
    private boolean ssl;

    private HttpHandlerStack httpHandlerStack;

    private HttpServerThread serverThread;

    public DefaultHttpServer(int port, boolean ssl) {
        this.port = port;
        this.ssl = ssl;
        this.httpHandlerStack = new HttpHandlerStack();
        use((HttpMiddleware) (req, res) -> res.header("Date", ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.RFC_1123_DATE_TIME)));
    }

    @Override
    public HttpServer start() {
        use((request, response) -> {
            response.status(StatusCode.NOT_FOUND);
            response.entity("Could not find resource at " + request.method().toUpperCase() + " " + request.path());
            return true;
        });
        this.serverThread = new HttpServerThread(httpHandlerStack);
        serverThread.setPort(port);
        serverThread.start();
        return this;
    }

    @Override
    public HttpServer stop() {
        this.serverThread.interrupt();
        return this;
    }

    @Override
    public HttpServer use(HttpMiddleware handler) {
        HttpMiddlewareHandle handle = new HttpMiddlewareHandle();
        handle.setHttpMiddleware(handler);
        httpHandlerStack.submit(handle);
        return this;
    }

    @Override
    public HttpServer use(String path, HttpMiddleware handler) {
        HttpMiddlewareHandle handle = new HttpMiddlewareHandle();
        handle.setPath(path);
        handle.setHttpMiddleware(handler);
        httpHandlerStack.submit(handle);
        return this;
    }

    @Override
    public HttpServer use(HttpHandler handler) {
        if(handler instanceof HttpRouter) {
            HttpRouterHandle handle = new HttpRouterHandle();
            ((HttpRouter) handler).parentPath("/");
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
    public HttpServer use(String path, HttpHandler httpHandler) {
        if(httpHandler instanceof HttpRouter) {
            HttpRouterHandle handle = new HttpRouterHandle();
            ((HttpRouter) httpHandler).parentPath(joinPaths("/", path));
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
    public HttpServer use(String method, String path, HttpHandler httpHandler) {
        if(httpHandler instanceof HttpRouter) {
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
    public HttpServer after(HttpMiddleware middleware) {
        httpHandlerStack.submitAfter(middleware);
        return this;
    }

    @Override
    public boolean matches(HttpRequest httpRequest) {
        return httpHandlerStack.matches(httpRequest);
    }

    @Override
    public boolean ready() {
        return this.serverThread.ready();
    }

}

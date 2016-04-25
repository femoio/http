package io.femo.http.drivers;

import io.femo.http.HttpHandler;
import io.femo.http.HttpServer;
import io.femo.http.StatusCode;
import io.femo.http.drivers.server.HttpHandle;
import io.femo.http.drivers.server.HttpHandlerStack;
import io.femo.http.drivers.server.HttpServerThread;

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
    public HttpServer use(HttpHandler handler) {
        HttpHandle handle = new HttpHandle();
        handle.setHandler(handler);
        httpHandlerStack.submit(handle);
        return this;
    }

    @Override
    public HttpServer use(String path, HttpHandler httpHandler) {
        HttpHandle handle = new HttpHandle();
        handle.setHandler(httpHandler);
        handle.setPath(path);
        httpHandlerStack.submit(handle);
        return this;
    }

    @Override
    public HttpServer use(String method, String path, HttpHandler httpHandler) {
        HttpHandle handle = new HttpHandle();
        handle.setHandler(httpHandler);
        handle.setMethod(method);
        handle.setPath(path);
        httpHandlerStack.submit(handle);
        return this;
    }

    @Override
    public boolean ready() {
        return this.serverThread.ready();
    }

}

package io.femo.http.drivers;

import io.femo.http.HttpServer;
import io.femo.http.drivers.server.HttpServerThread;

/**
 * Created by Felix Resch on 25-Apr-16.
 */
public class DefaultHttpServer implements HttpServer {

    private int port;
    private HttpServerThread serverThread;

    public DefaultHttpServer(int port) {
        this.port = port;
    }

    @Override
    public HttpServer start() {
        this.serverThread = new HttpServerThread();
        serverThread.start();
        return this;
    }

    @Override
    public HttpServer stop() {
        this.serverThread.interrupt();
        return this;
    }
}

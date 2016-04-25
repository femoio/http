package io.femo.http.drivers;

import io.femo.http.HttpServer;
import io.femo.http.drivers.server.HttpServerThread;

import java.net.ServerSocket;

/**
 * Created by felix on 2/24/16.
 */
public class DefaultHttpServer implements HttpServer {

    private int port;
    private boolean ssl;


    private HttpServerThread serverThread;

    public DefaultHttpServer(int port, boolean ssl) {
        this.port = port;
        this.ssl = ssl;
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

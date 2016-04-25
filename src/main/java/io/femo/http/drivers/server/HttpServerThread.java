package io.femo.http.drivers.server;

import io.femo.http.HttpRequest;
import io.femo.http.StatusCode;
import io.femo.http.drivers.DefaultHttpResponse;
import io.femo.http.drivers.IncomingHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Created by Felix Resch on 25-Apr-16.
 */
public class HttpServerThread extends Thread {

    private Logger log = LoggerFactory.getLogger("HTTP");

    private ServerSocket serverSocket;
    private HttpHandlerStack httpHandlerStack;
    private int port;
    private boolean ready = false;
    private final Object lock = new Object();

    public HttpServerThread(HttpHandlerStack httpHandlerStack) {
        this.httpHandlerStack = httpHandlerStack;
        setName("HTTP-" + port);
    }

    public void run() {
        log.debug("Starting HTTP Server on port {}", port);
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            log.error("Error while starting HTTP service", e);
        }
        try {
            this.serverSocket.setSoTimeout(20000);
        } catch (SocketException e) {
            log.warn("Could not set timeout. Shutdown may lag a bit...", e);
        }
        while (!isInterrupted()) {
            synchronized (lock) {
                this.ready = true;
            }
            try {
                Socket socket = serverSocket.accept();
                DefaultHttpResponse response = new DefaultHttpResponse();
                HttpRequest httpRequest = IncomingHttpRequest.readFromStream(socket.getInputStream());
                httpHandlerStack.handle(httpRequest, response);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                response.print(byteArrayOutputStream);
                log.debug("Writing {} bytes to {}", byteArrayOutputStream.size(), socket.getRemoteSocketAddress().toString());
                byteArrayOutputStream.writeTo(socket.getOutputStream());
                socket.getOutputStream().flush();
                socket.close();
            } catch (SocketTimeoutException e) {
                log.debug("Socket timeout");
            } catch (IOException e) {
                log.warn("Socket Error", e);
            }
        }
    }

    @Override
    public void interrupt() {
        log.debug("Stopping HTTP Server on port {}", port);
        super.interrupt();
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error("Exception while shutting down server", e);
        }
    }

    public int getPort() {
        return port;
    }

    public boolean ready() {
        synchronized (lock) {
            return this.ready;
        }
    }

    public void setPort(int port) {
        setName("HTTP-" + port);
        this.port = port;
    }
}

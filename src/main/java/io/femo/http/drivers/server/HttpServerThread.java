package io.femo.http.drivers.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Felix Resch on 25-Apr-16.
 */
public class HttpServerThread extends Thread {

    private Logger log = LoggerFactory.getLogger("HTTP");

    private ServerSocket serverSocket;
    private int port;

    public void run() {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            log.error("Error while starting HTTP service", e);
        }
        while (!isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                socket.getOutputStream().write("HTTP/1.1 500 Internal Server Error\r\n\r\n".getBytes());
                socket.close();
            } catch (IOException e) {
                log.error("Socket Error", e);
            }
        }
    }

    public void setPort(int port) {
        this.port = port;
    }
}

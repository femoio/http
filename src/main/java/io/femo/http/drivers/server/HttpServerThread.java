package io.femo.http.drivers.server;

import io.femo.http.HttpRequest;
import io.femo.http.HttpTransport;
import io.femo.http.drivers.DefaultHttpResponse;
import io.femo.http.helper.HttpHelper;
import io.femo.http.helper.HttpSocketOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.*;

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

    private ConcurrentLinkedQueue<Future<?>> futures;

    private static final int CONNECTION_TIMEOUT = 10000;

    public HttpServerThread(HttpHandlerStack httpHandlerStack) {
        this.httpHandlerStack = httpHandlerStack;
        this.futures = new ConcurrentLinkedQueue<>();
        setName("HTTP-" + port);
    }

    public void run() {
        log.debug("Starting HTTP Server on port {}", port);
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            log.error("Error while starting HTTP service", e);
        }
        /*try {
            this.serverSocket.setSoTimeout(20000);
        } catch (SocketException e) {
            log.warn("Could not set timeout. Shutdown may lag a bit...", e);
        }*/
        log.debug("Starting Executor Service");
        ExecutorService executorService = Executors.newCachedThreadPool(new HttpThreadFactory(port));
        while (!isInterrupted()) {
            synchronized (lock) {
                this.ready = true;
            }
            try {
                Socket socket = serverSocket.accept();
                futures.add(executorService.submit(new SocketHandler(socket)));
            } catch (SocketTimeoutException e) {
                log.debug("Socket timeout");
            } catch (IOException e) {
                log.warn("Socket Error", e);
            }
        }
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Had to perform dirty shutdown, not all clients might have been served!", e);
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

    private class SocketHandler implements Runnable {

        private Socket socket;

        private SocketHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            boolean run = true;
            try {
                socket.setSoTimeout(CONNECTION_TIMEOUT);
            } catch (SocketException e) {
                log.warn("Error while setting timeout!", e);
            }
            while (run) {
                try {
                    run = false;
                    HttpHelper.get().add(new HttpSocketOptions());
                    DefaultHttpResponse response = new DefaultHttpResponse();
                    HttpRequest httpRequest = HttpTransport.def().readRequest(socket.getInputStream());
                    long start = System.currentTimeMillis();
                    HttpHelper.remote(socket.getRemoteSocketAddress());
                    HttpHelper.request(httpRequest);
                    HttpHelper.response(response);
                    HttpHelper.get().add(socket);
                    httpHandlerStack.handle(httpRequest, response);
                    if (httpRequest.hasHeader("Connection") && !response.hasHeader("Connection") && httpRequest.header("Connection").value().equals("keep-alive")) {
                        response.header("Connection", "keep-alive");
                        run = true;
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    response.print(byteArrayOutputStream);
                    log.debug("Writing {} bytes to {}", byteArrayOutputStream.size(), socket.getRemoteSocketAddress().toString());
                    byteArrayOutputStream.writeTo(socket.getOutputStream());
                    socket.getOutputStream().flush();
                    HttpSocketOptions httpSocketOptions = HttpHelper.get().getFirst(HttpSocketOptions.class).get();
                    if (httpSocketOptions.isClose())
                        socket.close();
                    if (httpSocketOptions.hasHandledCallback()) {
                        httpSocketOptions.callHandledCallback();
                    }
                    log.info("Took {} ms to handle request", (System.currentTimeMillis() - start));
                    HttpHelper.get().reset();
                } catch (SocketTimeoutException e) {
                    log.debug("Connection timed out with " + socket.getRemoteSocketAddress().toString());
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        log.warn("Could not close socket", e1);
                    }
                } catch (IOException e) {
                    log.warn("Socket Error", e);
                }
            }
        }
    }
}

package io.femo.http.drivers;

import io.femo.http.HttpHandler;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;
import io.femo.http.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * Created by felix on 2/24/16.
 */
public class DefaultHttpServer extends HttpServer {

    private ServerSocket serverSocket;

    private List<HttpHandlerWrapper> httpHandlers;

    @Override
    public HttpServer listen(int port, boolean ssl) throws IOException {
        serverSocket = new ServerSocket(port);
        startListeners();
        return this;
    }

    private void startListeners() {

    }

    @Override
    public HttpServer route(String method, String path, HttpHandler handler) {
        HttpHandlerWrapper wrapper = new HttpHandlerWrapper();
        wrapper.setHandler(handler);
        wrapper.setMethod(method);
        wrapper.setPath(path);
        httpHandlers.add(wrapper);
        return this;
    }


    private class HttpHandlerWrapper {

        private String path;
        private String method;
        private HttpHandler handler;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public HttpHandler getHandler() {
            return handler;
        }

        public void setHandler(HttpHandler handler) {
            this.handler = handler;
        }
    }

    private class HttpHandlerThread extends Thread {

        public void run() {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    InputStream inputStream = socket.getInputStream();
                    HttpRequest request = IncomingHttpRequest.readFromStream(inputStream);
                    HttpResponse response = new DefaultHttpResponse();
                    for(HttpHandlerWrapper wrapper : httpHandlers) {
                        if(wrapper.getPath().equals(request.path())) {
                            wrapper.getHandler().handle(request, response, null);
                            break;
                        }
                    }
                    response.print(socket.getOutputStream());
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

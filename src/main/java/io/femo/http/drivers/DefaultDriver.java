package io.femo.http.drivers;

import io.femo.http.HttpDriver;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;
import io.femo.http.HttpServer;

import java.net.URL;

/**
 * Created by felix on 9/11/15.
 */
public class DefaultDriver extends HttpDriver {

    @Override
    public HttpRequest url(URL url) {
        HttpRequest request = new DefaultHttpRequest(url);
        if(url.getProtocol().toLowerCase().equals("https")) {
            request.https();
        }
        return request;
    }

    @Override
    public HttpServer server(int port, boolean ssl) {
        return new DefaultHttpServer(port, ssl);
    }
}

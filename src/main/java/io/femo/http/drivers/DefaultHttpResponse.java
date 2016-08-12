package io.femo.http.drivers;

import io.femo.http.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by felix on 9/11/15.
 */
public class DefaultHttpResponse extends HttpResponse {

    private StatusCode statusCode;
    private Map<String, HttpHeader> headers;
    private Map<String, HttpCookie> cookies;
    private byte[] entity;
    private HttpTransport httpTransport;

    private InputStream entityStream;
    private HttpRequest request;

    public DefaultHttpResponse() {
        this.headers = new HashMap<>();
        this.cookies = new HashMap<>();
    }

    @Override
    public HttpResponse status(StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    @Override
    public HttpResponse entity(String entity) {
        return entity(entity.getBytes());
    }

    @Override
    public HttpResponse entity(byte[] entity) {
        header("Content-Length", String.valueOf(entity.length));
        if(!hasHeader("Content-Type")) {
            header("Content-Type", "text/plain");
        }
        if(statusCode == null) {
            statusCode = StatusCode.OK;
        }
        this.entity = entity;
        return this;
    }

    @Override
    public HttpResponse entity(InputStream inputStream) {
        if(!hasHeader("Content-Type")) {
            header("Content-Type", "text/plain");
        }
        if(statusCode == null) {
            statusCode = StatusCode.OK;
        }
        this.entityStream = inputStream;
        return this;
    }

    public boolean hasHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public HttpResponse header(String name, String value) {
        this.headers.put(name, new HttpHeader(name, value));
        return this;
    }

    @Override
    public HttpCookie cookie(String name) {
        return cookies.get(name);
    }

    public boolean hasCookie(String name) {
        return cookies.containsKey(name);
    }

    @Override
    public HttpResponse cookie(String name, String value) {
        this.cookies.put(name, new HttpCookie(name, value));
        return this;
    }

    @Override
    public Collection<HttpCookie> cookies() {
        return cookies.values();
    }

    @Override
    public void print(OutputStream outputStream) {
        if(httpTransport == null) {
            httpTransport = HttpTransport.def();
        }
        httpTransport.write(this, outputStream, entityStream);
    }

    public String statusLine() {
        return status().status() + " " + status().statusMessage();
    }

    @Override
    public Collection<HttpHeader> headers() {
        return headers.values();
    }

    @Override
    public StatusCode status() {
        return statusCode;
    }

    @Override
    public String responseString() {
        return new String(entity);
    }

    @Override
    public byte[] responseBytes() {
        return entity;
    }

    @Override
    public void request(HttpRequest request) {
        this.request = request;
    }

    @Override
    public HttpRequest request() {
        return this.request;
    }

    @Override
    public HttpHeader header(String name) {
        return headers.get(name);
    }

}

package io.femo.http;

import io.femo.http.events.HttpEventHandler;
import io.femo.http.events.HttpEventManager;
import io.femo.http.events.HttpEventType;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Created by felix on 9/10/15.
 */
public abstract class HttpRequest {

    public abstract HttpRequest method(String method);
    public abstract HttpRequest cookie(String name, String value);
    public abstract HttpRequest header(String name, String value);
    public abstract HttpRequest entity(byte[] entity);
    public abstract HttpRequest entity(String entity);
    public abstract HttpRequest entity(Object entity);
    public abstract HttpRequest basicAuth(String username, String password);
    public abstract HttpRequest execute(HttpResponseCallback callback);
    public abstract HttpRequest transport(Transport transport);
    public abstract HttpRequest version(HttpVersion version);
    public abstract HttpRequest print(PrintStream printStream);
    public abstract HttpRequest data(String key, String value);
    public abstract HttpRequest eventManager(HttpEventManager manager);
    public abstract HttpRequest event(HttpEventType type, HttpEventHandler handler);

    public abstract String method();
    public abstract HttpCookie[] cookies();
    public abstract HttpHeader[] headers();
    public abstract byte[] entityBytes();
    public abstract String entityString();
    public abstract boolean checkAuth(String username, String password);
    public abstract HttpResponse response();

    public abstract Transport transport();
    public abstract String requestLine();

    public HttpRequest execute() {
        return execute(null);
    }

    public HttpRequest contentType(String contentType) {
        return header("Content-Type", contentType);
    }

    public HttpRequest https() {
        return transport(Transport.HTTPS);
    }

}

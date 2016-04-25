package io.femo.http;

import java.io.OutputStream;

/**
 * Created by felix on 9/10/15.
 */
public abstract class HttpResponse {

    public abstract HttpResponse status(StatusCode statusCode);
    public abstract HttpResponse entity(String entity);
    public abstract HttpResponse entity(byte[] entity);


    public abstract StatusCode status();
    public abstract String responseString();
    public abstract byte[] responseBytes();

    public abstract HttpHeader header(String name);
    public abstract boolean hasHeader(String name);
    public abstract HttpResponse header(String name, String value);

    public abstract HttpCookie cookie(String name);
    public abstract boolean hasCookie(String name);
    public abstract HttpResponse cookie(String name, String value);

    public int statusCode() {
        return status().status();
    }

    public abstract void print(OutputStream outputStream);
    public abstract String statusLine();
}

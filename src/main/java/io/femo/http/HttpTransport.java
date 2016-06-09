package io.femo.http;

import io.femo.http.transport.Http11Transport;
import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by felix on 6/9/16.
 */
public interface HttpTransport extends Driver {

    ThreadLocal<HttpTransport> version11 = new ReadonlyThreadLocal<HttpTransport>() {
        @Contract(" -> !null")
        @Override
        protected HttpTransport initialValue() {
            return new Http11Transport();
        }

    };

    void write(HttpRequest httpRequest, OutputStream outputStream);
    void write(HttpResponse httpResponse, OutputStream outputStream, InputStream entityStream);

    HttpRequest readRequest(InputStream inputStream) throws IOException;
    HttpResponse readResponse(InputStream inputStream, OutputStream pipe) throws IOException;

    static HttpTransport version11() {
        return version11.get();
    }

    static HttpTransport def() {
        return version11();
    }
}

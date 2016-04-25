package io.femo.http.drivers.server;

import io.femo.http.HttpHandleException;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by felix on 4/25/16.
 */
public class HttpHandlerStack {

    private List<HttpHandle> httpHandles;

    public HttpHandlerStack() {
        this.httpHandles = new ArrayList<>();
    }

    public void submit(HttpHandle httpHandle) {
        this.httpHandles.add(httpHandle);
    }

    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        for (HttpHandle httpHandle : httpHandles) {
            if (httpHandle.matches(httpRequest)) {
                try {
                    if (httpHandle.getHandler().handle(httpRequest, httpResponse)) {
                        break;
                    }
                } catch (HttpHandleException e) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    e.printStackTrace(new PrintStream(byteArrayOutputStream));
                    httpResponse.status(e.getStatusCode());
                    httpResponse.entity(byteArrayOutputStream.toByteArray());
                }
            }
        }
    }
}

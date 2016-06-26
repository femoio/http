package io.femo.http.drivers.server;

import io.femo.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by felix on 4/25/16.
 */
public class HttpHandlerStack {

    private Logger log = LoggerFactory.getLogger("HTTP");

    private List<HttpHandle> httpHandlerHandles;

    private List<HttpMiddleware> after;

    public HttpHandlerStack() {
        this.httpHandlerHandles = new ArrayList<>();
        this.after = new ArrayList<>();
    }

    public void submit(HttpHandle httpHandle) {
        this.httpHandlerHandles.add(httpHandle);
    }

    public void submitAfter(HttpMiddleware httpMiddleware) {
        after.add(httpMiddleware);
    }

    public boolean handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        boolean handled = false;
        try {
            for (HttpHandle httpHandle : httpHandlerHandles) {
                if (httpHandle.matches(httpRequest)) {
                    try {
                        if (httpHandle.handle(httpRequest, httpResponse)) {
                            handled = true;
                            break;
                        }
                    } catch (HttpHandleException e) {
                        log.warn("Error while handling HTTP request", e);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        e.printStackTrace(new PrintStream(byteArrayOutputStream));
                        httpResponse.status(e.getStatusCode());
                        httpResponse.entity(byteArrayOutputStream.toByteArray());
                        handled = true;
                        break;
                    }
                }
            }
        } catch (Throwable t) {
            log.error("Error while handling " + httpRequest.method() + " " + httpRequest.path(), t);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            t.printStackTrace(new PrintStream(byteArrayOutputStream));
            httpResponse.entity(byteArrayOutputStream.toByteArray());
            httpResponse.status(StatusCode.INTERNAL_SERVER_ERROR);
        }
        try {
            for (HttpMiddleware middleware : after) {
                try {
                    middleware.handle(httpRequest, httpResponse);
                } catch (HttpHandleException e) {
                    log.warn("Error while performing finalizing operations on HTTP request", e);
                }
            }
        } catch (Throwable t) {
            log.error("Error while finishing " + httpRequest.method() + " " + httpRequest.path(), t);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            t.printStackTrace(new PrintStream(byteArrayOutputStream));
            httpResponse.entity(byteArrayOutputStream.toByteArray());
            httpResponse.status(StatusCode.INTERNAL_SERVER_ERROR);
        }
        return handled;
    }

    public boolean matches(HttpRequest httpRequest) {
        for (HttpHandle httpHandle : httpHandlerHandles) {
            if (httpHandle.matches(httpRequest)) {
                return true;
            }
        }
        return false;
    }

    public void parentPath(String path) {
        httpHandlerHandles.forEach(httpHandle -> httpHandle.parentPath(path));
    }

    public void prependPath(String parentPath) {
        httpHandlerHandles.forEach(httpHandle -> httpHandle.prependPath(parentPath));
    }
}

package io.femo.http.drivers.server;

import io.femo.http.HttpHandleException;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;

/**
 * Created by felix on 4/26/16.
 */
public interface HttpHandle {

    boolean matches(HttpRequest request);

    boolean handle(HttpRequest request, HttpResponse response) throws HttpHandleException;

    void parentPath(String path);

    void prependPath(String path);
}

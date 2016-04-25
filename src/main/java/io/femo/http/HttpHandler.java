package io.femo.http;


import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;

/**
 * Created by Felix Resch on 25-Apr-16.
 */
@FunctionalInterface
public interface HttpHandler {

    boolean handle(HttpRequest request, HttpResponse response) throws HttpHandleException;
}

package io.femo.http;

/**
 * Created by felix on 2/24/16.
 */
public interface HttpHandler {

    void handle(HttpRequest request, HttpResponse response, HttpHandler next);
}

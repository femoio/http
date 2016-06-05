package io.femo.http;

/**
 * Created by Felix Resch on 25-Apr-16.
 */
public interface HttpServer extends HttpRoutable<HttpServer> {

    HttpServer start();
    HttpServer stop();

    boolean ready();
}

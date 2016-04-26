package io.femo.http;

/**
 * Created by Felix Resch on 25-Apr-16.
 */
public interface HttpServer {

    HttpServer start();
    HttpServer stop();

    HttpServer use(HttpMiddleware handler);
    HttpServer use(String path, HttpMiddleware handler);

    HttpServer use(HttpHandler handler);
    HttpServer use(String path, HttpHandler httpHandler);
    HttpServer use(String method, String path, HttpHandler httpHandler);

    HttpServer after(HttpMiddleware middleware);

    default HttpServer get(String path, HttpHandler httpHandler) {
        return use(Http.GET, path, httpHandler);
    }
    default HttpServer post(String path, HttpHandler httpHandler) {
        return use(Http.POST, path, httpHandler);
    }
    default HttpServer put(String path, HttpHandler httpHandler) {
        return use(Http.PUT, path, httpHandler);
    }
    default HttpServer delete(String path, HttpHandler httpHandler) {
        return use(Http.DELETE, path, httpHandler);
    }
    default HttpServer update(String path, HttpHandler httpHandler) {
        return use(Http.UPDATE, path, httpHandler);
    }
    default HttpServer patch(String path, HttpHandler httpHandler) {
        return use(Http.PATCH, path, httpHandler);
    }

    boolean ready();
}

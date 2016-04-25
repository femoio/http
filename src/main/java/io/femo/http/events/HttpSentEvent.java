package io.femo.http.events;

import io.femo.http.HttpRequest;

/**
 * Created by felix on 2/11/16.
 */
public class HttpSentEvent extends HttpEvent {

    private HttpRequest request;

    public HttpSentEvent(HttpRequest request) {
        super(HttpEventType.SENT);
        this.request = request;
    }

    public HttpRequest request() {
        return request;
    }
}

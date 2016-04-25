package io.femo.http.events;

import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;

/**
 * Created by felix on 2/11/16.
 */
public class HttpReceivedEvent extends HttpEvent {

    private HttpRequest request;
    private HttpResponse response;

    public HttpReceivedEvent(HttpRequest request, HttpResponse response) {
        super(HttpEventType.RECEIVED);
        this.request = request;
        this.response = response;
    }

    public HttpRequest request() {
        return request;
    }

    public HttpResponse response() {
        return response;
    }
}

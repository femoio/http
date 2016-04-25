package io.femo.http.events;

import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;

/**
 * Created by felix on 2/11/16.
 */
public class HttpHandledEvent extends HttpEvent {

    private HttpRequest request;
    private HttpResponse response;
    private boolean callbackExecuted;

    public HttpHandledEvent(HttpRequest request, HttpResponse response, boolean callbackExecuted) {
        super(HttpEventType.HANDLED);
        this.request = request;
        this.response = response;
        this.callbackExecuted = callbackExecuted;
    }

    public HttpRequest request() {
        return request;
    }

    public HttpResponse response() {
        return response;
    }

    public boolean callbackExecuted() {
        return callbackExecuted;
    }
}

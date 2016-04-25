package io.femo.http.events;

/**
 * Created by felix on 2/11/16.
 */
public abstract class HttpEvent {

    private HttpEventType httpEventType;

    public HttpEvent(HttpEventType httpEventType) {
        this.httpEventType = httpEventType;
    }

    public HttpEventType eventType() {
        return httpEventType;
    }

    public void eventType(HttpEventType httpEventType) {
        this.httpEventType = httpEventType;
    }
}

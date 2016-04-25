package io.femo.http;

/**
 * Created by felix on 4/25/16.
 */
public class HttpHandleException extends Exception {

    private StatusCode statusCode;

    public HttpHandleException(StatusCode statusCode) {
        super();
        this.statusCode = statusCode;
    }

    public HttpHandleException(StatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpHandleException(StatusCode statusCode, String s, Throwable throwable) {
        super(s, throwable);
        this.statusCode = statusCode;
    }

    public HttpHandleException() {
        this.statusCode = StatusCode.INTERNAL_SERVER_ERROR;
    }

    public HttpHandleException(String s) {
        super(s);
        this.statusCode = StatusCode.INTERNAL_SERVER_ERROR;
    }

    public HttpHandleException(String s, Throwable throwable) {
        super(s, throwable);
        this.statusCode = StatusCode.INTERNAL_SERVER_ERROR;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}

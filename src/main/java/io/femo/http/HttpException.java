package io.femo.http;

import java.io.PrintStream;

/**
 * Created by felix on 1/19/16.
 */
public class HttpException extends RuntimeException {

    private HttpRequest request;

    public HttpException(HttpRequest request, Exception cause) {
        super(cause);
        this.request = request;
    }

    public HttpException(HttpRequest request, String message) {
        super(message);
        this.request = request;
    }

    @Override
    public void printStackTrace(PrintStream printStream) {
        if(request != null)
            request.print(printStream);
        super.printStackTrace(printStream);
    }
}

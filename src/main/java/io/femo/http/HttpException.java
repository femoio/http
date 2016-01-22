package io.femo.http;

import java.io.PrintStream;

/**
 * Created by felix on 1/19/16.
 */
public class HttpException extends RuntimeException {

    private HttpRequest request;

    public HttpException(HttpRequest request, Exception cause) {
        super(cause);
    }

    @Override
    public void printStackTrace(PrintStream printStream) {
        if(request != null)
            request.print(printStream);
        super.printStackTrace(printStream);
    }
}

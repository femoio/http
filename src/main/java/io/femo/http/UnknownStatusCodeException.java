package io.femo.http;

/**
 * Created by felix on 9/10/15.
 */
public class UnknownStatusCodeException extends RuntimeException {

    public UnknownStatusCodeException(int status) {
        super("Unknown status " + status);
    }
}

package io.femo.http;

/**
 * Created by felix on 6/8/16.
 */
public interface HttpContext {

    Base64Driver base64();

    void useDriver(Driver driver);

    MimeService mime();

    Environment environment();
}

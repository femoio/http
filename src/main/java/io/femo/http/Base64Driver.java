package io.femo.http;

/**
 * Created by Felix Resch on 08-Apr-16.
 */
public interface Base64Driver extends Driver {

    String encodeToString(byte[] data);
    byte[] decodeFromString(String data);
}

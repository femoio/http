package io.femo.http.drivers;

import io.femo.http.Base64Driver;

import javax.xml.bind.DatatypeConverter;

/**
 * Created by Felix Resch on 08-Apr-16.
 */
public class DefaultBase64Driver implements Base64Driver {
    @Override
    public String encodeToString(byte[] data) {
        return DatatypeConverter.printBase64Binary(data);
    }

    @Override
    public byte[] decodeFromString(String data) {
        return DatatypeConverter.parseBase64Binary(data);
    }
}

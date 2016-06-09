package io.femo.http.transport;

import io.femo.http.Transport;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by felix on 2/7/16.
 */
public class HttpTransport implements Transport {

    @Override
    public Socket openSocket(String host, int port) throws IOException {
        return new Socket(host, port);
    }
}

package io.femo.http.transport;

import io.femo.http.Transport;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by felix on 2/7/16.
 */
public class HttpsTransport implements Transport {

    private static SocketFactory sslSocketFactory;

    @Override
    public Socket openSocket(String host, int port) throws IOException {
        return sslSocketFactory.createSocket(host, port);
    }

    static {
        sslSocketFactory = SSLSocketFactory.getDefault();
    }
}

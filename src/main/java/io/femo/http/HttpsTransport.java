package io.femo.http;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by felix on 2/7/16.
 */
public class HttpsTransport implements Transport {
    @Override
    public Socket openSocket(String host, int port) throws IOException {
        return SSLSocketFactory.getDefault().createSocket(host, port);
    }
}

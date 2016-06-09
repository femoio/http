package io.femo.http;

import io.femo.http.transport.HttpTransport;
import io.femo.http.transport.HttpsTransport;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by felix on 9/10/15.
 */
public interface Transport {

    Transport HTTP = new HttpTransport();
    Transport HTTPS = new HttpsTransport();

    Socket openSocket(String host, int port) throws IOException;
}

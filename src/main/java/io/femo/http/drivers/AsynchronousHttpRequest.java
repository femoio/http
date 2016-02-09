package io.femo.http.drivers;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import io.femo.http.HttpException;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponseCallback;
import io.femo.http.Transport;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;

/**
 * Created by felix on 2/9/16.
 */
public class AsynchronousHttpRequest extends DefaultHttpRequest {

    public AsynchronousHttpRequest(URL url) {
        super(url);
    }

    @Override
    public HttpRequest execute(HttpResponseCallback callback) {
        Thread thread = new Thread(getRunnable(callback));
        thread.start();
        return this;
    }

    protected Runnable getRunnable(final HttpResponseCallback callback) {
        return () -> {
            int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
            DefaultHttpResponse response = null;
            if(transport() == Transport.HTTP) {
                try {
                    Socket socket = new Socket(url.getHost(), port);
                    PrintStream printStream = new PrintStream(socket.getOutputStream());
                    print(printStream);
                    response = DefaultHttpResponse.read(socket.getInputStream());
                } catch (IOException e) {
                    throw new HttpException(AsynchronousHttpRequest.this, e);
                }
            } else {
                //TODO with Transport.openSocket();
            }
            if(callback != null)
                callback.receivedResponse(response);
        };
    }
}

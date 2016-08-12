package io.femo.http.drivers;

import io.femo.http.*;

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
        return new Runnable() {
            @Override
            public void run() {
                int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
                HttpResponse response = null;
                try {
                    Socket socket = transport().openSocket(url.getHost(), port);
                    PrintStream printStream = new PrintStream(socket.getOutputStream());
                    print(printStream);
                    response = HttpTransport.def().readResponse(socket.getInputStream(), null);
                } catch (IOException e) {
                    throw new HttpException(AsynchronousHttpRequest.this, e);
                }
                if(callback != null)
                    callback.receivedResponse(response);
            }
        };
    }
}

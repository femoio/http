package io.femo.http.drivers.server;

import java.util.concurrent.ThreadFactory;

/**
 * Created by Felix Resch on 29-Apr-16.
 */
public class HttpThreadFactory implements ThreadFactory {

    private int port;
    private int counter = 0;

    public HttpThreadFactory(int port) {
        this.port = port;
    }

    @Override
    public Thread newThread(Runnable r) {
        HttpThread httpThread = new HttpThread(r);
        httpThread.setName(String.format("pool-%04d-thread-%03d", port, counter++));
        httpThread.setDaemon(true);
        httpThread.setPriority(Thread.MAX_PRIORITY);
        return httpThread;
    }
}

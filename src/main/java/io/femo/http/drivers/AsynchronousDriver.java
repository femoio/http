package io.femo.http.drivers;

import io.femo.http.HttpDriver;
import io.femo.http.HttpRequest;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by felix on 2/9/16.
 */
public class AsynchronousDriver extends DefaultDriver {

    private ExecutorService executorService;

    public AsynchronousDriver() {
        executorService = null;
    }

    public AsynchronousDriver(int threads) {
        this.executorService = Executors.newFixedThreadPool(threads);
    }

    @Override
    public HttpRequest url(URL url) {
        if(executorService == null)
            return new AsynchronousHttpRequest(url);
        return new AsynchronousExecutorHttpRequest(url, executorService);
    }
}

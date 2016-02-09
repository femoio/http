package io.femo.http.drivers;

import io.femo.http.HttpRequest;
import io.femo.http.HttpResponseCallback;

import java.net.URL;
import java.util.concurrent.ExecutorService;

/**
 * Created by felix on 2/9/16.
 */
public class AsynchronousExecutorHttpRequest extends AsynchronousHttpRequest {

    private ExecutorService executorService;

    public AsynchronousExecutorHttpRequest(URL url, ExecutorService executorService) {
        super(url);
        this.executorService = executorService;
    }

    @Override
    public HttpRequest execute(HttpResponseCallback callback) {
        executorService.submit(getRunnable(callback));
        return this;
    }
}

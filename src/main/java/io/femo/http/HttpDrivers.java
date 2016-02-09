package io.femo.http;

import io.femo.http.drivers.AsynchronousDriver;
import io.femo.http.drivers.DefaultDriver;

/**
 * Created by felix on 2/9/16.
 */
public final class HttpDrivers {

    public static HttpDriver defaultDriver() {
        return new DefaultDriver();
    }

    public static HttpDriver asyncDriver() {
        return new AsynchronousDriver();
    }

    public static HttpDriver asyncDriver(int threads) {
        return new AsynchronousDriver(threads);
    }

}

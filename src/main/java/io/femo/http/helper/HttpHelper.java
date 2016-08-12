package io.femo.http.helper;

import io.femo.http.Driver;
import io.femo.http.HttpContext;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;
import io.femo.http.drivers.server.HttpThread;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.xjs.dynamic.Pluggable;

import java.net.Socket;
import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Felix Resch on 29-Apr-16.
 */
public class HttpHelper {

    private static ThreadLocal<HttpContext> context = new ThreadLocal<HttpContext>() {
        @Contract(" -> !null")
        @Override
        protected HttpContext initialValue() {
            return new DefaultHttpContext();
        }
    };

    @Nullable
    public static HttpResponse response() {
        if(Thread.currentThread() instanceof Pluggable) {
            Optional<HttpResponse> httpResponse = ((Pluggable<HttpThread>) Thread.currentThread()).getFirst(HttpResponse.class);
            if(httpResponse.isPresent()) {
                return httpResponse.get();
            }
        }
        return null;
    }

    @Nullable
    public static HttpRequest request() {
        if(Thread.currentThread() instanceof Pluggable) {
            Optional<HttpRequest> httpRequest = ((Pluggable<HttpThread>) Thread.currentThread()).getFirst(HttpRequest.class);
            if(httpRequest.isPresent()) {
                return httpRequest.get();
            }
        }
        return null;
    }

    public static void response(HttpResponse response) {
        if(Thread.currentThread() instanceof Pluggable) {
            Pluggable Pluggable = (Pluggable) Thread.currentThread();
            Pluggable.removeAll(HttpResponse.class);
            Pluggable.add(response);
        }
    }

    public static void request(HttpRequest request) {
        if(Thread.currentThread() instanceof Pluggable) {
            Pluggable Pluggable = (Pluggable) Thread.currentThread();
            Pluggable.removeAll(HttpRequest.class);
            Pluggable.add(request);
        }
    }

    @Nullable
    public static SocketAddress remote() {
        if(Thread.currentThread() instanceof Pluggable) {
            Optional<SocketAddress> socketAddress = ((Pluggable) Thread.currentThread()).getFirst(SocketAddress.class);
            if(socketAddress.isPresent()) {
                return socketAddress.get();
            }
        }
        return null;
    }

    public static void remote(SocketAddress socketAddress) {
        if(Thread.currentThread() instanceof Pluggable) {
            Pluggable Pluggable = (Pluggable) Thread.currentThread();
            Pluggable.removeAll(SocketAddress.class);
            Pluggable.add(socketAddress);
        }
    }

    public static HttpContext context() {
        return context.get();
    }

    public static void useDriver(Driver driver) {
        context.get().useDriver(driver);
    }

    @Nullable
    public static Pluggable<HttpThread> get() {
        if(Thread.currentThread() instanceof Pluggable) {
            return (Pluggable<HttpThread>) Thread.currentThread();
        } else {
            return null;
        }
    }

    public static void keepOpen() {
        if(Thread.currentThread() instanceof Pluggable) {
            ((HttpSocketOptions)((Pluggable) Thread.currentThread()).getFirst(HttpSocketOptions.class).get()).setClose(false);
        }
    }

    public static void callback(HandledCallback handledCallback) {
        if(Thread.currentThread() instanceof Pluggable) {
            ((HttpSocketOptions)((Pluggable) Thread.currentThread()).getFirst(HttpSocketOptions.class).get()).setHandledCallback(handledCallback);
        }
    }
}

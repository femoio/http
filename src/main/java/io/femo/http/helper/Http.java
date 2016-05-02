package io.femo.http.helper;

import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;
import org.jetbrains.annotations.Nullable;
import org.xjs.dynamic.PlugableObject;

import java.net.Socket;
import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;

/**
 * Created by Felix Resch on 29-Apr-16.
 */
public class Http {

    @Nullable
    public static HttpResponse response() {
        if(Thread.currentThread() instanceof PlugableObject) {
            Optional<HttpResponse> httpResponse = ((PlugableObject) Thread.currentThread()).getFirst(HttpResponse.class);
            if(httpResponse.isPresent()) {
                return httpResponse.get();
            }
        }
        return null;
    }

    @Nullable
    public static HttpRequest request() {
        if(Thread.currentThread() instanceof PlugableObject) {
            Optional<HttpRequest> httpRequest = ((PlugableObject) Thread.currentThread()).getFirst(HttpRequest.class);
            if(httpRequest.isPresent()) {
                return httpRequest.get();
            }
        }
        return null;
    }

    public static void response(HttpResponse response) {
        if(Thread.currentThread() instanceof PlugableObject) {
            PlugableObject plugableObject = (PlugableObject) Thread.currentThread();
            plugableObject.removeAll(HttpResponse.class);
            plugableObject.add(response);
        }
    }

    public static void request(HttpRequest request) {
        if(Thread.currentThread() instanceof PlugableObject) {
            PlugableObject plugableObject = (PlugableObject) Thread.currentThread();
            plugableObject.removeAll(HttpRequest.class);
            plugableObject.add(request);
        }
    }

    @Nullable
    public static SocketAddress remote() {
        if(Thread.currentThread() instanceof PlugableObject) {
            Optional<SocketAddress> socketAddress = ((PlugableObject) Thread.currentThread()).getFirst(SocketAddress.class);
            if(socketAddress.isPresent()) {
                return socketAddress.get();
            }
        }
        return null;
    }

    public static void remote(SocketAddress socketAddress) {
        if(Thread.currentThread() instanceof PlugableObject) {
            PlugableObject plugableObject = (PlugableObject) Thread.currentThread();
            plugableObject.removeAll(SocketAddress.class);
            plugableObject.add(socketAddress);
        }
    }

    @Nullable
    public static PlugableObject get() {
        if(Thread.currentThread() instanceof PlugableObject) {
            return (PlugableObject) Thread.currentThread();
        } else {
            return null;
        }
    }
}

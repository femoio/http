package io.femo.http.helper;

import io.femo.http.HttpHandler;
import io.femo.http.HttpRequest;
import io.femo.http.HttpResponse;
import io.femo.http.StatusCode;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Created by felix on 5/10/16.
 */
public final class HttpCacheControl {

    /**
     * Verifies the supplied cache information with the currently processed http request
     *
     * If no request is present on the current thread, this method will return <strong>false</strong>
     *
     * The required headers will automatically be validated and added. This method will return true if the server can
     * respond with a 302 - Not Modified or false if the resource has to be sent.
     *
     * @param maxAge the maximum age of a resource in the browsers cache in seconds
     * @param eTag a identifier that uniquely identifies a version of a resource
     * @return whether the server can respond with a 302 - Not Modified
     */
    public static boolean cacheControl(int maxAge, String eTag) {
        return cacheControl(Http.request(), Http.response(), maxAge, eTag);
    }

    /**
     * Creates an http handler that performs cache control on the provided http request
     *
     * The required headers will automatically be validated and added. This handler will terminate the http handling
     * queue and sent a 302 - Not Modified if the resource hasn't been modified.
     *
     * The suppliers should be thread safe and are required to provide steady values.
     *
     * @param maxAge the maximum age of a resource in the browsers cache in seconds
     * @param eTag a identifier that uniquely identifies a version of a resource
     * @return an http handler that performs cache control operations
     */
    @NotNull
    @Contract(pure = true)
    public static HttpHandler cacheControl(Supplier<Integer> maxAge, Supplier<String> eTag) {
        return (req, res) -> {
            if(cacheControl(req, res, maxAge.get(), eTag.get())) {
                res.status(StatusCode.NOT_MODIFIED);
                return true;
            }
            return false;
        };
    }

    /**
     * Verifies the supplied cache information with the currently processed http request
     *
     * If no request is present on the current thread, this method will return <strong>false</strong>
     *
     * The required headers will automatically be validated and added. This method will return true if the server can
     * respond with a 302 - Not Modified or false if the resource has to be sent.
     *
     * @param maxAge the maximum age of a resource in the browsers cache in seconds
     * @param eTag a identifier that uniquely identifies a version of a resource
     * @param request the request that should be validated
     * @param response the response that will be sent to the client
     * @return whether the server can respond with a 302 - Not Modified
     */
    public static boolean cacheControl(HttpRequest request, HttpResponse response, int maxAge, String eTag) {
        response.header("Cache-Control", "max-age=" + maxAge);
        response.header("ETag", eTag);
        if(request.hasHeader("If-None-Match")) {
            if(request.header("If-None-Match").equals(eTag)) {
                response.status(StatusCode.NOT_MODIFIED);
                return true;
            }
        }
        return false;
    }
}

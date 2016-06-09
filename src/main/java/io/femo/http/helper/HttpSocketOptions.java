package io.femo.http.helper;

/**
 * Created by felix on 6/4/16.
 */
public class HttpSocketOptions {

    private boolean close;
    private HandledCallback handledCallback;


    public boolean isClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public boolean hasHandledCallback() {
        return handledCallback != null;
    }

    public void callHandledCallback() {
        handledCallback.sent();
    }

    public void setHandledCallback(HandledCallback handledCallback) {
        this.handledCallback = handledCallback;
    }
}

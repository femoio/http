package io.femo.http.drivers.server;

import org.xjs.dynamic.Pluggable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Felix Resch on 29-Apr-16.
 */
public class HttpThread extends Thread implements Pluggable<HttpThread> {

    private List<Object> children;

    public HttpThread(Runnable runnable) {
        super(runnable);
    }

    @Override
    public Collection<Object> __children() {
        if(this.children == null) {
            this.children = new ArrayList<>();
        }
        return this.children;
    }

    public HttpThread(Runnable target, List<Object> children) {
        super(target);
        this.children = children;
    }
}

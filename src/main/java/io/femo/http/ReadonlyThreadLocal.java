package io.femo.http;

/**
 * Created by felix on 6/9/16.
 */
public class ReadonlyThreadLocal<T> extends ThreadLocal<T> {

    @Override
    public void set(T t) {}

    @Override
    public void remove() {}
}

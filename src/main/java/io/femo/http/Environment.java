package io.femo.http;

import io.femo.http.middleware.EnvironmentReplacerMiddleware;

import java.util.function.Supplier;

/**
 * Created by felix on 7/2/16.
 */
public interface Environment extends Driver {


    boolean has(String variable);

    EnvironmentReplacerMiddleware.HttpSupplier<String> get(String key);

    Environment set(String name, EnvironmentReplacerMiddleware.HttpSupplier<String> value);

    default Environment set(String name, String value) {
        return set(name, () -> value);
    }

    default Environment set(String name, Supplier<String> value) {
        return set(name, (req, res) -> value.get());
    }
}

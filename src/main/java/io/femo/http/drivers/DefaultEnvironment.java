package io.femo.http.drivers;

import io.femo.http.Constants;
import io.femo.http.Environment;
import io.femo.http.middleware.EnvironmentReplacerMiddleware;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by felix on 7/2/16.
 */
public class DefaultEnvironment implements Environment {

    private Map<String, EnvironmentReplacerMiddleware.HttpSupplier<String>> variables;

    public DefaultEnvironment () {
        this.variables = new HashMap<>();
        set("iso_time", () -> LocalDateTime.now().format(DateTimeFormatter.ISO_TIME));
        set("iso_date", () -> LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
        set("iso_datetime", () -> LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        set("server", "FeMo.IO HTTP Server " + Constants.VERSION);
    }

    @Override
    public boolean has(String variable) {
        return variables.containsKey(variable);
    }

    @Override
    public EnvironmentReplacerMiddleware.HttpSupplier<String> get(String key) {
        return variables.get(key);
    }

    @Override
    public Environment set(String name, EnvironmentReplacerMiddleware.HttpSupplier<String> value) {
        variables.put(name, value);
        return this;
    }
}

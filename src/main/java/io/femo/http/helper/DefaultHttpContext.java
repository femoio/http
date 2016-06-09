package io.femo.http.helper;

import io.femo.http.Base64Driver;
import io.femo.http.Driver;
import io.femo.http.HttpContext;
import io.femo.http.drivers.DefaultBase64Driver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by felix on 6/8/16.
 */
public class DefaultHttpContext implements HttpContext {

    private List<Driver> drivers;

    public DefaultHttpContext() {
        this.drivers = new ArrayList<>();
    }

    @Override
    public Base64Driver base64() {
        Optional<Base64Driver> driver = getFirstDriver(Base64Driver.class);
        if(driver.isPresent()) {
            return driver.get();
        } else {
            return new DefaultBase64Driver();
        }
    }

    private <T extends Driver> Optional<T> getFirstDriver(Class<T> type) {
        return drivers.stream().filter(d -> type.isAssignableFrom(d.getClass())).map(type::cast).findFirst();
    }

    @Override
    public void useDriver(Driver driver) {
        this.drivers.add(driver);
    }
}

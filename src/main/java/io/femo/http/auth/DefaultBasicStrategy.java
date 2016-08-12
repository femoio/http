package io.femo.http.auth;

import io.femo.http.*;
import io.femo.http.drivers.DefaultBase64Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by felix on 6/21/16.
 */
public class DefaultBasicStrategy implements Authentication {

    private static final Logger LOGGER = LoggerFactory.getLogger("HTTP");

    private Supplier<String> username;
    private Supplier<String> password;

    private String realm;
    private Pattern pattern = Pattern.compile("realm=\"(.*)\"");
    private String host;
    private String basePath;


    public DefaultBasicStrategy(Supplier<String> username, Supplier<String> password) {
        this.username = username;
        this.password = password;
    }

    public DefaultBasicStrategy(String username, String password) {
        this.username = () -> username;
        this.password = () -> password;
    }

    @Override
    public boolean isInitialized() {
        return realm != null;
    }

    @Override
    public boolean matches(HttpRequest request) {
        return host.equalsIgnoreCase(request.header("Host").value()) && request.path().startsWith(basePath);
    }

    @Override
    public boolean supportsMulti() {
        return false;
    }

    @Override
    public boolean supportsDirect() {
        return true;
    }

    @Override
    public void init(HttpResponse response) {
        if(response.hasHeader("WWW-Authenticate")) {
            Matcher matcher = pattern.matcher(response.header("WWW-Authenticate"));
            if(matcher.find()) {
                this.realm = matcher.group(1);
                this.host = response.request().header("Host").value();
                this.basePath = response.request().path();
                LOGGER.debug("Found realm {} @ {}{}", this.realm, this.host, this.basePath);
            } else {
                throw new HttpException(response.request(), "Did not receive a realm. No authentication possible!");
            }
        }
    }

    @Override
    public String strategy() {
        return "Basic";
    }

    @Override
    public void authenticate(HttpRequest request) {
        String auth = username.get() + ":" + password.get();
        List<Base64Driver> drivers = request.drivers(Base64Driver.class);
        Base64Driver driver;
        if(drivers.size() == 0) {
            driver = new DefaultBase64Driver();
        } else {
            driver = drivers.get(0);
        }
        request.header("Authorization", "Basic " + driver.encodeToString(auth.getBytes()));
    }
}

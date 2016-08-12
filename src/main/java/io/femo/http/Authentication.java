package io.femo.http;

import io.femo.http.auth.DefaultBasicStrategy;
import io.femo.http.auth.DefaultDigestStrategy;
import org.jetbrains.annotations.Contract;

import java.util.function.Supplier;

/**
 * Created by felix on 6/21/16.
 */
public interface Authentication extends Driver {

    boolean isInitialized();
    boolean matches(HttpRequest request);
    boolean supportsMulti();
    boolean supportsDirect();

    void init(HttpResponse response);

    String strategy();

    void authenticate(HttpRequest request);

    default boolean supports(HttpResponse response) {
        String authenticate = response.header("WWW-Authenticate").value();
        return authenticate.startsWith(strategy());
    }

    @Contract("_, _ -> !null")
    static Authentication basic(Supplier<String> username, Supplier<String> password) {
        return new DefaultBasicStrategy(username, password);
    }

    @Contract("_, _ -> !null")
    static Authentication basic(String username, String password) {
        return new DefaultBasicStrategy(username, password);
    }

    @Contract("_, _ -> !null")
    static Authentication digest(String username, String password) {
        return new DefaultDigestStrategy(username, password);
    }

    @Contract("_, _ -> !null")
    static Authentication digest(Supplier<String> username, Supplier<String> password) {
        return new DefaultDigestStrategy(username, password);
    }
}

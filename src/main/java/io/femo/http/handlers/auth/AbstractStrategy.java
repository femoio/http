package io.femo.http.handlers.auth;

/**
 * Created by felix on 6/13/16.
 */
public abstract class AbstractStrategy implements Strategy {

    private String realm;

    protected AbstractStrategy(String realm) {
        this.realm = realm;
    }

    @Override
    public String realm() {
        return realm;
    }

    @Override
    public String authenticateHeader() {
        return  String.format("%s realm=%s", name(), realm());
    }
}

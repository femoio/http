package io.femo.http.middleware;

import io.femo.http.*;
import io.femo.http.helper.HttpHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by felix on 6/9/16.
 */
public class EnvironmentReplacerMiddleware implements HttpMiddleware {

    private Pattern replace;
    private Map<String, HttpSupplier<String>> replacers;

    public EnvironmentReplacerMiddleware () {
        replace = Pattern.compile("\\$\\{\\{(.*)\\}\\}");
        replacers = new HashMap<>();
        replacers.put("req.host", (req, res) -> req.hasHeader("Host") ? req.header("Host").value() : "");
        replacers.put("req.path", (req, res) -> req.path());
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws HttpHandleException {
        if(response.hasHeader("Content-Type") && response.header("Content-Type").equals("text/html") ||
                response.hasHeader("X-Replace-Env") && response.header("X-Replace-Env").equals("true")) {
            String val;
            Matcher matcher = replace.matcher(val = response.responseString());
            Environment environment = HttpHelper.context().environment();
            while (matcher.find()) {
                String key = matcher.group(1);
                if(replacers.containsKey(key)) {
                    matcher.reset(val = matcher.replaceFirst(replacers.get(key).get(request, response)));
                } else if (environment.has(key)) {
                    matcher.reset(val = matcher.replaceFirst(environment.get(key).get(request, response)));
                }
            }
            response.entity(val);
        }
    }

    public interface HttpSupplier<T> {

        T get(HttpRequest req, HttpResponse res);
    }
}

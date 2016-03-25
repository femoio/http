package io.femo.http.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by felix on 2/11/16.
 */
public class HttpEventManager {

    private Map<HttpEventType, List<HttpEventHandler>> events;

    public HttpEventManager () {
        events = new HashMap<>();
    }

    public void raise(HttpEvent event) {
        if(events.containsKey(event.eventType())) {
            List<HttpEventHandler> handlers = events.get(event.eventType());
            for (HttpEventHandler handler: handlers) {
                try {
                    handler.handle(event);
                } catch (Throwable t) {
                    System.err.println("Error while handling event " + event + ".");
                }
            }
        }
    }

    public void addEventHandler(HttpEventType type, HttpEventHandler handler) {
        if(type == HttpEventType.ALL) {
            for (HttpEventType t : HttpEventType.values()) {
                if(t == HttpEventType.ALL)
                    continue;
                addEventHandler(t, handler);
            }
        } else {
            if(!events.containsKey(type)) {
                events.put(type, new ArrayList<HttpEventHandler>());
            }
            events.get(type).add(handler);
        }
    }
}

package hyperbench.request;

import hyperbench.stats.RequestTracker;
import hyperbench.input.HttpRequestPrototype;
import org.jboss.netty.handler.codec.http.HttpRequest;

class HttpRequestContext {
    private final HttpRequestPrototype hrp;
    private final RequestTracker tracker;

    public HttpRequestContext(HttpRequestPrototype hrp) {
        this.hrp = hrp;
        this.tracker = hrp.newTrackerInstance();
    }

    public HttpRequest getHttpRequest() {
        return hrp.getHttpRequest();
    }

    public RequestTracker getTracker() {
        return tracker;
    }
}
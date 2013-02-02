package hyperbench.request;

import hyperbench.input.HttpRequestPrototype;
import hyperbench.stats.RequestTracker;
import lombok.Getter;

class HttpRequestContext {
    @Getter private final HttpRequestPrototype httpRequest;
    @Getter private final RequestTracker tracker;

    public HttpRequestContext(HttpRequestPrototype hrp) {
        this.httpRequest = hrp;
        this.tracker = hrp.newTrackerInstance();
    }
}
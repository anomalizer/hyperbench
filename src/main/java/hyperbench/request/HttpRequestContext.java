package hyperbench.request;

import hyperbench.stats.RequestTracker;
import hyperbench.stats.YammerTrackFactory;
import io.netty.handler.codec.http.HttpRequest;
import lombok.Getter;

import javax.inject.Provider;

class HttpRequestContext {
    @Getter private final HttpRequest httpRequest;
    @Getter private final RequestTracker tracker;

    private static final Provider<RequestTracker> rgt = new YammerTrackFactory();

    public HttpRequestContext(HttpRequest hrp) {
        this.httpRequest = hrp;
        this.tracker = rgt.get();
    }
}
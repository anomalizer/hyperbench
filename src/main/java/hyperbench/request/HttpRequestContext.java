package hyperbench.request;

import hyperbench.stats.RequestTracker;
import io.netty.handler.codec.http.HttpRequest;
import lombok.Getter;

import javax.inject.Provider;

final class HttpRequestContext {
    @Getter private final HttpRequest httpRequest;
    @Getter private final RequestTracker tracker;

    public HttpRequestContext(HttpRequest hrp, Provider<RequestTracker> rt) {
        this.httpRequest = hrp;
        this.tracker = rt.get();
    }
}
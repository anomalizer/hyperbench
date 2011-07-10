package hyperbench.request;

import com.inmobi.instrumentation.TimingAccumulator;
import hyperbench.input.HttpRequestPrototype;
import org.jboss.netty.handler.codec.http.HttpRequest;

class HttpRequestContext {
    private final HttpRequestPrototype hrp;
    private final TimingAccumulator stats;
    private long startTime;

    public HttpRequestContext(HttpRequestPrototype hrp) {
        this.hrp = hrp;
        stats = hrp.getStatsCounter();
    }

    public void start() {
        startTime = stats.accumulateInvocationStartTimer();
    }

    public void connectFail() {
        stats.accumulateOutcome(TimingAccumulator.Outcome.UNHANDLED_FAILURE, startTime);
    }

    public void gotResponse(int statusCode, long endTime) {
        stats.accumulateOutcomeWithDelta(TimingAccumulator.Outcome.SUCCESS, endTime - startTime);
    }

    public HttpRequest getHttpRequest() {
        return hrp.getHttpRequest();
    }
}

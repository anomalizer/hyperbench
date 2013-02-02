package hyperbench.stats;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Timer;
import com.yammer.metrics.core.TimerContext;
import hyperbench.request.Harness;

import javax.inject.Provider;
import java.util.concurrent.TimeUnit;

public class YammerTrackFactory implements Provider<RequestTracker> {
    private final Timer timer = Metrics.newTimer(
            Harness.class, "http-requests", TimeUnit.MILLISECONDS, TimeUnit.SECONDS);

    private final class YammerTracker implements RequestTracker {
        private TimerContext context;

        @Override
        public void start() {
            context = timer.time();
        }

        @Override
        public void connectFail() {
            context.stop();
        }

        @Override
        public void recordResponse(int statusCode) {
            context.stop();
        }
    }

    @Override
    public RequestTracker get() {
        return new YammerTracker();
    }

    @Override
    public String toString() {
        return "average time was " + timer.mean() + " over "
                + timer.count() + " requests";
    }
}

package hyperbench.stats;

import javax.inject.Named;
import javax.inject.Provider;

@Named("metric-nop")
public class NopRequestTrackerFactory implements Provider<RequestTracker> {
    private static class NopTracker implements RequestTracker {

        @Override
        public void start() {}

        @Override
        public void connectFail() {}

        @Override
        public void recordResponse(int statusCode) {}
    }

    private static final RequestTracker r = new NopTracker();

    @Override
    public RequestTracker get() {
        return r;
    }
}

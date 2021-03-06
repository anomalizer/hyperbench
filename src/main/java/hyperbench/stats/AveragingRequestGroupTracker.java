package hyperbench.stats;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AveragingRequestGroupTracker implements RequestGroupTracker{
    private final AtomicInteger starts = new AtomicInteger(0);
    private final AtomicInteger connectFails = new AtomicInteger(0);
    private final AtomicInteger responses = new AtomicInteger(0);

    private final AtomicLong cumulativeSuccessDuration = new AtomicLong(0);

    private class ARequestTracker implements RequestTracker {
        private long startTime;

        @Override
        public void start() {
            startTime = System.nanoTime();
            starts.incrementAndGet();
        }

        @Override
        public void connectFail() {
            long duration = getDuration();
            connectFails.incrementAndGet();
        }

        @Override
        public void recordResponse(int statusCode) {
            long duration = getDuration();
            responses.incrementAndGet();
            cumulativeSuccessDuration.addAndGet(duration);
        }

        public long getDuration() {
            return System.nanoTime() - startTime;
        }
    }

    @Override
    public RequestTracker newTrackerInstance() {
        return new ARequestTracker();
    }

    @Override
    public String toString() {
        Object args[] = new Object[] {
                starts.get(),
                connectFails.get(),
                responses.get(),
                responses.get()>0? cumulativeSuccessDuration.get()/1e9/responses.get(): 0
        };
        return String.format("starts: %d, connect-fails: %d, success: %s, avg-success-time: %f", args);
    }
}
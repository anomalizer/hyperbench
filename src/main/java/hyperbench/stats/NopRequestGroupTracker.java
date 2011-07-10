package hyperbench.stats;

public class NopRequestGroupTracker implements RequestGroupTracker {
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
    public RequestTracker newTrackerInstance() {
        return r;
    }
}

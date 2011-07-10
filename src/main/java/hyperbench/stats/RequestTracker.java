package hyperbench.stats;

public interface RequestTracker {
    void start();
    void connectFail();
    void recordResponse(int statusCode);
}
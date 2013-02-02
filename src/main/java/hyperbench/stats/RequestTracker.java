package hyperbench.stats;

/**
 * An interface to measure the response time of a single
 * HTTP request
 */
public interface RequestTracker {

    /**
     * Clients should invoke this as soon as the request is issued
     */
    void start();

    /**
     * Call this method if there is any sort of a connection error
     */
    void connectFail();

    /**
     * Call this method when we get back some response
     * @param statusCode the HTTP status code of the response
     */
    void recordResponse(int statusCode);
}
package hyperbench.input;

import hyperbench.request.CappedIterator;
import hyperbench.stats.RequestTracker;
import hyperbench.stats.YammerTrackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 */
public class SimpleGet implements LoadSet {
    private static final Logger logger = LoggerFactory.getLogger(SimpleGet.class);

    private final HttpRequestBuilder.Request req;
    private final int count;
    private final Provider<RequestTracker> tracker;

    public SimpleGet(String url, int count, Provider<RequestTracker> tracker) {
        this.tracker = tracker;

        if(count < 1) {
            throw new RuntimeException("count must be greater than 1");
        }

        HttpRequestBuilder tmpreq = new HttpRequestBuilder(new YammerTrackFactory());
        try {
            tmpreq.setUrl(url);
            tmpreq.build();
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
            tmpreq = null;
        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
            tmpreq = null;
        } finally {
            req = tmpreq.build();
        }

        this.count = count;
    }

    @Override
    public Iterator<HttpRequestBuilder.Request> workloadGenerator() {
        return new SimpleGetIterator(count);
    }

    @Override
    public List<HttpRequestBuilder.Request> contents() {
        return Collections.singletonList(req);
    }

    private class SimpleGetIterator extends CappedIterator<HttpRequestBuilder.Request> {
        public SimpleGetIterator(int maxFetches) {
            super(maxFetches);
        }

        @Override
        public HttpRequestBuilder.Request doNext(int curr, int max) {
            return req;
        }
    }
}
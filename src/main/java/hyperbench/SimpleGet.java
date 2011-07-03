package hyperbench;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 */
public class SimpleGet implements LoadSet {
    private static final Logger logger = LoggerFactory.getLogger(SimpleGet.class);

    private final HttpRequestPrototype req;
    private final int count;

    public SimpleGet(String url, int count) {
        if(count < 1) {
            throw new RuntimeException("count must be greater than 1");
        }

        HttpRequestPrototype tmpreq = new HttpRequestPrototype();
        try {
            tmpreq.setUrl(url);
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
            tmpreq = null;
        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
            tmpreq = null;
        } finally {
            req = tmpreq;
        }

        this.count = count;
    }

    @Override
    public Iterator<HttpRequestPrototype> workloadGenerator() {
        return new SimpleGetIterator(count);
    }

    @Override
    public List<HttpRequestPrototype> contents() {
        return Collections.singletonList(req);
    }

    private class SimpleGetIterator extends CappedIterator<HttpRequestPrototype> {
        public SimpleGetIterator(int maxFetches) {
            super(maxFetches);
        }

        @Override
        public HttpRequestPrototype doNext(int curr, int max) {
            return req;
        }
    }
}
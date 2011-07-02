package hyperbench;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */
public class SimpleGet implements LoadSet {

    private final HttpRequestPrototype req;
    private final int count;

    public SimpleGet(String url, int count) {
        if(count < 1) {
            throw new RuntimeException("count must be greater than 1");
        }
        req = new HttpRequestPrototype();
        req.setUrl(url);

        this.count = count;
    }

    @Override
    public Iterator<HttpRequestPrototype> iterator() {
        return new SimpleGetIterator(count);
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
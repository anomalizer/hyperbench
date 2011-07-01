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
        return new SimpleGetIterator();
    }

    private class SimpleGetIterator implements Iterator<HttpRequestPrototype> {

        private final AtomicInteger i = new AtomicInteger(0);

        @Override
        public boolean hasNext() {
            return (i.get() < count);
        }

        @Override
        public HttpRequestPrototype next() {
            if(i.getAndIncrement() < count) {
                return req;
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("immutable list");
        }
    }
}
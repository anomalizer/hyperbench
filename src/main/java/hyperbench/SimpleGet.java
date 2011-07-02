package hyperbench;

import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Iterator;

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
        try {
            req.setUrl(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

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
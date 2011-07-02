package hyperbench;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An abstract read-only iterator that will ensure at most 'n' elements are retrieved.
 *
 * Implementations will have to help out with providing the actual elements
 */
public abstract class CappedIterator<T>  implements Iterator<T> {
    private final int max;
    private final AtomicInteger counter = new AtomicInteger(0);

    public CappedIterator(int maxFetches) {
        max = maxFetches;
    }

    @Override
    public boolean hasNext() {
        return (counter.get() < max);
    }

    @Override
    public T next() {
        int curr = counter.getAndIncrement();
        if(curr < max) {
            return doNext(curr, max);
        }
        throw new NoSuchElementException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("read-only iterator");
    }

    /**
     * This method will be invoked every time a legitimate call of next()
     * is invoked. "Legitimate" here means that the fetch is lies within
     * [0, maxFetchs). The current "progress" is available via the two
     * arguments
     *
     * @param curr The current "fetch" count
     * @param max The maximum number of elements that this iterator can return
     */
    public abstract T doNext(int curr, int max);
}
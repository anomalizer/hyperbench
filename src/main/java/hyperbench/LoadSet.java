package hyperbench;

import java.util.Iterator;

/**
 */
public interface LoadSet {
    public Iterator<HttpRequestPrototype> workloadIterator();
}

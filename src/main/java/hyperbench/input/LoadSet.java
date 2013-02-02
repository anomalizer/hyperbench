package hyperbench.input;

import java.util.Iterator;
import java.util.List;

/**
 */
public interface LoadSet {
    public Iterator<HttpRequestBuilder.Request> workloadGenerator();

    public List<HttpRequestBuilder.Request> contents();
}

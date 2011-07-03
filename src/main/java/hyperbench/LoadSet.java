package hyperbench;

import java.util.Iterator;
import java.util.List;

/**
 */
public interface LoadSet {
    public Iterator<HttpRequestPrototype> workloadGenerator();

    public List<HttpRequestPrototype> contents();
}

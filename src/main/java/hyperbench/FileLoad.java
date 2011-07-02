package hyperbench;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.yaml.snakeyaml.Yaml;

import javax.print.attribute.Size2DSyntax;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class FileLoad implements LoadSet{
    private final List<HttpRequestPrototype> testRequests = new ArrayList<HttpRequestPrototype>();
    private int size;
    private int count;

    public FileLoad(String filename, int count) throws FileNotFoundException {
        doYaml(new FileInputStream(filename));
        this.count = count;
    }

    private void doYaml(InputStream is) {
        Yaml yaml = new Yaml();
        List<Map> requests = (List<Map>) yaml.load(is);
        for( Map m : requests) {

            String methodName = (String) m.get("method");
            String url = (String) m.get("url");
            if(methodName == null || url == null) {
                //WTF
                continue;
            }

            if(methodName.equals("GET")) {
                HttpRequestPrototype oneRequest = new HttpRequestPrototype();
                oneRequest.setUrl(url);
                testRequests.add(oneRequest);
            }
        }
        size = testRequests.size();
    }

    @Override
    public Iterator<HttpRequestPrototype> iterator() {
        return new FileLoadIterator();
    }

    private class FileLoadIterator implements Iterator<HttpRequestPrototype> {
        private final AtomicInteger i = new AtomicInteger(0);

        @Override
        public boolean hasNext() {
            return (i.get() < count);
        }

        @Override
        public HttpRequestPrototype next() {
            int curr =i.getAndIncrement();
            if(curr < count) {
                if(size > 1)
                    return testRequests.get(curr % count);
                else
                    return  testRequests.get(0);
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("immutable list");
        }
    }
}

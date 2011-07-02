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
        return new FileLoadIterator(count);
    }

    private class FileLoadIterator extends CappedIterator<HttpRequestPrototype> {

        public FileLoadIterator(int maxFetches) {
            super(maxFetches);
        }

        @Override
        public HttpRequestPrototype doNext(int curr, int max) {
            if(size > 1)
                return testRequests.get(curr % max);
            else
                return  testRequests.get(0);
        }
    }
}
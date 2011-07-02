package hyperbench;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FileLoad implements LoadSet{
    private static final Logger logger = LoggerFactory.getLogger(FileLoad.class);

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
                try {
                    oneRequest.setUrl(url);
                    testRequests.add(oneRequest);
                } catch (URISyntaxException e) {
                    logger.error(e.getMessage());
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage());
                }
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
            return testRequests.get(curr % size);
        }
    }
}
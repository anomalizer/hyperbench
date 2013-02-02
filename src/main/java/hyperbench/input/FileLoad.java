package hyperbench.input;

import hyperbench.request.CappedIterator;
import hyperbench.stats.RequestTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Provider;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FileLoad implements LoadSet {
    private static final Logger logger = LoggerFactory.getLogger(FileLoad.class);

    private List<HttpRequestBuilder.Request> testRequests;
    private int size;
    private int count;
    private Provider<RequestTracker> tracker;

    public FileLoad(String filename, int count, Provider<RequestTracker> tracker) throws FileNotFoundException {
        doYaml(new FileInputStream(filename));
        this.count = count;
        this.tracker = tracker;
    }

    private void doYaml(InputStream is) {
        Yaml yaml = new Yaml();
        List<Map> requests = (List<Map>) yaml.load(is);
        testRequests = new ArrayList<HttpRequestBuilder.Request>(requests.size());

        for( Map m : requests) {

            String methodName = (String) m.get("method");
            String url = (String) m.get("url");
            if(methodName == null || url == null) {
                //WTF
                continue;
            }

            if("GET".equals(methodName) || "POST".equals(methodName)) {
                HttpRequestBuilder oneRequest = new HttpRequestBuilder(tracker);

                try {
                    oneRequest.setUrl(url, methodName);

                    String body = (String) m.get("body");
                    if(body != null) {
                        oneRequest.setBody(body);
                    }

                    Map<String, String> headers = (Map) m.get("headers");
                    if(headers != null) {
                        for(Map.Entry<String, String> e: headers.entrySet()) {
                            oneRequest.addHeader(e.getKey(), e.getValue());
                        }
                    }
                    oneRequest.build();
                    testRequests.add(oneRequest.build());
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
    public Iterator<HttpRequestBuilder.Request> workloadGenerator() {
        return new FileLoadIterator(count);
    }

    @Override
    public List<HttpRequestBuilder.Request> contents() {
        return testRequests;
    }

    private class FileLoadIterator extends CappedIterator<HttpRequestBuilder.Request> {

        public FileLoadIterator(int maxFetches) {
            super(maxFetches);
        }

        @Override
        public HttpRequestBuilder.Request doNext(int curr, int max) {
            return testRequests.get(curr % size);
        }
    }
}
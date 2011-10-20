package hyperbench.input;

import hyperbench.stats.AveragingRequestGroupTracker;
import hyperbench.stats.RequestGroupTracker;
import hyperbench.stats.RequestTracker;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

//TODO: really horrible API design needs cleanup
public class HttpRequestPrototype {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestPrototype.class);

    private InetAddress addr = null;
    private String host = null;
    private HttpRequest request;
    private int port;
    private String uriString;

    private final RequestGroupTracker stats = new AveragingRequestGroupTracker();

    public void setUrl(String urlString) throws URISyntaxException, UnknownHostException {
        setUrl(urlString, HttpMethod.GET);
    }

    public void setUrl(String urlString, String method) throws URISyntaxException, UnknownHostException {
        HttpMethod m;
        if("GET".equals(method)) {
            m = HttpMethod.GET;
        } else if("POST".equals(method)) {
            m = HttpMethod.POST;
        } else {
            throw new RuntimeException("Whoa");
        }
        setUrl(urlString, m);
    }

    public void setUrl(String urlString, HttpMethod method) throws URISyntaxException, UnknownHostException {
        URI url = new URI(urlString);
        host = url.getHost();
        addr = InetAddress.getByName(host);
        uriString = urlString;
        port = url.getPort();
        if(port == -1)
            port = 80;

        request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, method, getUriString());
        request.setHeader(HttpHeaders.Names.HOST, getHost());
        request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);

        String protocol = url.getScheme();
        if( !("http".equals(protocol)) ) {
            throw new UnsupportedOperationException("Protocol " + protocol + "is not supported");
        }
    }

    public InetAddress getHostAddress() {
        return addr;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUriString() {
        return uriString;
    }

    public HttpRequest getHttpRequest() {
        return request;
    }

    public void addHeader(String name, String value) {
        if(request != null) {
            request.addHeader(name, value);
        }
    }

    public RequestTracker newTrackerInstance() {
        return stats.newTrackerInstance();
    }

    public String stats() {
        return stats.toString();
    }


    public void setBody(Object body) {
        if(request != null) {
            ChannelBuffer cb = null;
            if(body instanceof String) {
                cb = ChannelBuffers.wrappedBuffer(((String) body).getBytes());
            } else {
                cb = ChannelBuffers.wrappedBuffer((byte[]) body);
            }
            request.setHeader("Content-Length", cb.array().length);
            request.setContent(cb);
        }
    }
}
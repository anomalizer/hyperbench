package hyperbench.input;

import hyperbench.stats.AveragingRequestGroupTracker;
import hyperbench.stats.RequestGroupTracker;
import hyperbench.stats.RequestTracker;
import lombok.Getter;
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

    @Getter private InetAddress hostAddress = null;
    @Getter private String host = null;
    @Getter private HttpRequest httpRequest;
    @Getter private int port;
    @Getter private String uriString;

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
        hostAddress = InetAddress.getByName(host);
        uriString = urlString;
        port = url.getPort();
        if(port == -1)
            port = 80;

        httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, method, getUriString());
        httpRequest.setHeader(HttpHeaders.Names.HOST, getHost());
        httpRequest.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);

        String protocol = url.getScheme();
        if( !("http".equals(protocol)) ) {
            throw new UnsupportedOperationException("Protocol " + protocol + "is not supported");
        }
    }

    public void addHeader(String name, String value) {
        if(httpRequest != null) {
            httpRequest.addHeader(name, value);
        }
    }

    public RequestTracker newTrackerInstance() {
        return stats.newTrackerInstance();
    }

    public String stats() {
        return stats.toString();
    }


    public void setBody(Object body) {
        if(httpRequest != null) {
            ChannelBuffer cb = null;
            if(body instanceof String) {
                cb = ChannelBuffers.wrappedBuffer(((String) body).getBytes());
            } else {
                cb = ChannelBuffers.wrappedBuffer((byte[]) body);
            }
            httpRequest.setHeader("Content-Length", cb.array().length);
            httpRequest.setContent(cb);
        }
    }
}
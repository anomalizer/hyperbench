package hyperbench.input;

import hyperbench.stats.RequestTracker;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Provider;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpRequestBuilder {
    @AllArgsConstructor
    @Getter
    public static final class Request {
        private HttpRequest request;
        private InetAddress address;
        private int port;
    }

    @Getter private InetAddress hostAddress = null;
    @Getter private String host = null;
    @Getter private int port;
    @Getter private String uriString;
    private ByteBuf body;

    private final Provider<RequestTracker> stats;
    private HttpMethod method;
    private Map<String, String> headers = new HashMap<String, String>();

    @Inject
    public HttpRequestBuilder(Provider<RequestTracker> stats) {
        this.stats = stats;
    }

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

        String protocol = url.getScheme();
        if( !("http".equals(protocol)) ) {
            throw new UnsupportedOperationException("Protocol " + protocol + "is not supported");
        }

        this.method = method;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public RequestTracker newTrackerInstance() {
        return stats.get();
    }

    public String stats() {
        return stats.toString();
    }


    public void setBody(String body) {
        setBody(body.getBytes());
    }

    public void setBody(byte[] body) {
        setBody(body, false);
    }

    public void setBody(byte[] body, boolean makeCopy) {
        this.body = makeCopy ? Unpooled.copiedBuffer(body) : Unpooled.wrappedBuffer(body);
    }

    public Request build() {
        HttpRequest httpRequest;
        if(null == body) {
            httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, getUriString());
        } else {
            httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, method, getUriString(), body);
            httpRequest.headers().set("Content-Length", body.array().length);
        }
        httpRequest.headers().set(HttpHeaders.Names.HOST, getHost());
        httpRequest.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);

        for(Map.Entry<String, String> h: headers.entrySet()) {
            httpRequest.headers().add(h.getKey(), h.getValue());
        }

        return new Request(httpRequest, hostAddress, port);
    }
}
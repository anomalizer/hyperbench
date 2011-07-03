package hyperbench;

import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

/**
 */
public class HttpRequestPrototype {
    private URI url = null;
    private InetAddress addr = null;
    private String host = null;
    private HttpRequest request;

    public void setUrl(String urlString) throws URISyntaxException, UnknownHostException {
        this.url = new URI(urlString);
        host = url.getHost();
        addr = InetAddress.getByName(host);

        request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, getUriString());
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
        int retval = url.getPort();
        return retval == -1 ? 80 : retval;
    }

    public String getUriString() {
        return url.toASCIIString();
    }

    public HttpRequest getHttpRequest() {
        return request;
    }

    public void addHeader(String name, String value) {
        throw new UnsupportedOperationException("unimplemented");
    }
}
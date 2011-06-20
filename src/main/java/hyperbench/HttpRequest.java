package hyperbench;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

/**
 */
public class HttpRequest {
    private URI url;
    private InetAddress addr;
    private String host;

    public void setUrl(String urlString){
        try {
            this.url = new URI(urlString);
            host = url.getHost();
            addr = InetAddress.getByName(host);
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
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
        return url.getPort();
    }

    public String getUriString() {
        return url.toASCIIString();
    }

    public void addHeader(String name, String value) {
        throw new UnsupportedOperationException("unimplemented");
    }
}
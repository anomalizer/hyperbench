package hyperbench;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 */
public class HttpRequest {
    private URI url;

    public void setUrl(String urlString){
        try {
            this.url = new URI(urlString);
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        String protocol = url.getScheme();
        if( !("http".equals(protocol)) ) {
            throw new UnsupportedOperationException("Protocol " + protocol + "is not supported");
        }
    }

    public String getHost() {
        return url.getHost();
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
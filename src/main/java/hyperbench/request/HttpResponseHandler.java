package hyperbench.request;

import hyperbench.stats.RequestTracker;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;

import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class HttpResponseHandler extends SimpleChannelUpstreamHandler {

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        HttpResponse response = (HttpResponse) e.getMessage();
        long time = System.nanoTime();
        HttpRequestContext rc = (HttpRequestContext) ctx.getAttachment();
        if(rc != null) {
            rc.getTracker().recordResponse(response.getStatus().getCode());
        }
    }
}
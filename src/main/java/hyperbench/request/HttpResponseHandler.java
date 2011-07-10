package hyperbench.request;

import hyperbench.request.HttpRequestContext;
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
        ConcurrentHashMap<Channel, HttpRequestContext> chm = (ConcurrentHashMap<Channel, HttpRequestContext>) ctx.getAttachment();
        HttpRequestContext rc = chm.get(e.getChannel());
        if(rc != null) {
            rc.gotResponse(response.getStatus().getCode(), time);
        }
    }
}
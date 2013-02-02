package hyperbench.request;

import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 */
@Slf4j
public class HttpResponseHandler extends SimpleChannelUpstreamHandler {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        log.warn("Aiee, got an exception", e.getCause());
    }

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
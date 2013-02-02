package hyperbench.request;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

/**
 */
@Slf4j
public class HttpResponseHandler extends ChannelInboundMessageHandlerAdapter<HttpResponse> {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Aiee, got an exception", cause);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, HttpResponse msg) throws Exception {
        long time = System.nanoTime();
        HttpRequestContext rc = ctx.attr(Harness.STATE).get();
        if(rc != null) {
            rc.getTracker().recordResponse(msg.getStatus().getCode());
        }
    }
}
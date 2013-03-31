package hyperbench.request;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 */
@Slf4j
public class HttpResponseHandler extends ChannelInboundMessageHandlerAdapter<FullHttpResponse> {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Aiee, got an exception", cause);
        final Channel channel = ctx.channel();
        if( (null != channel) && channel.isOpen()) {
            channel.close();
        }
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        if(log.isTraceEnabled()) {
            final List<String> encodingHeader = msg.headers().getAll("Content-Encoding");
            final String encoding = (encodingHeader.size() == 1) ? encodingHeader.get(0): "ISO-8859-1";
            log.trace("Received response {}", new String(msg.data().array(), encoding));
        }
        HttpRequestContext rc = ctx.attr(Harness.STATE).get();
        if(rc != null) {
            rc.getTracker().recordResponse(msg.getStatus().code());
        } else {
            log.warn("Code should never reach here");
        }
        final Channel channel = ctx.channel();
        channel.close();
    }
}
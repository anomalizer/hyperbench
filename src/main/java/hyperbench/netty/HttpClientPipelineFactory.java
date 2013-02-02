package hyperbench.netty;

import hyperbench.request.HttpResponseHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpChunkAggregator;
import io.netty.handler.codec.http.HttpClientCodec;

public class HttpClientPipelineFactory extends ChannelInitializer<SocketChannel> {

    public static final int MAX_CONTENT_LENGTH = 1024 * 1024;

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("http", new HttpClientCodec());
        pipeline.addLast("aggregator", new HttpChunkAggregator(MAX_CONTENT_LENGTH));
        pipeline.addLast("handler", new HttpResponseHandler());
    }
}
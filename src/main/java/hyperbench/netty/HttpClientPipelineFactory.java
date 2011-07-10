package hyperbench.netty;

import hyperbench.request.HttpResponseHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpClientCodec;

import static org.jboss.netty.channel.Channels.pipeline;

public class HttpClientPipelineFactory implements ChannelPipelineFactory {

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = pipeline();
        pipeline.addLast("codec", new HttpClientCodec());
        pipeline.addLast("handler", new HttpResponseHandler());
        return pipeline;
    }
}
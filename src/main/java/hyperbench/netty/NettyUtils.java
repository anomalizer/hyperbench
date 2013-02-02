package hyperbench.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 */
public class NettyUtils {
    private static Bootstrap bootstrap = null;

    public synchronized static Bootstrap getClientBootstrap() {
        if(bootstrap == null) {
            bootstrap = new Bootstrap();
            bootstrap.group(new NioEventLoopGroup())
                    .channel(NioSocketChannel.class)
                    .handler(new HttpClientPipelineFactory());
        }
        return bootstrap;
    }

    public synchronized static void shutdown() {
        if(bootstrap != null) {
            bootstrap.shutdown();
            bootstrap = null;
        }
    }
}
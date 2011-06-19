package hyperbench;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.util.concurrent.Executors;

/**
 */
public class NettyUtils {
    private static ClientBootstrap bootstrap = null;

    public synchronized static ClientBootstrap getClientBootstrap() {
        if(bootstrap == null) {
            bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool()));

            bootstrap.setPipelineFactory(new HttpClientPipelineFactory());
        }
        return bootstrap;
    }

    public synchronized static void shutdown() {
        if(bootstrap != null) {
            bootstrap.releaseExternalResources();
            bootstrap = null;
        }
    }
}
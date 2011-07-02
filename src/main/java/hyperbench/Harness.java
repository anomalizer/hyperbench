package hyperbench;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 */
public class Harness implements Runnable {

    final static Logger logger = LoggerFactory.getLogger(Harness.class);

    private final Iterator<HttpRequestPrototype> iter;
    private final Semaphore concurrencyLimiter;
    private final ClientBootstrap bootstrap;

    private final AtomicInteger requests = new AtomicInteger();
    private final AtomicInteger responses = new AtomicInteger();

    public Harness(Iterator<HttpRequestPrototype> requests, int maxConcurrency) {
        iter = requests;
        concurrencyLimiter = new Semaphore(maxConcurrency);
        bootstrap = NettyUtils.getClientBootstrap();
    }

    @Override
    public void run() {
        logger.info("Starting the test");
        while(true) {
            try {
                HttpRequestPrototype r = iter.next();
                fire(r);
            } catch(NoSuchElementException e){
                logger.info("All requests issued");
                break;
            }
        }

        // No more requests to fire, but some responses are pending
        logger.debug("Waiting for pending requests to finish");
        while(requests.get() != responses.get()) {
            //wait for things to finish, this is a semi-busy loop
            logger.info("{} requests, {} done", requests.get(), responses.get());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.warn("Aborted");
                e.printStackTrace();
                break;
            }
        }
        NettyUtils.shutdown();
        logger.info("end of test...............................................................");
    }

    private void fire(HttpRequestPrototype r) {
        logger.debug("Trying to get a ticket");
        try {
            concurrencyLimiter.acquire();
            requests.incrementAndGet();

            logger.info("connecting");
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(r.getHostAddress(), r.getPort()));
            future.addListener(new ConnectHandler(r));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void requestCleanup() {
        concurrencyLimiter.release();
        responses.incrementAndGet();
    }

    private class ConnectHandler implements  ChannelFutureListener {
        private final HttpRequestPrototype r;
        private final RequestCleanup rc = new RequestCleanup();

        public ConnectHandler(HttpRequestPrototype r) {
            this.r = r;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if(future.isSuccess()) {
                // Go do actual request
                logger.info("connected successfully");

                Channel ch = future.getChannel();
                ch.getCloseFuture().addListener(rc);

                ch.write(r.getHttpRequest());
            } else {
                requestCleanup();
                logger.error("connection failed {}", future.getCause().getMessage());
            }
        }
    }

    private class RequestCleanup implements ChannelFutureListener {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            requestCleanup();
            logger.info("all done");
        }
    }
}
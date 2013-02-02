package hyperbench.request;

import hyperbench.input.HttpRequestPrototype;
import hyperbench.netty.NettyUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
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
    static final AttributeKey<HttpRequestContext> STATE =
            new AttributeKey<HttpRequestContext>("MyHandler.httpRequest");

    final static Logger logger = LoggerFactory.getLogger(Harness.class);

    private final Iterator<HttpRequestPrototype> iter;
    private final Semaphore concurrencyLimiter;
    private final Bootstrap bootstrap;

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
            logger.debug("{} requests, {} done", requests.get(), responses.get());
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
            HttpRequestContext context = new HttpRequestContext(r);

            logger.debug("Got a ticket, now trying to connect");
            context.getTracker().start();
            bootstrap.remoteAddress(new InetSocketAddress(r.getHostAddress(), r.getPort()));
            ChannelFuture future = bootstrap.connect();
            future.addListener(new ConnectHandler(context));
            logger.debug("async connect issued");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void requestCleanup() {
        concurrencyLimiter.release();
        responses.incrementAndGet();
    }

    private class ConnectHandler implements ChannelFutureListener {
        private final HttpRequestContext r;
        private final RequestCleanup rc = new RequestCleanup();

        public ConnectHandler(HttpRequestContext r) {
            this.r = r;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if(future.isSuccess()) {
                // Go do actual request
                logger.info("connected successfully");

                Channel ch = future.channel();
                ch.closeFuture().addListener(rc);
                ch.pipeline().context(HttpResponseHandler.class).attr(STATE).set(r);

                logger.debug("About to issue request");
                ch.write(r.getHttpRequest());
                logger.info("issued request");
            } else {
                r.getTracker().connectFail();
                requestCleanup();
                logger.error("connection failed", future.cause());
            }
        }
    }

    private class RequestCleanup implements ChannelFutureListener {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            requestCleanup();
            logger.debug("finished one request");
        }
    }
}
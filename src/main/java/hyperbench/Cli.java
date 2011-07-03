package hyperbench;

import com.beust.jcommander.JCommander;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

public class Cli {
    public static void main(String args[]) throws InterruptedException, FileNotFoundException {
        final Logger logger = LoggerFactory.getLogger(Cli.class);

        CliArgs opts = new CliArgs();
        JCommander jc = new JCommander(opts, args);
        LoadSet l;

        if (args.length == 0) {
            jc.usage();
            return;
        }

        logger.info("Preparing requests");
        if(opts.filename == null) {
            l = new SimpleGet(opts.url, opts.requests);
        } else {
            l = new FileLoad(opts.filename, opts.requests);
        }

        Thread t = new Thread(new Harness(l.workloadGenerator(), opts.concurrency), "load generator");
        t.start();
        t.join();

        for(HttpRequestPrototype tmp : l.contents() ) {
            logger.info("url: {} invoke: {} avg-time {}",  new Object[] {tmp.getUriString(), tmp.getStatsCounter().getInvocationCount(), tmp.getStatsCounter().getCumulativeNanoseconds()/tmp.getStatsCounter().getInvocationCount()/1e9});
        }
    }
}
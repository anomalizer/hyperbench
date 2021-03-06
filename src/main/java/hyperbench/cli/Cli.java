package hyperbench.cli;

import com.beust.jcommander.JCommander;
import hyperbench.input.FileLoad;
import hyperbench.input.HttpRequestPrototype;
import hyperbench.input.LoadSet;
import hyperbench.input.SimpleGet;
import hyperbench.request.Harness;
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
            logger.info("url: {} stats {}",  tmp.getUriString(), tmp.stats() );
        }
    }
}
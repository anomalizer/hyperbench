package hyperbench;

import com.beust.jcommander.JCommander;

public class Cli {
    public static void main(String args[]) throws InterruptedException {

        CliArgs opts = new CliArgs();
        JCommander jc = new JCommander(opts, args);

        if (args.length == 0) {
            jc.usage();
            return;
        }

        LoadSet l = new SimpleGet(opts.url, opts.requests);

        Thread t = new Thread(new Harness(l.iterator(), opts.concurrency), "load generator");
        t.start();
        t.join();
    }
}
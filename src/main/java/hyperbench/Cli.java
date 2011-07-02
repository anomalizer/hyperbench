package hyperbench;

import com.beust.jcommander.JCommander;

import java.io.FileNotFoundException;

public class Cli {
    public static void main(String args[]) throws InterruptedException, FileNotFoundException {

        CliArgs opts = new CliArgs();
        JCommander jc = new JCommander(opts, args);
        LoadSet l;

        if (args.length == 0) {
            jc.usage();
            return;
        }

        if(opts.filename == null) {
            l = new SimpleGet(opts.url, opts.requests);
        } else {
            l = new FileLoad(opts.filename, opts.requests);
        }

        Thread t = new Thread(new Harness(l.iterator(), opts.concurrency), "load generator");
        t.start();
        t.join();
    }
}
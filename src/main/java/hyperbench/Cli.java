package hyperbench;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class Cli {
    public static void main(String args[]) throws InterruptedException {
        LoadSet l = new SimpleGet("http://localhost:81/STL_doc/", 30*1000);

        Thread t = new Thread(new Harness(l.iterator(), 100), "load generator");
        t.start();
        t.join();
    }
}
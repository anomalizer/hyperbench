package hyperbench;

import com.beust.jcommander.Parameter;

/**
 */
public class CliArgs {

    @Parameter(names = "-u", description = "The url to be invoked")
    public String url;

    @Parameter(names = "-n", description = "Total number of requests to be issued")
    public int requests = 1;

    @Parameter(names = "-c", description = "Max concurrency")
    public int concurrency = 1;
}

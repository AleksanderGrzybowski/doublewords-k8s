package pl.kelog;

import pl.kelog.collector.CollectorServer;
import pl.kelog.publisher.ConsoleResultsPublisher;
import pl.kelog.publisher.HttpResultsPublisher;
import pl.kelog.publisher.ResultsPublisher;
import pl.kelog.worker.Worker;

import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;
import static java.text.MessageFormat.format;
import static java.util.Optional.ofNullable;

public class Main {
    
    private static final String ENV_MODE = "MODE";
    private static final String ENV_MODE_WORKER = "WORKER";
    private static final String ENV_MODE_SERVER = "SERVER";
    
    private static final String ENV_WORDS_URL = "ENV_WORDS_URL";
    private static final String DEFAULT_WORDS_URL = "http://storage.kelog.pl/sjp-words.zip";
    private static final String ENV_SEGMENTS_COUNT = "SEGMENTS_COUNT";
    private static final String ENV_SELECTED_SEGMENT = "SELECTED_SEGMENT";
    
    private static final String ENV_RESULTS_PUBLISHER = "RESULTS_PUBLISHER";
    private static final String ENV_RESULTS_PUBLISHER_CONSOLE = "CONSOLE";
    private static final String ENV_RESULTS_PUBLISHER_HTTP = "HTTP";
    
    private static final String ENV_PORT = "PORT";
    private static final String DEFAULT_PORT = "8080";
    private static final String ENV_SINK_URL = "SINK_URL";
    private static final String DEFAULT_SINK_URL = "http://localhost:8080";
    
    public static void main(String[] args) throws Exception {
        System.out.println("Starting doublewords application...");
        
        String mode = ofNullable(getenv(ENV_MODE)).orElse(ENV_MODE_SERVER);
        
        if (ENV_MODE_WORKER.equals(mode)) {
            bootstrapWorker();
        } else if (ENV_MODE_SERVER.equals(mode)) {
            bootstrapServer();
        } else {
            throw new RuntimeException(format("Unknown mode {0}", mode));
        }
    }
    
    private static void bootstrapWorker() throws Exception {
        System.out.println("Starting worker...");
        
        String wordsFileUrl = ofNullable(getenv(ENV_WORDS_URL)).orElse(DEFAULT_WORDS_URL);
        int segmentsCount = parseInt(ofNullable(getenv(ENV_SEGMENTS_COUNT)).orElse("1"));
        int selectedSegment = parseInt(ofNullable(getenv(ENV_SELECTED_SEGMENT)).orElse("0"));
        System.out.println(format(
                "Worker parameters: words URL: {0}, segments count: {1}, selected segment: {2}.",
                wordsFileUrl, segmentsCount, selectedSegment
        ));
        
        new Worker(wordsFileUrl, segmentsCount, selectedSegment, resultsPublisher()).runToCompletion();
    }
    
    private static ResultsPublisher resultsPublisher() {
        String publisher = ofNullable(getenv(ENV_RESULTS_PUBLISHER)).orElse(ENV_RESULTS_PUBLISHER_CONSOLE);
        
        if (ENV_RESULTS_PUBLISHER_CONSOLE.equals(publisher)) {
            return new ConsoleResultsPublisher();
        } else if (ENV_RESULTS_PUBLISHER_HTTP.equals(publisher)) {
            return new HttpResultsPublisher(ofNullable(getenv(ENV_SINK_URL)).orElse(DEFAULT_SINK_URL));
        } else {
            throw new RuntimeException(format("Unknown results publisher {0}", publisher));
        }
    }
    
    private static void bootstrapServer() {
        String port = ofNullable(getenv(ENV_PORT)).orElse(DEFAULT_PORT);
        System.out.println(format("Server port: {0}.", port));
        
        new CollectorServer(parseInt(port)).startInDaemonThread();
    }
}

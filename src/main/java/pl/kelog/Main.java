package pl.kelog;

import pl.kelog.collector.CollectorServer;
import pl.kelog.publisher.ConsoleResultsPublisher;
import pl.kelog.publisher.HttpResultsPublisher;
import pl.kelog.publisher.ResultsPublisher;
import pl.kelog.worker.Worker;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting doublewords application...");
        String mode = System.getenv("MODE");
        if ("WORKER".equals(mode)) {
            bootstrapWorker();
        } else if ("SERVER".equals(mode)) {
            bootstrapServer();
        } else {
            throw new RuntimeException("Unknown mode");
        }
    }
    
    private static void bootstrapWorker() throws IOException {
//        String wordsFileUrl = "http://storage.kelog.pl/100k.txt";
        System.out.println("Starting worker...");
        String wordsFileUrl = System.getenv("WORDS_URL");
        int segmentsCount = Integer.parseInt(System.getenv("SEGMENTS_COUNT"));
        int selectedSegment = Integer.parseInt(System.getenv("SELECTED_SEGMENT"));
        new Worker(wordsFileUrl, segmentsCount, selectedSegment, resultsPublisher()).runToCompletion();
    }
    
    private static ResultsPublisher resultsPublisher() {
        String publisher = System.getenv("RESULTS_PUBLISHER");
        if ("CONSOLE".equals(publisher)) {
            return new ConsoleResultsPublisher();
        } else if ("HTTP".equals(publisher)) {
            return new HttpResultsPublisher(System.getenv("SINK_URL"));
        } else {
            throw new RuntimeException("Unknown results publisher");
        }
    }
    
    private static void bootstrapServer() {
        String port = System.getenv("PORT");
        if (port == null) {
            port = "8080";
        }
        System.out.println("Starting server on port " + port + "...");
        
        new CollectorServer(Integer.parseInt(port)).start();
    }
    
}

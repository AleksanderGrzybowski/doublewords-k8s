package pl.kelog;

import pl.kelog.publisher.ConsoleResultsPublisher;
import pl.kelog.worker.Worker;

public class Main {
    public static void main(String[] args) throws Exception {
        new Worker("http://storage.kelog.pl/part.txt", 10, 4, new ConsoleResultsPublisher()).runToCompletion();
    }
    
}

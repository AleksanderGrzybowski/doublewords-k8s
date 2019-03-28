package pl.kelog;

import pl.kelog.collector.CollectorServer;

public class Main {
    public static void main(String[] args) throws Exception {
//        new Worker("http://storage.kelog.pl/100k.txt", 111, 110, new ConsoleResultsPublisher()).runToCompletion();
        new CollectorServer(8080).start();
    }
}

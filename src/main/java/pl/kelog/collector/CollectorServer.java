package pl.kelog.collector;

import spark.Spark;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CollectorServer {
    private final int port;
    private final Set<String> matches = Collections.synchronizedSet(new HashSet<>());
    
    public CollectorServer(int port) {
        this.port = port;
    }
    
    public void start() {
        Spark.port(port);
        
        Spark.post("/publish", (request, response) -> {
            String word = request.queryParams("word");
            if (word == null) {
                throw new AssertionError("Published word can't be null");
            }
            System.out.println("Got word: " + word);
            matches.add(word);
            return 200;
        });
        
        Spark.get("/", ((request, response) -> matches));
    }
}

package pl.kelog.collector;

import spark.Request;

import static spark.Spark.*;

public class CollectorServer {
    
    private static final String NULL_MESSAGE = "Request word can't be null";
    
    private final int port;
    private final FoundWordsRepository foundWordsRepository = new FoundWordsRepository();
    
    public CollectorServer(int port) {
        this.port = port;
    }
    
    public void startInDaemonThread() {
        port(port);
        
        post("/store", (request, response) -> handleStore(request));
        get("/", (request, response) -> foundWordsRepository.list());
    }
    
    private int handleStore(Request request) {
        String word = request.queryParams("word");
        if (word == null) {
            System.err.println(NULL_MESSAGE);
            throw new IllegalArgumentException(NULL_MESSAGE);
        }
        
        foundWordsRepository.add(word);
        return 200;
    }
}

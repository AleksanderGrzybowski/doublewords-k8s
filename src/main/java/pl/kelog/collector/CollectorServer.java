package pl.kelog.collector;

import pl.kelog.common.Constants;
import spark.Request;

import static pl.kelog.common.Constants.POST_WORD_PARAM_NAME;
import static spark.Spark.*;

/**
 * This component is a web server that acts as a sink for
 * all found words. It can be ran easily in IntelliJ for local
 * development as a second configuration in "Run configurations".
 */
public class CollectorServer {
    
    private static final String NULL_ERROR_MESSAGE = "Word can't be null";
    
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
        String word = request.queryParams(POST_WORD_PARAM_NAME);
        if (word == null) {
            System.err.println(NULL_ERROR_MESSAGE);
            throw new IllegalArgumentException(NULL_ERROR_MESSAGE);
        }
        
        foundWordsRepository.add(word);
        return Constants.HTTP_STATUS_OK;
    }
}

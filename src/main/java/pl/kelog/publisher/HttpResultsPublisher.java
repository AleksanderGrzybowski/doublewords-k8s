package pl.kelog.publisher;

import pl.kelog.dto.SearchResult;

import java.util.List;

public class HttpResultsPublisher implements ResultsPublisher {
    
    private final String sinkUrl;
    
    public HttpResultsPublisher(String sinkUrl) {
        this.sinkUrl = sinkUrl;
    }
    
    public void publish(List<SearchResult> results) {
        System.out.println("TODO");
    }
}

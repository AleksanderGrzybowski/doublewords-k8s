package pl.kelog.publisher;

import pl.kelog.dto.SearchResult;

import java.util.List;

public interface ResultsPublisher {
    
    void publish(List<SearchResult> results);
}

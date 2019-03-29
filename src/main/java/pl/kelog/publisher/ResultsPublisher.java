package pl.kelog.publisher;

import pl.kelog.dto.SearchResult;

public interface ResultsPublisher {
    
    void publishAsync(SearchResult result);
    void flush();
}

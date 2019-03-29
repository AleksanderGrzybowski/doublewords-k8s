package pl.kelog.publisher;

import pl.kelog.dto.SearchResult;

import java.text.MessageFormat;

public class ConsoleResultsPublisher implements ResultsPublisher {
    
    @Override
    public void publishAsync(SearchResult result) {
        System.out.println(MessageFormat.format("[console] Published {0}.", result));
    }
    
    @Override
    public void flush() {
    }
}

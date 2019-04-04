package pl.kelog.publisher;

import pl.kelog.dto.SearchResult;

import java.text.MessageFormat;

/**
 * For local testing, we don't need HTTP server for collecting results,
 * simple console output will do.
 */
public class ConsoleResultsPublisher implements ResultsPublisher {
    
    @Override
    public void publishAsync(SearchResult result) {
        System.out.println(MessageFormat.format("[console] Published {0}.", result));
    }
    
    @Override
    public void flush() {
    }
}

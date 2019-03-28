package pl.kelog.publisher;

import pl.kelog.dto.SearchResult;

import java.util.List;
import java.util.stream.Collectors;

public class ConsoleResultsPublisher implements ResultsPublisher {
    
    public void publish(List<SearchResult> results) {
        System.out.println("Mock publishing " + results.stream().map(r -> r.first + "-" + r.second).collect(Collectors.toList()));
    }
}

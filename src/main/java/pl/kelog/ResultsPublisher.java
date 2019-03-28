package pl.kelog;

import java.util.List;
import java.util.stream.Collectors;

class ResultsPublisher {
    
    void publish(List<SearchResult> results) {
        System.out.println("Mock publishing " + results.stream().map(r -> r.first + "- " + r.second).collect(Collectors.toList()));
    }
}

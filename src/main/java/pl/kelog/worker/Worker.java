package pl.kelog.worker;

import pl.kelog.dto.SearchResult;
import pl.kelog.publisher.ResultsPublisher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pl.kelog.worker.FileFetcher.fetchWordsFile;

public class Worker {
    
    private final String wordsFileUrl;
    private final int segmentsCount;
    private final int selectedSegment;
    private final ResultsPublisher publisher;
    
    public Worker(String wordsFileUrl, int segmentsCount, int selectedSegment, ResultsPublisher publisher) {
        this.wordsFileUrl = wordsFileUrl;
        this.segmentsCount = segmentsCount;
        this.selectedSegment = selectedSegment;
        this.publisher = publisher;
    }
    
    public void runToCompletion() throws Exception {
        List<String> words = fetchWordsFile(wordsFileUrl)
                .stream()
                .filter(w -> w.length() >= 5)
                .collect(Collectors.toList());
        
        System.out.println("Total word count in file: " + words.size());
        
        List<SearchResult> results = search(words, segmentsCount, selectedSegment);
        System.out.println("Found results: " + results.size());
        
        publisher.flush();
    }
    
    private List<SearchResult> search(List<String> words, int segmentsCount, int segmentIndex) {
        Set<String> memo = new HashSet<>(words);
        
        int total = words.size();
        int slice = total / segmentsCount;
        int from = segmentIndex * slice;
        int to = from + slice;
        
        System.out.println("From: " + from + ", to: " + to + ", total:" + (to - from) + "...");
        
        List<SearchResult> results = new ArrayList<>();
        
        for (int i = from; i < to; i++) {
            if ((i - from) % 100 == 0) {
                logProgress(from, to, i);
            }
            
            //noinspection ForLoopReplaceableByForEach
            for (int j = 0; j < words.size(); j++) {
                if (memo.contains(words.get(i) + words.get(j))) {
                    SearchResult result = new SearchResult(words.get(i), words.get(j));
                    System.out.println("Found: " + result);
                    publisher.publishAsync(result);
                }
            }
        }
        
        return results;
    }
    
    private static void logProgress(int from, int to, int i) {
        double processed = i - from;
        double all = to - from;
        double percentage = 100 * processed / all;
        System.out.println("Progess: " + String.format("%.2f", percentage) + "%");
    }
}

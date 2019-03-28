package pl.kelog.worker;

import pl.kelog.dto.SearchResult;
import pl.kelog.publisher.ResultsPublisher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
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
    
    public void runToCompletion() throws IOException {
        String content = fetchWordsFile(wordsFileUrl);
        
        List<String> words = new ArrayList<>(asList(content.split("\n")));
        List<SearchResult> results = search(words, segmentsCount, selectedSegment);
        publisher.publish(results);
    }
    
    private static List<SearchResult> search(List<String> words, int segmentsCount, int segmentIndex) {
        Set<String> set = new HashSet<>(words);
        int total = words.size();
        int slice = total / segmentsCount;
        int from = segmentIndex * slice;
        int to = (segmentIndex + 1) * slice;
        
        System.out.println("From: " + from + ", to: " + to + ", total:" + (to - from));
        
        List<SearchResult> results = new ArrayList<>();
        
        for (int i = from; i < to; i++) {
            if ((to - from) % 10000 == 0) {
                logProgress(from, to, i);
            }
            for (String word : words) {
                if (set.contains(words.get(i) + word)) {
                    SearchResult result = new SearchResult(words.get(i), word);
                    System.out.println("Found: " + result);
                    results.add(result);
                }
            }
        }
        
        return results;
    }
    
    private static void logProgress(int from, int to, int i) {
        double processed = i - from;
        double all = to - from;
        System.out.println("Progess: " + 100 * processed / all + "%");
    }
}

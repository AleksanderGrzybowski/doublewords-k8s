package pl.kelog.worker;

import pl.kelog.dto.SearchResult;
import pl.kelog.publisher.ResultsPublisher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toList;
import static pl.kelog.worker.WordsFileFetcher.fetchPolishWords;

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
        List<String> allWords = fetchPolishWords(wordsFileUrl);
        System.out.println(format("Total word count in file before filtering: {0} words. ", allWords.size()));
        
        List<String> sensibleWords = allWords.stream()
                .filter(WordsFilter::isSensibleWord)
                .collect(toList());
        System.out.println(format("Total word count in file after filtering: {0} words. ", sensibleWords.size()));
        
        searchAndPublishResults(sensibleWords, segmentsCount, selectedSegment);
        publisher.flush();
    }
    
    private void searchAndPublishResults(List<String> words, int segmentsCount, int segmentIndex) {
        Set<String> memo = new HashSet<>(words);
        
        int total = words.size();
        int slice = total / segmentsCount;
        int from = segmentIndex * slice;
        int to = from + slice;
        
        System.out.println(format("Processing list of words, from: {0}, to: {1}, total: {2}...", from, to, to - from));
        
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
    }
    
    private static void logProgress(int from, int to, int i) {
        double processed = i - from;
        double all = to - from;
        double percentage = 100 * processed / all;
        System.out.println("Progess: " + String.format("%.2f", percentage) + "%");
    }
}

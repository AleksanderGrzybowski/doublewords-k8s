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
            String left = words.get(i);
            System.out.println(format("[{0}/{1}] {2}", Integer.toString(i - from + 1), Integer.toString(to - from + 1), left));
            
            //noinspection ForLoopReplaceableByForEach
            for (int j = 0; j < words.size(); j++) {
                String right = words.get(j);
                if (memo.contains(left + right)) {
                    SearchResult result = new SearchResult(left, right);
                    System.out.println(format("Found: {0}", result));
                    publisher.publishAsync(result);
                }
            }
        }
    }
}

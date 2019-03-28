package pl.kelog.worker;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import pl.kelog.dto.SearchResult;
import pl.kelog.publisher.ResultsPublisher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

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
        int total = words.size();
        int slice = total / segmentsCount;
        int from = segmentIndex * slice;
        int to = (segmentIndex + 1) * slice;
        
        System.out.println("From: " + from + ", to: " + to + ", total:" + (to - from));
        
        List<SearchResult> results = new ArrayList<>();
        
        for (int i = from; i < to; i++) {
            if ((to-from) % 1000 == 0) {
                double processed = i - from;
                double all = to-from;
                System.out.println("Progess: " + 100*processed/all + "%");
            }
            for (int j = 0; j < words.size(); j++) {
                if (words.contains(words.get(i) + words.get(j))) {
                    results.add(new SearchResult(words.get(i), words.get(j)));
                }
            }
        }
        
        return results;
    }
    
    
    private static String fetchWordsFile(String url) throws IOException {
        System.out.println("Fetching words file from URL: " + url + " ...");
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        
        HttpResponse response = client.execute(request);
        
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            content.append(line).append("\n");
        }
        System.out.println("File fetched successfully, size: " + content.length() + " bytes.");
        return content.toString();
    }
    
}

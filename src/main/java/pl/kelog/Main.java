package pl.kelog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String content = fetchWordsFile("http://storage.kelog.pl/part.txt");
        
        List<String> words = new ArrayList<>(Arrays.asList(content.split("\n")));
        
        search(words, 11, 10);
    }
    
    private static void search(List<String> words, int segmentsCount, int segmentIndex) {
        int total = words.size();
        int slice = total / segmentsCount;
        int from = segmentIndex * slice;
        int to = (segmentIndex + 1) * slice;
        
        
        for (int i = from; i < to; i++) {
            System.out.println(words.get(i));
            for (int j = 0; j < words.size(); j++) {
                if (words.contains(words.get(i) + words.get(j))) {
                    publish(words.get(i), words.get(j));
                }
            }
        }
    }
    
    private static void publish(String first, String second) {
        System.out.println("Found: " + first + "-" + second);
    }
    
    private static String fetchWordsFile(String url) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        
        HttpResponse response = client.execute(request);
        
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        
        String content = "";
        String line;
        while ((line = rd.readLine()) != null) {
            content += line + "\n";
        }
        return content;
    }
}

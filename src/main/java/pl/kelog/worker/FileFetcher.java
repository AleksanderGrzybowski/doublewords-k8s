package pl.kelog.worker;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

class FileFetcher {
    
    static List<String> fetchWordsFile(String url) throws IOException {
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
        
        return new ArrayList<>(asList(content.toString().split("\n")));
    }
    
}

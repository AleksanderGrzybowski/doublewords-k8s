package pl.kelog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        String content = fetchWordsFile("http://storage.kelog.pl/part.txt");
        System.out.println(content);
        
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

package pl.kelog.publisher;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import pl.kelog.dto.SearchResult;

import java.util.List;

import static java.util.Collections.singletonList;

public class HttpResultsPublisher implements ResultsPublisher {
    
    private final String sinkUrl;
    
    public HttpResultsPublisher(String sinkUrl) {
        this.sinkUrl = sinkUrl;
    }
    
    public void publish(List<SearchResult> results) throws Exception {
        for (SearchResult result : results) {
            publish(result);
        }
    }
    
    private void publish(SearchResult searchResult) throws Exception {
        System.out.println("URL " + sinkUrl + " publishing result " + searchResult + "...");
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(sinkUrl);
        request.setEntity(new UrlEncodedFormEntity(singletonList(new BasicNameValuePair("word", searchResult.toString()))));
    
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
    
        if (statusCode == 200) {
            System.out.println("Successfully published " + searchResult + ".");
        } else {
            System.out.println("Failed to publish " + searchResult + " - HTTP " + statusCode);
        }
    }
}

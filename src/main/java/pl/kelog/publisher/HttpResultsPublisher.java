package pl.kelog.publisher;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import pl.kelog.dto.SearchResult;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.text.MessageFormat.format;
import static java.util.Collections.singletonList;

public class HttpResultsPublisher implements ResultsPublisher {
    
    private static final String POST_WORD_PARAM_NAME = "word";
    
    private final String sinkUrl;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    public HttpResultsPublisher(String sinkUrl) {
        this.sinkUrl = sinkUrl;
    }
    
    @Override
    public void publishAsync(SearchResult searchResult) {
        executorService.submit((Callable<Void>) () -> {
            postToServer(searchResult);
            return null;
        });
    }
    
    @Override
    public void flush() {
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdownNow();
    }
    
    private void postToServer(SearchResult searchResult) throws Exception {
        System.out.println(format("Publishing {0} using {1}...", searchResult, sinkUrl));
        
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(sinkUrl);
        request.setEntity(new UrlEncodedFormEntity(singletonList(
                new BasicNameValuePair(POST_WORD_PARAM_NAME, searchResult.toString())
        )));
        
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        
        if (statusCode == 200) {
            System.out.println("OK");
        } else {
            System.err.println(format("Failed to publish {0}, HTTP error {1}.", searchResult, statusCode));
        }
    }
}

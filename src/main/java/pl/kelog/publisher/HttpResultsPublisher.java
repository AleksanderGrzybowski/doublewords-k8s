package pl.kelog.publisher;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import pl.kelog.dto.SearchResult;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.text.MessageFormat.format;
import static java.util.Collections.singletonList;
import static pl.kelog.common.Constants.HTTP_STATUS_OK;
import static pl.kelog.common.Constants.POST_WORD_PARAM_NAME;

public class HttpResultsPublisher implements ResultsPublisher {
    
    private static final int THREAD_POOL_SIZE = 5;
    private static final int POOL_TERMINATION_TIMEOUT_SECONDS = 10;
    
    private final String sinkUrl;
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    
    public HttpResultsPublisher(String sinkUrl) {
        this.sinkUrl = sinkUrl;
    }
    
    /**
     * Callable is used here instead of Runnable because of
     * checked exceptions.
     */
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
            executorService.awaitTermination(POOL_TERMINATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) { // safe to ignore
            e.printStackTrace();
        }
        executorService.shutdownNow();
    }
    
    private void postToServer(SearchResult searchResult) throws Exception {
        System.out.println(format("Publishing {0} using {1}...", searchResult, sinkUrl));
        
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(sinkUrl);
        request.setEntity(
                new UrlEncodedFormEntity(
                        singletonList(new BasicNameValuePair(POST_WORD_PARAM_NAME, searchResult.toString())),
                        StandardCharsets.UTF_8
                )
        );
        
        HttpResponse response = client.execute(request);
        int statusCode = response.getStatusLine().getStatusCode();
        
        if (statusCode == HTTP_STATUS_OK) {
            System.out.println("OK");
        } else {
            System.err.println(format("Failed to publish {0}, HTTP error {1}.", searchResult, statusCode));
        }
    }
}

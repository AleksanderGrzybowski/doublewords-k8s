package pl.kelog.scraper;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.text.MessageFormat.format;

public class SJPScraper {
    
    private static final String SJP_BASE_URL = "https://sjp.pl/slownik/lp.phtml?page=";
    private static final String OUTPUT_FILENAME = "sjp-words.txt";
    
    private static final IntStream WEBSITE_PAGINATION_RANGE = IntStream.rangeClosed(1, 4482);
    private static final int THREAD_COUNT = 100;
    
    public static void main(String[] args) throws Exception {
        List<String> words = new ForkJoinPool(THREAD_COUNT).submit(
                createTask()
        ).get();
    
        System.out.println(format("Scraped {0} words.", words.size()));
        FileUtils.writeLines(new File(OUTPUT_FILENAME), words);
    }
    
    private static Callable<List<String>> createTask() {
        return () -> WEBSITE_PAGINATION_RANGE.boxed()
                .parallel()
                .flatMap(SJPScraper::extractWords)
                .filter(SJPScraper::isSensibleWord)
                .map(String::toLowerCase)
                .sorted()
                .collect(Collectors.toList());
    }
    
    private static Stream<? extends String> extractWords(Integer pageNumber) {
        Document document;
        try {
            document = Jsoup.connect(SJP_BASE_URL + pageNumber).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Successfully fetched page no. " + pageNumber);
        
        Elements words = document.select("tr > td > a");
        
        return words.stream().map(Element::ownText);
    }
    
    private static boolean isSensibleWord(String word) {
        return !word.contains(" ") && !word.contains("-") && !word.contains(".") && !word.contains("'") && word.length() >= 4;
    }
}

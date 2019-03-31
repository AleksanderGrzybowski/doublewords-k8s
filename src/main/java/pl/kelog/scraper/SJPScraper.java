package pl.kelog.scraper;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * We need a simple, but complete list of Polish words, and previously
 * we used a pretty popular list for gaming purposes like Scrabble:
 * https://sjp.pl/slownik/growy/sjp-20190225.zip
 * <p>
 * However this list is huge and contains almost every possible form of every word,
 * so we need a smaller and simpler list. I couldn't find such list on the Internet,
 * so I ended up scraping official Polish Dictionary site for all Polish words
 * in basic form.
 */
public class SJPScraper {
    
    private static final String DICTIONARY_BASE_URL = "https://sjp.pl/slownik/lp.phtml?page=";
    private static final String WORDS_OUTPUT_FILENAME = "sjp-words.txt";
    
    /**
     * Update if needed, but the need is very unlikely.
     */
    private static final IntStream DICTIONARY_HTML_PAGINATION_RANGE = IntStream.rangeClosed(1, 4482);
    
    /**
     * Play with concurrency level, but be careful of system (kernel)
     * level TCP connection throttling.
     */
    private static final int THREAD_COUNT = 100;
    
    private static final Set<String> BANNED_CHARACTERS = new HashSet<>(asList(
            " ", "-", ".", "'"
    ));
    
    private static final int MIN_WORD_LENGTH = 4;
    
    public static void main(String[] args) throws Exception {
        List<String> words = new ForkJoinPool(THREAD_COUNT).submit(
                createScrapeTask()
        ).get();
        
        System.out.println(format(
                "Scraped {0} words, saving file {1}...",
                words.size(),
                WORDS_OUTPUT_FILENAME
        ));
        FileUtils.writeLines(new File(WORDS_OUTPUT_FILENAME), words);
        System.out.println("File saved.");
    }
    
    private static Callable<List<String>> createScrapeTask() {
        return () -> DICTIONARY_HTML_PAGINATION_RANGE.boxed()
                .parallel()
                .flatMap(SJPScraper::extractWords)
                .filter(SJPScraper::isSensibleWord)
                .map(String::toLowerCase)
                .sorted()
                .collect(toList());
    }
    
    private static Stream<String> extractWords(Integer pageNumber) {
        return fetchHtmlPage(pageNumber)
                .select("tr > td > a")
                .stream()
                .map(Element::ownText);
    }
    
    private static Document fetchHtmlPage(Integer pageNumber) {
        System.out.println(format("Fetching page no. {0}...", pageNumber));
        
        Document document;
        try {
            document = Jsoup.connect(DICTIONARY_BASE_URL + pageNumber).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        System.out.println(format("Successfully fetched page no. {0}.", pageNumber));
        return document;
    }
    
    private static boolean isSensibleWord(String word) {
        return word.length() >= MIN_WORD_LENGTH && BANNED_CHARACTERS.stream().noneMatch(word::contains);
    }
}

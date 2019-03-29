package pl.kelog.collector;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toList;

class FoundWordsRepository {
    
    private final Set<String> foundWords = Collections.synchronizedSet(new HashSet<>());
    
    void add(String word) {
        foundWords.add(word);
        System.out.println(format("Stored word: {0}, current word count: {1}", word, foundWords.size()));
    }
    
    List<String> list() {
        return foundWords.stream().sorted().collect(toList());
    }
}

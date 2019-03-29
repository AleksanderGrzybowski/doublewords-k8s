package pl.kelog.worker;

import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singletonList;

class WordsFilter {
    
    private static Set<String> BANNED_PREFIXES = new HashSet<>(singletonList("nie"));
    
    static boolean isSensibleWord(String word) {
        return word.length() >= 4 && BANNED_PREFIXES.stream().noneMatch(word::startsWith);
    }
}

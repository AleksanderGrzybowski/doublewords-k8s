package pl.kelog.dto;

public class SearchResult {
    private final String first;
    private final String second;
    
    public SearchResult(String first, String second) {
        this.first = first;
        this.second = second;
    }
    
    @Override
    public String toString() {
        return first + "-" + second;
    }
}

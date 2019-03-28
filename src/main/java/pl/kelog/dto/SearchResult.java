package pl.kelog.dto;

public class SearchResult {
    public final String first;
    public final String second;
    
    public SearchResult(String first, String second) {
        this.first = first;
        this.second = second;
    }
    
    @Override
    public String toString() {
        return first + "-" + second;
    }
}

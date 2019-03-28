package pl.kelog;

public class Main {
    public static void main(String[] args) throws Exception {
        new Worker("http://storage.kelog.pl/part.txt", 10, 4, new ResultsPublisher()).runToCompletion();
    }
    
}

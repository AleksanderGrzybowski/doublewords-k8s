package pl.kelog.worker;

import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

class WordsFileFetcher {
    
    static List<String> fetchPolishWords(String zipUrl) throws Exception {
        System.out.println(format("Fetching compressed words file from URL: {0}...", zipUrl));
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(zipUrl);
        
        HttpResponse response = client.execute(request);
        System.out.println(format("Response status code {0}.", response.getStatusLine().getStatusCode()));
        
        byte[] zipContent = IOUtils.toByteArray(response.getEntity().getContent());
        System.out.println(format("Stored ZIP file content in memory, size: {0} bytes.", zipContent.length));
        
        File zipFile = File.createTempFile("words-zip", ".zip");
        System.out.println(format("Storing ZIP file in {0}...", zipFile.getAbsolutePath()));
        FileUtils.writeByteArrayToFile(zipFile, zipContent);
        
        String extractedFolderPath = Files.createTempDirectory("words-zip-content").toAbsolutePath().toString();
        System.out.println(format("Extracting ZIP file in {0}...", extractedFolderPath));
        ZipFile zip = new ZipFile(zipFile);
        zip.extractAll(extractedFolderPath);
        System.out.println("Extraction completed.");
        
        File wordsFile = Arrays.stream(requireNonNull(new File(extractedFolderPath).listFiles()))
                .max(Comparator.comparing(File::length))
                .orElseThrow(() -> new RuntimeException("No files found in archive"));
        System.out.println(format("Using file {0} as words file, size: {1} bytes", wordsFile.getAbsolutePath(), wordsFile.length()));
        
        // TODO: do not store everything in memory, require less heap
        String content = FileUtils.readFileToString(wordsFile, StandardCharsets.UTF_8);
        System.out.println(format("File loaded successfully, size in-memory: {0} bytes.", content.length()));
        
        return new ArrayList<>(asList(content.split("\n")));
    }
}

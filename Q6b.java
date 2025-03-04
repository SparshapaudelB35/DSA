
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Q6b {

    // Inner class for the multithreaded web crawler
    public static class MultithreadedWebCrawler {

        // Data structure to store URLs to be crawled
        private final Queue<String> urlQueue = new LinkedList<>();
        private final Set<String> visitedUrls = new HashSet<>();

        // Thread pool for concurrent crawling
        private final ExecutorService executorService;

        // Maximum number of threads
        private static final int MAX_THREADS = 10;

        // Constructor to initialize the crawler with a starting URL
        public MultithreadedWebCrawler(String startUrl) {
            this.urlQueue.add(startUrl);
            this.executorService = Executors.newFixedThreadPool(MAX_THREADS);
        }

        // Method to start crawling
        public void startCrawling() {
            while (!urlQueue.isEmpty()) {
                String currentUrl = urlQueue.poll();

                if (currentUrl == null || visitedUrls.contains(currentUrl)) {
                    continue; // Skip already visited URLs
                }

                // Submit the crawling task to the thread pool
                executorService.submit(new CrawlTask(currentUrl));
            }

            // Shutdown the executor service after all tasks are submitted
            executorService.shutdown();
            try {
                // Wait for all threads to finish
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }

        // Task to crawl a single URL
        private class CrawlTask implements Runnable {

            private final String url;

            public CrawlTask(String url) {
                this.url = url;
            }

            @Override
            public void run() {
                try {
                    System.out.println("Crawling: " + url);

                    // Fetch the content of the web page
                    String content = fetchContent(url);

                    // Process the fetched content (e.g., extract links or index data)
                    processContent(content);

                    // Mark the URL as visited
                    synchronized (visitedUrls) {
                        visitedUrls.add(url);
                    }
                } catch (Exception e) {
                    System.err.println("Error crawling " + url + ": " + e.getMessage());
                }
            }

            // Method to fetch the content of a web page
            private String fetchContent(String urlString) throws Exception {
                StringBuilder content = new StringBuilder();
                HttpURLConnection connection = null;

                try {
                    URL url = new URL(urlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000); // Timeout after 5 seconds
                    connection.setReadTimeout(5000);

                    if (connection.getResponseCode() != 200) {
                        throw new RuntimeException("Failed to fetch content: HTTP " + connection.getResponseCode());
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    reader.close();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

                return content.toString();
            }

            // Method to process the fetched content
            private void processContent(String content) {
                // Example: Extract links from the content (simple regex-based extraction)
                String[] words = content.split("\\s+");
                for (String word : words) {
                    if (word.startsWith("http")) {
                        synchronized (urlQueue) {
                            if (!visitedUrls.contains(word)) {
                                urlQueue.add(word);
                            }
                        }
                    }
                }

                // Save the content to a file or database (mock implementation)
                saveContent(content);
            }

            private void saveContent(String content) {
                String filePath = "saved_content.txt"; // Define the file path

                try (FileWriter writer = new FileWriter(filePath, true)) { // Append mode
                    writer.write(content + System.lineSeparator()); // Write content and add a newline
                    System.out.println("Content saved successfully.");
                } catch (IOException e) {
                    System.err.println("Error saving content: " + e.getMessage());
                }
            }
        }
    }

    // Main method to test the web crawler
    public static void main(String[] args) {
        // Start URL for crawling
        String startUrl = "https://quotes.toscrape.com/";

        // Create and start the web crawler
        MultithreadedWebCrawler crawler = new MultithreadedWebCrawler(startUrl);
        crawler.startCrawling();
    }
}

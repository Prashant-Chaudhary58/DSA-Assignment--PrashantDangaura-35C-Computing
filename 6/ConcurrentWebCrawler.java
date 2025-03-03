import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ConcurrentWebCrawler {
    private final ExecutorService executorService;
    private final BlockingQueue<String> urlQueue;
    private final ConcurrentHashMap<String, String> crawledData;
    private final Set<String> visitedUrls;
    private final int numThreads;

    public ConcurrentWebCrawler(int numThreads) {
        this.numThreads = numThreads;
        this.executorService = Executors.newFixedThreadPool(numThreads);
        this.urlQueue = new LinkedBlockingQueue<>();
        this.crawledData = new ConcurrentHashMap<>();
        this.visitedUrls = ConcurrentHashMap.newKeySet();
    }

    public void addUrl(String url) {
        if (visitedUrls.add(url)) {
            urlQueue.offer(url);
        }
    }

    public void startCrawling() {
        for (int i = 0; i < numThreads; i++) {
            executorService.submit(new Worker());
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("Crawling interrupted: " + e.getMessage());
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private class Worker implements Runnable {
        @Override
        public void run() {
            while (true) {
                String url = null;
                try {
                    url = urlQueue.poll(10, TimeUnit.SECONDS);
                    if (url == null) {
                        break;
                    }
                    String content = fetchContent(url);
                    crawledData.put(url, content);
                    List<String> newUrls = extractUrls(content, url); // Pass url as base
                    for (String newUrl : newUrls) {
                        addUrl(newUrl);
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Error processing URL " + url + ": " + e.getMessage());
                }
            }
        }
    }

    private String fetchContent(String urlString) throws IOException {
        StringBuilder content = new StringBuilder();
        URL urlObj = new URL(urlString);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(urlObj.openStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private List<String> extractUrls(String content, String baseUrl) {
        List<String> urls = new ArrayList<>();
        Document doc = Jsoup.parse(content, baseUrl); // Use Jsoup to parse with base URL
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String absUrl = link.absUrl("href"); // Get absolute URL
            if (!absUrl.isEmpty()) {
                urls.add(absUrl);
            }
        }
        return urls;
    }

    public ConcurrentHashMap<String, String> getCrawledData() {
        return crawledData;
    }

    public static void main(String[] args) {
        ConcurrentWebCrawler crawler = new ConcurrentWebCrawler(10);
        crawler.addUrl("https://schoolworkspro.com/modules/st5008cem-programming-for-developers");
        crawler.startCrawling();

        System.out.println("Crawled pages: " + crawler.getCrawledData().size());

        for (Map.Entry<String, String> entry : crawler.getCrawledData().entrySet()) {
            System.out.println("URL: " + entry.getKey());
            System.out.println("Content:\n" + entry.getValue());
            System.out.println("---------------------------------------------------");
        }
    }
}


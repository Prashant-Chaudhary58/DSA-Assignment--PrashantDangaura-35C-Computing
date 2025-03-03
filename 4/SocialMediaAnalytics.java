import java.util.*;
import java.util.regex.*;

public class SocialMediaAnalytics {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HashMap<String, Integer> hashtagCount = new HashMap<>();
        Pattern hashtagPattern = Pattern.compile("#\\w+");

        System.out.println("Enter 7 tweets (Each field will be entered separately):");

        List<Tweet> tweets = new ArrayList<>(); // Store tweets for 4a

        for (int i = 1; i <= 7; i++) {
            System.out.println("\nTweet " + i + ":");

            int userId = getValidIntegerInput(scanner, "Enter User ID: ");
            int tweetId = getValidIntegerInput(scanner, "Enter Tweet ID: ");

            System.out.print("Enter Tweet Text: ");
            String tweetText = scanner.nextLine().trim();

            String tweetDate = getValidDateInput(scanner, "Enter Tweet Date (YYYY-MM-DD): ");

            tweets.add(new Tweet(userId, tweetId, tweetText, tweetDate)); // Store tweet for 4a

            // Extract hashtags from tweet text
            Matcher matcher = hashtagPattern.matcher(tweetText);
            while (matcher.find()) {
                String hashtag = matcher.group().toLowerCase(); // Convert to lowercase
                hashtagCount.put(hashtag, hashtagCount.getOrDefault(hashtag, 0) + 1);
            }
        }
        scanner.close();

        // Sort hashtags by count (descending) and then by name (descending)
        List<Map.Entry<String, Integer>> sortedHashtags = new ArrayList<>(hashtagCount.entrySet());
        sortedHashtags.sort((a, b) -> {
            int countCompare = b.getValue().compareTo(a.getValue()); // Descending count
            return countCompare != 0 ? countCompare : b.getKey().compareTo(a.getKey()); // Descending name
        });

        // Display top 3 trending hashtags
        System.out.println("\n+------------+-------+");
        System.out.println("| Hashtag    | Count |");
        System.out.println("+------------+-------+");
        for (int i = 0; i < Math.min(3, sortedHashtags.size()); i++) {
            System.out.printf("| %-10s | %5d |\n", sortedHashtags.get(i).getKey(), sortedHashtags.get(i).getValue());
        }
        System.out.println("+------------+-------+");

        // Example usage of Tweet data from 4a (if needed)
        System.out.println("\nStored Tweets:");
        for (Tweet tweet : tweets) {
            System.out.println(tweet);
        }
    }

    // Helper method for validated integer input
    private static int getValidIntegerInput(Scanner scanner, String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    // Helper method for validated date input
    private static String getValidDateInput(Scanner scanner, String prompt) {
        Pattern datePattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (datePattern.matcher(input).matches()) {
                return input;
            } else {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    // Inner class to represent a Tweet (for 4a compatibility)
    static class Tweet {
        int userId;
        int tweetId;
        String tweetText;
        String tweetDate;

        public Tweet(int userId, int tweetId, String tweetText, String tweetDate) {
            this.userId = userId;
            this.tweetId = tweetId;
            this.tweetText = tweetText;
            this.tweetDate = tweetDate;
        }

        @Override
        public String toString() {
            return "Tweet{" +
                    "userId=" + userId +
                    ", tweetId=" + tweetId +
                    ", tweetText='" + tweetText + '\'' +
                    ", tweetDate='" + tweetDate + '\'' +
                    '}';
        }
    }
}
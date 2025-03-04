/*Write a solution to find the top 3 trending hashtags in February 2024. Every tweet may
contain several hashtags.
Return the result table ordered by count of hashtag, hashtag in descending order.

Explanation:
#HappyDay: Appeared in tweet IDs 13, 14, and 17, with a total count of 3 mentions.
#TechLife: Appeared in tweet IDs 16 and 18, with a total count of 2 mentions.
#WorkLife: Appeared in tweet ID 15, with a total count of 1 mention.
Note: Output table is sorted in descending order by hashtag_count and hashtag respectively.*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Q4a {
    public static void main(String[] args) {
        // Sample input: List of tweets
        List<Tweet> tweets = Arrays.asList(
            new Tweet(135, 13, "Enjoying a great start to the day. #HappyDay #MorningVibes", "2024-02-01"),
            new Tweet(136, 14, "Another #HappyDay with good vibes! #FeelGood", "2024-02-01"),
            new Tweet(137, 15, "Productivity peaks! #WorkLife #ProductiveDay", "2024-02-04"),
            new Tweet(138, 16, "Exploring new tech frontiers. #TechLife #Innovation", "2024-02-06"),
            new Tweet(139, 17, "Gratitude for today's moments. #HappyDay #Thankful", "2024-02-08"),
            new Tweet(140, 18, "Innovation drives us. #TechLife #FutureTech", "2024-02-08"),
            new Tweet(141, 19, "Connecting with nature's serenity. #Nature #Peaceful", "2024-02-09")
        );

        // Find the top 3 trending hashtags
        List<Map.Entry<String, Integer>> trendingHashtags = getTopHashtags(tweets, "2024-02");

        // Print the results
        System.out.println("+-----------+-------+");
        System.out.println("| hashtag   | count |");
        System.out.println("+-----------+-------+");
        for (Map.Entry<String, Integer> entry : trendingHashtags) {
            System.out.printf("| %-9s | %5d |\n", entry.getKey(), entry.getValue());
        }
        System.out.println("+-----------+-------+");
    }

    public static List<Map.Entry<String, Integer>> getTopHashtags(List<Tweet> tweets, String monthYear) {
        Map<String, Integer> hashtagCount = new HashMap<>();
        Pattern hashtagPattern = Pattern.compile("#\\w+"); // Regex for hashtags

        for (Tweet tweet : tweets) {
            if (tweet.tweetDate.startsWith(monthYear)) { // Filter for February 2024
                Matcher matcher = hashtagPattern.matcher(tweet.tweetText);
                while (matcher.find()) {
                    String hashtag = matcher.group();
                    hashtagCount.put(hashtag, hashtagCount.getOrDefault(hashtag, 0) + 1);
                }
            }
        }

        // Sort hashtags by count (descending), then by name (descending)
        List<Map.Entry<String, Integer>> sortedHashtags = new ArrayList<>(hashtagCount.entrySet());
        sortedHashtags.sort((a, b) -> {
            if (!a.getValue().equals(b.getValue())) {
                return b.getValue() - a.getValue(); // Sort by count descending
            }
            return b.getKey().compareTo(a.getKey()); // Sort by hashtag name descending
        });

        // Return top 3 trending hashtags
        return sortedHashtags.subList(0, Math.min(3, sortedHashtags.size()));
    }
}

// Helper class to represent a tweet
class Tweet {
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
}
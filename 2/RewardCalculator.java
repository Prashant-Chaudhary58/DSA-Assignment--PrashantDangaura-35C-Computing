import java.util.Arrays;

public class RewardCalculator {

    public static int calculateMinimumRewards(int[] studentRatings) {
        int studentCount = studentRatings.length;
        int[] rewardPoints = new int[studentCount];
        Arrays.fill(rewardPoints, 1);

        // Left to Right pass
        for (int i = 1; i < studentCount; i++) {
            if (studentRatings[i] > studentRatings[i - 1]) {
                rewardPoints[i] = rewardPoints[i - 1] + 1;
            }
        }

        // Right to Left pass
        for (int i = studentCount - 2; i >= 0; i--) {
            if (studentRatings[i] > studentRatings[i + 1]) {
                rewardPoints[i] = Math.max(rewardPoints[i], rewardPoints[i + 1] + 1);
            }
        }

        // Sum up rewards
        int totalRewards = 0;
        for (int reward : rewardPoints) {
            totalRewards += reward;
        }

        return totalRewards;
    }

    public static void main(String[] args) {
        int[] ratings1 = {1, 0, 2};
        int[] ratings2 = {1, 2, 2};

        System.out.println(calculateMinimumRewards(ratings1)); // Output: 5
        System.out.println(calculateMinimumRewards(ratings2)); // Output: 4
    }
}
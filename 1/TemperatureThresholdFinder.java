public class TemperatureThresholdFinder {

    public static int findMinimumTests(int samples, int levels) {
        if (samples == 1) {
            return levels;
        }

        int[][] testMatrix = new int[samples + 1][levels + 1];

        for (int i = 1; i <= samples; i++) {
            testMatrix[i][1] = 1;
        }

        for (int j = 1; j <= levels; j++) {
            testMatrix[1][j] = j;
        }

        for (int i = 2; i <= samples; i++) {
            for (int j = 2; j <= levels; j++) {
                int minTests = Integer.MAX_VALUE;
                int left = 1;
                int right = j;

                while (left <= right) {
                    int mid = left + (right - left) / 2;
                    int breakTests = testMatrix[i - 1][mid - 1];
                    int noBreakTests = testMatrix[i][j - mid];
                    int worstCaseTests = 1 + Math.max(breakTests, noBreakTests);

                    minTests = Math.min(minTests, worstCaseTests);

                    if (breakTests > noBreakTests) {
                        right = mid - 1;
                    } else {
                        left = mid + 1;
                    }
                }
                testMatrix[i][j] = minTests;
            }
        }
        return testMatrix[samples][levels];
    }

    public static void main(String[] args) {
        System.out.println(findMinimumTests(1, 2)); // Output: 2
        System.out.println(findMinimumTests(2, 6)); // Output: 3
        System.out.println(findMinimumTests(3, 14)); // Output: 4
    }
}
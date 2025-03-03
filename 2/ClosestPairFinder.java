public class ClosestPairFinder {

    public static int[] findClosestPair(int[] xCoords, int[] yCoords) {
        int n = xCoords.length;
        int minDistance = Integer.MAX_VALUE;
        int closestPairIndex1 = -1;
        int closestPairIndex2 = -1;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                // Calculate Manhattan distance
                int distance = Math.abs(xCoords[i] - xCoords[j]) + Math.abs(yCoords[i] - yCoords[j]);

                // Update minimum distance and lexicographically smallest pair
                if (distance < minDistance || (distance == minDistance && (i < closestPairIndex1 || (i == closestPairIndex1 && j < closestPairIndex2)))) {
                    minDistance = distance;
                    closestPairIndex1 = i;
                    closestPairIndex2 = j;
                }
            }
        }

        return new int[]{closestPairIndex1, closestPairIndex2};
    }

    public static void main(String[] args) {
        int[] xCoords = {1, 2, 3, 2, 4};
        int[] yCoords = {2, 3, 1, 2, 3};

        int[] result = findClosestPair(xCoords, yCoords);
        System.out.println("[" + result[0] + ", " + result[1] + "]");
    }
}
import java.util.*;

public class PackageDelivery {

    public static void main(String[] args) {
        int[] packages1 = {0, 0, 0, 1, 1, 0, 0, 1};
        int[][] roads1 = {
                {0, 1},
                {0, 2},
                {1, 3},
                {1, 4},
                {2, 5},
                {5, 6},
                {5, 7}
        };
        System.out.println("Output (Example 1): " + calculateMinRoads(packages1, roads1)); // Expected 2
    }

    public static int calculateMinRoads(int[] packages, int[][] roads) {
        int numLocations = packages.length;
        boolean hasPackages = false;
        for (int pkg : packages) {
            if (pkg == 1) {
                hasPackages = true;
                break;
            }
        }
        if (!hasPackages) {
            return 0;
        }

        @SuppressWarnings("unchecked")
        List<Integer>[] adjacencyList = new List[numLocations];
        for (int i = 0; i < numLocations; i++) {
            adjacencyList[i] = new ArrayList<>();
        }
        for (int[] road : roads) {
            int location1 = road[0];
            int location2 = road[1];
            adjacencyList[location1].add(location2);
            adjacencyList[location2].add(location1);
        }

        int[][] distances = new int[numLocations][numLocations];
        for (int i = 0; i < numLocations; i++) {
            Arrays.fill(distances[i], Integer.MAX_VALUE);
            breadthFirstSearch(i, adjacencyList, distances[i]);
        }

        int minRoadsRequired = Integer.MAX_VALUE;
        int totalSubsets = 1 << numLocations;

        for (int mask = 1; mask < totalSubsets; mask++) {
            if (!isPackageCovered(mask, packages, distances)) {
                continue;
            }

            List<Integer> selectedLocations = new ArrayList<>();
            for (int j = 0; j < numLocations; j++) {
                if (((mask >> j) & 1) == 1) {
                    selectedLocations.add(j);
                }
            }

            int tourCost = calculateTourCost(selectedLocations, distances);
            minRoadsRequired = Math.min(minRoadsRequired, tourCost);
        }

        return minRoadsRequired == Integer.MAX_VALUE ? -1 : minRoadsRequired;
    }

    private static void breadthFirstSearch(int startLocation, List<Integer>[] adjacencyList, int[] distances) {
        Queue<Integer> queue = new LinkedList<>();
        distances[startLocation] = 0;
        queue.offer(startLocation);

        while (!queue.isEmpty()) {
            int currentLocation = queue.poll();
            for (int neighbor : adjacencyList[currentLocation]) {
                if (distances[neighbor] == Integer.MAX_VALUE) {
                    distances[neighbor] = distances[currentLocation] + 1;
                    queue.offer(neighbor);
                }
            }
        }
    }

    private static boolean isPackageCovered(int mask, int[] packages, int[][] distances) {
        int numLocations = packages.length;
        for (int i = 0; i < numLocations; i++) {
            if (packages[i] == 1) {
                boolean isDelivered = false;
                for (int j = 0; j < numLocations; j++) {
                    if (((mask >> j) & 1) == 1) {
                        if (distances[i][j] <= 2) {
                            isDelivered = true;
                            break;
                        }
                    }
                }
                if (!isDelivered) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int calculateTourCost(List<Integer> selectedLocations, int[][] distances) {
        int numSelectedLocations = selectedLocations.size();
        int fullMask = (1 << numSelectedLocations) - 1;
        int[][] dp = new int[1 << numSelectedLocations][numSelectedLocations];

        for (int i = 0; i < (1 << numSelectedLocations); i++) {
            Arrays.fill(dp[i], Integer.MAX_VALUE);
        }

        dp[1 << 0][0] = 0;

        for (int mask = 0; mask < (1 << numSelectedLocations); mask++) {
            for (int i = 0; i < numSelectedLocations; i++) {
                if (((mask >> i) & 1) == 1 && dp[mask][i] != Integer.MAX_VALUE) {
                    for (int j = 0; j < numSelectedLocations; j++) {
                        if (((mask >> j) & 1) == 0) {
                            int nextMask = mask | (1 << j);
                            int cost = dp[mask][i] + distances[selectedLocations.get(i)][selectedLocations.get(j)];
                            dp[nextMask][j] = Math.min(dp[nextMask][j], cost);
                        }
                    }
                }
            }
        }

        int minCost = Integer.MAX_VALUE;
        for (int i = 0; i < numSelectedLocations; i++) {
            if (dp[fullMask][i] != Integer.MAX_VALUE) {
                minCost = Math.min(minCost, dp[fullMask][i] + distances[selectedLocations.get(i)][selectedLocations.get(0)]);
            }
        }

        return minCost;
    }
}
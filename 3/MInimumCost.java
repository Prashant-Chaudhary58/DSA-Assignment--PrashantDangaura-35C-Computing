import java.util.*;

class UnionFind {
    private int[] parent;
    private int[] rank;

    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }}

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);  }// Path compression
        return parent[x];
    }

    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return false;  // Already connected
        }
        // Union by rank
        if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        return true;
    }}

public class MInimumCost {

    public static int minTotalCost(int n, int[] modules, int[][] connections) {
        List<int[]> edges = new ArrayList<>();

        // Add module installation as virtual edges from a "virtual node" (node n)
        for (int i = 0; i < n; i++) {
            edges.add(new int[] {modules[i], n, i});
        }
        // Add actual connections
        for (int[] connection : connections) {
            int device1 = connection[0] - 1; // Convert to 0-based index
            int device2 = connection[1] - 1; // Convert to 0-based index
            int cost = connection[2];
            edges.add(new int[] {cost, device1, device2});
        }
        // Sort edges by cost
        edges.sort(Comparator.comparingInt(a -> a[0]));
        UnionFind uf = new UnionFind(n + 1); // Extra node for module installation
        int totalCost = 0;
        int edgesUsed = 0;
        // Kruskal’s MST Algorithm
        for (int[] edge : edges) {
            int cost = edge[0];
            int u = edge[1];
            int v = edge[2];

            if (uf.union(u, v)) {
                totalCost += cost;
                edgesUsed++;
                if (edgesUsed == n) { // Minimum spanning tree is complete
                    break;
                }}}
        return totalCost;
    }

    public static void main(String[] args) {
        int n = 3;
        int[] modules = {1, 2, 2};
        int[][] connections = {
            {1, 2, 1},
            {2, 3, 1}
        };
        
        System.out.println(minTotalCost(n, modules, connections)); // Output: 3
    }}


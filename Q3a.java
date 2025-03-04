/*You have a network of n devices. Each device can have its own communication module installed at a 
cost of modules [i - 1]. Alternatively, devices can communicate with each other using direct connections. 
The cost of connecting two devices is given by the array connections where each connections[j] = 
[device1j, device2j, costj] represents the cost to connect devices device1j and device2j. Connections are 
bidirectional, and there could be multiple valid connections between the same two devices with different 
costs. 
Goal: 
Determine the minimum total cost to connect all devices in the network. 
Input: 
n: The number of devices. 
modules: An array of costs to install communication modules on each device. 
connections: An array of connections, where each connection is represented as a triplet [device1j, 
device2j, costj]. 
Output: 
The minimum total cost to connect all devices. 
Example: 
Input: n = 3, modules = [1, 2, 2], connections = [[1, 2, 1], [2, 3, 1]] Output: 3 
Explanation: 
The best strategy is to install a communication module on the first device with cost 1 and connect the 
other devices to it with cost 2, resulting in a total cost of 3. */


import java.util.Arrays;

public class Q3a {
    // Helper class to represent an edge
    static class Edge {
        int device1, device2, cost;

        Edge(int device1, int device2, int cost) {
            this.device1 = device1;
            this.device2 = device2;
            this.cost = cost;
        }
    }

    // Helper class for Union-Find (Disjoint Set)
    static class UnionFind {
        int[] parent, rank;

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        // Find operation with path compression
        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        // Union operation with union by rank
        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
        }
    }

    // Method to find the minimum cost to connect all devices
    public static int minCostToConnectDevices(int n, int[] modules, int[][] connections) {
        // Create a list to store all edges (connections and virtual module edges)
        Edge[] edges = new Edge[connections.length + n]; // +n for the virtual edges

        // Add the real connection edges to the list
        int index = 0;
        for (int[] conn : connections) {
            edges[index++] = new Edge(conn[0] - 1, conn[1] - 1, conn[2]); // 0-based index
        }

        // Add the virtual edges (device to "hub")
        for (int i = 0; i < n; i++) {
            edges[index++] = new Edge(i, n, modules[i]); // Connect each device to the virtual "hub"
        }

        // Sort the edges by cost (ascending order)
        Arrays.sort(edges, (a, b) -> a.cost - b.cost);

        // Initialize Union-Find (Disjoint Set) for Kruskal's algorithm
        UnionFind uf = new UnionFind(n + 1); // n + 1 because we have n devices and the virtual "hub"

        int totalCost = 0;
        int count = 0;

        // Process the sorted edges
        for (Edge edge : edges) {
            int root1 = uf.find(edge.device1);
            int root2 = uf.find(edge.device2);

            if (root1 != root2) {
                uf.union(root1, root2);
                totalCost += edge.cost;
                count++;

                // If we've connected n devices (including the virtual hub), we can stop
                if (count == n) {
                    break;
                }
            }
        }

        return totalCost;
    }

    public static void main(String[] args) {
        // Example 1
        int n = 3;
        int[] modules = {1, 2, 2};
        int[][] connections = {{1, 2, 1}, {2, 3, 1}};
        System.out.println(minCostToConnectDevices(n, modules, connections)); 
    }
}


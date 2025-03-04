/*You have a map of a city represented by a graph with n nodes (representing locations) and edges where
edges[i] = [ai, bi] indicates a road between locations ai and bi. Each location has a value of either 0 or 1,
indicating whether there is a package to be delivered. You can start at any location and perform the
following actions:
Collect packages from all locations within a distance of 2 from your current location.
Move to an adjacent location.
Your goal is to collect all packages and return to your starting location.
Goal:
Determine the minimum number of roads you need to traverse to collect all packages.
Input:
packages: An array of package values for each location.
roads: A 2D array representing the connections between locations.
Output:
The minimum number of roads to traverse.


Note that if you pass a roads several times, you need to count it into the answer several times.
 */

 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Map;
 import java.util.Queue;
 import java.util.Set;
 
 public class Q4b {
     public static void main(String[] args) {
         int[] packages1 = {1, 0, 0, 0, 0, 1};
         int[][] roads1 = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}};
         System.out.println(minimun_roads_to_collect_packages(packages1, roads1)); 
 
         int[] packages2 = {1, 0, 0, 1, 1, 0, 0, 1};
         int[][] roads2 = {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {5, 6}, {5, 7}};
         System.out.println(minimun_roads_to_collect_packages(packages2, roads2)); 
     }
 
     public static int minimun_roads_to_collect_packages(int[] packages, int[][] roads) {
         int n = packages.length;
         List<List<Integer>> graph = new ArrayList<>();
         for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
 
         // Build the adjacency list representation of the graph
         for (int[] road : roads) {
             graph.get(road[0]).add(road[1]);
             graph.get(road[1]).add(road[0]);
         }
 
         // Find a starting node (any node with a package)
         int startNode = -1;
         for (int i = 0; i < n; i++) {
             if (packages[i] == 1) {
                 startNode = i;
                 break;
             }
         }
         if (startNode == -1) return 0; // No packages to collect
 
         // BFS to find all nodes within distance 2 that contain packages
         Set<Integer> packageLocations = new HashSet<>();
         Queue<Integer> queue = new LinkedList<>();
         queue.add(startNode);
         Map<Integer, Integer> distance = new HashMap<>();
         distance.put(startNode, 0);
 
         while (!queue.isEmpty()) {
             int node = queue.poll();
             int dist = distance.get(node);
 
             if (dist <= 2 && packages[node] == 1) {
                 packageLocations.add(node);
             }
 
             if (dist < 2) {
                 for (int neighbor : graph.get(node)) {
                     if (!distance.containsKey(neighbor)) {
                         distance.put(neighbor, dist + 1);
                         queue.add(neighbor);
                     }
                 }
             }
         }
 
         // Find the minimum roads required using BFS/DFS
         return find_minimum_roads(graph, startNode, packageLocations);
     }
 
     private static int find_minimum_roads(List<List<Integer>> graph, int start, Set<Integer> targets) {
         // BFS to find the shortest route that covers all package locations
         Queue<int[]> queue = new LinkedList<>();
         queue.add(new int[]{start, 0});
         Set<Integer> visited = new HashSet<>();
         visited.add(start);
 
         int roads = 0;
         int packagesCollected = 0;
 
         while (!queue.isEmpty() && packagesCollected < targets.size()) {
             int[] nodeData = queue.poll();
             int node = nodeData[0];
             int dist = nodeData[1];
 
             if (targets.contains(node)) {
                 packagesCollected++;
                 roads += dist;
                 // Reset distance after collecting
                 queue.clear();
                 visited.clear();
                 queue.add(new int[]{node, 0});
                 visited.add(node);
                 continue;
             }
 
             for (int neighbor : graph.get(node)) {
                 if (!visited.contains(neighbor)) {
                     visited.add(neighbor);
                     queue.add(new int[]{neighbor, dist + 1});
                 }
             }
         }
 
         return roads;
     }
 }
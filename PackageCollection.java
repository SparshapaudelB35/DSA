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

 import java.util.*;

 public class PackageCollection {
     
     public static int minRoadsToCollectPackages(int[] packages, int[][] roads) {
         int n = packages.length;
         
         // Build adjacency list
         List<List<Integer>> graph = new ArrayList<>();
         for (int i = 0; i < n; i++) {
             graph.add(new ArrayList<>());
         }
         
         for (int[] road : roads) {
             graph.get(road[0]).add(road[1]);
             graph.get(road[1]).add(road[0]);
         }
         
         // Find the necessary subtree that contains all packages
         boolean[] visited = new boolean[n];
         int totalRoads = dfs(0, graph, visited, packages);
         
         return totalRoads;
     }
     
     private static int dfs(int node, List<List<Integer>> graph, boolean[] visited, int[] packages) {
         visited[node] = true;
         int totalRoads = 0;
         boolean hasPackage = packages[node] == 1;
         
         for (int neighbor : graph.get(node)) {
             if (!visited[neighbor]) {
                 int roadsFromChild = dfs(neighbor, graph, visited, packages);
                 if (roadsFromChild > 0 || packages[neighbor] == 1) {
                     totalRoads += roadsFromChild + 2;
                     hasPackage = true;
                 }
             }
         }
         
         return hasPackage ? totalRoads : 0;
     }
     
     public static void main(String[] args) {
         int[] packages1 = {1, 0, 0, 0, 0, 1};
         int[][] roads1 = {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}};
         System.out.println(minRoadsToCollectPackages(packages1, roads1)); // Output: 10
         
         int[] packages2 = {0, 0, 0, 1, 1, 0, 0, 1};
         int[][] roads2 = {{0, 1}, {0, 2}, {1, 3}, {1, 4}, {2, 5}, {5, 6}, {5, 7}};
         System.out.println(minRoadsToCollectPackages(packages2, roads2)); // Output: 12
     }
 }
 
/*b) 
You have two points in a 2D plane, represented by the arrays x_coords and y_coords. The goal is to find 
the lexicographically pair i.e. (i, j) of points (one from each array) that are closest to each other. 
Goal: 
Determine the lexicographically pair of points with the smallest distance and smallest distance calculated 
using  
| x_coords [i] - x_coords [j]| + | y_coords [i] - y_coords [j]| 
Note that 
|x| denotes the absolute value of x. 
A pair of indices (i1, j1) is lexicographically smaller than (i2, j2) if i1 < i2 or i1 == i2 and j1 < j2. 
Input: 
x_coords: The array of x-coordinates of the points. 
y_coords: The array of y-coordinates of the points. 
Output: 
The indices of the closest pair of points. 
Input: x_coords = [1, 2, 3, 2, 4], y_coords = [2, 3, 1, 2, 3] 
Output: [0, 3] 
Explanation: Consider index 0 and index 3. The value of | x_coords [i]- x_coords [j]| + | y_coords [i]- 
y_coords [j]| is 1, which is the smallest value we can achieve.  */

public class Q2b {
  // Method to find the lexicographically smallest pair of points with the smallest distance
  public static int[] closestPair(int[] x_coords, int[] y_coords) {
      // Initialize variables to track the minimum distance and the closest pair of points
      int minDistance = Integer.MAX_VALUE;
      int[] closestPair = new int[2]; // To store the indices of the closest pair

      // Loop through each pair of points
      for (int i = 0; i < x_coords.length; i++) {
          for (int j = i + 1; j < x_coords.length; j++) { // j starts from i+1 to avoid duplicate pairs
              // Calculate the Manhattan distance between points (i, j)
              int distance = Math.abs(x_coords[i] - x_coords[j]) + Math.abs(y_coords[i] - y_coords[j]);

              // If the distance is smaller than the current minimum, update the minimum distance
              // Also update the closest pair of points
              if (distance < minDistance) {
                  minDistance = distance;
                  closestPair[0] = i;  // Index of the first point in the pair
                  closestPair[1] = j;  // Index of the second point in the pair
              }
              // If the distance is the same as the current minimum, we check lexicographical order
              else if (distance == minDistance) {
                  // If the pair (i, j) is lexicographically smaller than the current closest pair, update it
                  if (i < closestPair[0] || (i == closestPair[0] && j < closestPair[1])) {
                      closestPair[0] = i;
                      closestPair[1] = j;
                  }
              }
          }
      }

      // Return the closest pair of points
      return closestPair;
  }

  public static void main(String[] args) {
      // Example input
      int[] x_coords = {1, 2, 3, 2, 4};
      int[] y_coords = {2, 3, 1, 2, 3};

      // Call the closestPair method and get the result
      int[] result = closestPair(x_coords, y_coords);

      // Print the output (the indices of the closest pair)
      System.out.println("Closest pair of points: [" + result[0] + ", " + result[1] + "]");
  }
}

/*Question1 
a) 
You have a material with n temperature levels. You know that there exists a critical temperature f where 
0 <= f <= n such that the material will react or change its properties at temperatures higher than f but 
remain unchanged at or below f. 
Rules: 
 You can measure the material's properties at any temperature level once. 
 If the material reacts or changes its properties, you can no longer use it for further measurements. 
 If the material remains unchanged, you can reuse it for further measurements. 
Goal: 
Determine the minimum number of measurements required to find the critical temperature. 
Input: 
 k: The number of identical samples of the material. 
 n: The number of temperature levels. 
Output: 
 The minimum number of measurements required to find the critical temperature. 
Example 1: 
Input: k = 1, n = 2 
Output: 2  */

class Q1a {
  // Function to find the minimum number of measurements required
  public static int minMeasurements(int k, int n) {
      // Create a DP table where dp[i][j] represents the maximum number of temperature levels
      // that can be checked with i samples and j attempts
      int[][] dp = new int[k + 1][n + 1];
      int attempts = 0; // Initialize the number of attempts to zero
      
      // Keep increasing the attempts until we can determine the critical temperature level
      while (dp[k][attempts] < n) {
          attempts++; // Increment the attempt count
          
          // Iterate through all available samples (from 1 to k)
          for (int i = 1; i <= k; i++) {
              // If we drop a sample at a certain temperature:
              // 1. If it breaks, we check below (dp[i-1][attempts-1])
              // 2. If it doesn't break, we check above (dp[i][attempts-1])
              // 3. Add 1 for the current attempt to track the test case
              dp[i][attempts] = dp[i - 1][attempts - 1] + dp[i][attempts - 1] + 1;
          }
      }
      
      // Return the minimum number of attempts required to find the critical temperature
      return attempts;
  }
  
  public static void main(String[] args) {
      // Define an array of test cases
      int[][] testCases = {
          {1, 2},  // Case with 1 sample and 2 temperature levels, expected output: 2
          {2, 6},  // Case with 2 samples and 6 temperature levels, expected output: 3
          {3, 14}  // Case with 3 samples and 14 temperature levels, expected output: 4
      };
      
      // Loop through each test case and execute the function
      for (int[] testCase : testCases) {
          int k = testCase[0]; // Extract number of samples
          int n = testCase[1]; // Extract number of temperature levels
          
          // Print the result for the current test case
          System.out.println("For k = " + k + ", n = " + n + " -> Minimum measurements required: " + minMeasurements(k, n));
      }
  }
}

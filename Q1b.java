/*b)  
You have two sorted arrays of investment returns, returns1 and returns2, and a target number k. You 
want to find the kth lowest combined return that can be achieved by selecting one investment from each 
array. 
Rules: 
 The arrays are sorted in ascending order. 
 You can access any element in the arrays. 
Goal: 
Determine the kth lowest combined return that can be achieved. 
Input: 
 returns1: The first sorted array of investment returns. 
 returns2: The second sorted array of investment returns. 
 k: The target index of the lowest combined return. 
Output: 
 The kth lowest combined return that can be achieved. 
Example 1: 
Input: returns1= [2,5], returns2= [3,4], k = 2 
Output: 8 
Explanation: The 2 smallest investments are  are: - returns1 [0] * returns2 [0] = 2 * 3 = 6 - returns1 [0] * returns2 [1] = 2 * 4 = 8 
The 2nd smallest investment  is 8. 
Example 2: 
Input: returns1= [-4,-2,0,3], returns2= [2,4], k = 6 
Output: 0 
Explanation: The 6 smallest products are: - returns1 [0] * returns2 [1] = (-4) * 4 = -16 - returns1 [0] * returns2 [0] = (-4) * 2 = -8 - returns1 [1] * returns2 [1] = (-2) * 4 = -8 - returns1 [1] * returns2 [0] = (-2) * 2 = -4 - returns1 [2] * returns2 [0] = 0 * 2 = 0 - returns1 [2] * returns2 [1] = 0 * 4 = 0 
The 6th smallest investment is 0. */

import java.util.ArrayList;
import java.util.Collections;

public class Q1b {
    // Method to find the kth lowest combined return
    public static int kthLowestCombinedReturn(int[] returns1, int[] returns2, int k) {
        // Create a list to store the products of pairs
        ArrayList<Integer> products = new ArrayList<>();

        // Loop through each number in returns1
        for (int num1 : returns1) {
            // Loop through each number in returns2
            for (int num2 : returns2) {
                // Multiply the numbers and add the result to the products list
                products.add(num1 * num2);
            }
        }

        // Check if there are enough products
        if (products.size() < k) {
            throw new IllegalArgumentException("k is greater than the total number of products.");
        }

        // Sort the products list to arrange them in increasing order
        Collections.sort(products);

        // Return the k-th smallest product (index k-1 because lists are 0-indexed)
        return products.get(k - 1);
    }

    public static void main(String[] args) {
        // Example 1
        int[] returns1 = {2, 5};
        int[] returns2 = {3, 4};
        int k = 2;
        System.out.println(kthLowestCombinedReturn(returns1, returns2, k));  // Output: 8

        int[] return3 = {-4, -2, 0, 3};
        int[] return4 = {2, 4};
        int k2 = 6;
        System.out.println(kthLowestCombinedReturn(return3, return4, k2)); 

       
    }
}

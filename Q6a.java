/*Question 6 
a) 
You are given a class NumberPrinter with three methods: printZero, printEven, and printOdd. 
These methods are designed to print the numbers 0, even numbers, and odd numbers, respectively. 
Task: 
Create a ThreadController class that coordinates three threads: 
5. ZeroThread: Calls printZero to print 0s. 
6. EvenThread: Calls printEven to print even numbers. 
7. OddThread: Calls printOdd to print odd numbers. 
These threads should work together to print the sequence "0102030405..." up to a specified number n. 
The output should be interleaved, ensuring that the numbers are printed in the correct order. 
Example: 
If n = 5, the output should be "0102030405". 
Constraints: 
 The threads should be synchronized to prevent race conditions and ensure correct output. 
 The NumberPrinter class is already provided and cannot be modified.  */

class NumberPrinter {
  public void printZero() {
      System.out.print(0);
  }

  public void printEven(int num) {
      System.out.print(num);
  }

  public void printOdd(int num) {
      System.out.print(num);
  }
}

class ThreadController {
  private int n;
  private int counter = 0; // Controls the sequence
  private final Object lock = new Object();

  public ThreadController(int n) {
      this.n = n;
  }

  public void zero(NumberPrinter printer) {
      synchronized (lock) {
          for (int i = 1; i <= n; i++) {
              while (counter % 2 != 0) { // Zero should be printed at even indices
                  try {
                      lock.wait();
                  } catch (InterruptedException e) {
                      Thread.currentThread().interrupt();
                  }
              }
              printer.printZero();
              counter++;
              lock.notifyAll();
          }
      }
  }

  public void even(NumberPrinter printer) {
      synchronized (lock) {
          for (int i = 2; i <= n; i += 2) {
              while (counter % 4 != 3) { // Ensures even numbers print after zero
                  try {
                      lock.wait();
                  } catch (InterruptedException e) {
                      Thread.currentThread().interrupt();
                  }
              }
              printer.printEven(i);
              counter++;
              lock.notifyAll();
          }
      }
  }

  public void odd(NumberPrinter printer) {
      synchronized (lock) {
          for (int i = 1; i <= n; i += 2) {
              while (counter % 4 != 1) { // Ensures odd numbers print after zero
                  try {
                      lock.wait();
                  } catch (InterruptedException e) {
                      Thread.currentThread().interrupt();
                  }
              }
              printer.printOdd(i);
              counter++;
              lock.notifyAll();
          }
      }
  }
}

public class Q6a {
  public static void main(String[] args) {
      int n = 5; // Modify as needed
      NumberPrinter printer = new NumberPrinter();
      ThreadController controller = new ThreadController(n);

      Thread zeroThread = new Thread(() -> controller.zero(printer));
      Thread evenThread = new Thread(() -> controller.even(printer));
      Thread oddThread = new Thread(() -> controller.odd(printer));

      zeroThread.start();
      evenThread.start();
      oddThread.start();

      try {
          zeroThread.join();
          evenThread.join();
          oddThread.join();
      } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
      }
  }
}

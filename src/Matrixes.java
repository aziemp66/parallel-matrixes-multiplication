import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.Locale;
import java.text.NumberFormat;

public class Matrixes {
  public static int[][] matrixA = new int[1000][1000];
  public static int[][] matrixB = new int[1000][1000];

  public static int[][] matrixC = new int[800][1200];
  public static int[][] matrixD = new int[1200][1000];

  public static int[] arrayA = new int[20000000];

  // Group Digits with Points
  public static NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

  public static void main(String[] args) throws Exception {
    // Initialize matrices with random values
    for (int i = 0; i < matrixA.length; i++) {
      for (int j = 0; j < matrixA[0].length; j++) {
        matrixA[i][j] = (int) (Math.random() * 100);
        matrixB[i][j] = (int) (Math.random() * 100);
      }
    }

    for (int i = 0; i < matrixC.length; i++) {
      for (int j = 0; j < matrixC[0].length; j++) {
        matrixC[i][j] = (int) (Math.random() * 100);
      }
    }

    for (int i = 0; i < matrixD.length; i++) {
      for (int j = 0; j < matrixD[0].length; j++) {
        matrixD[i][j] = (int) (Math.random() * 100);
      }
    }

    for (int i = 0; i < arrayA.length - 1; i++) {
      arrayA[i] = (int) (Math.random() * 100);
    }
    int target = 77;
    arrayA[arrayA.length - 1] = target;

    Arrays.sort(arrayA);

    // Perform serial and parallel matrix addition
    var serialAddition = serialMatrixesAddition();
    var parallelAddition = parallelMatrixesAddition();

    // Compare results and print status
    var additionStatus = Arrays.deepEquals(serialAddition, parallelAddition) ? "Success" : "Failed";
    System.out.println("Matrixes Addition: " + additionStatus);

    var serialMultiplication = serialMatrixesMultiplication();
    var parallelMultiplication = parallelMatrixesMultiplication();

    var multiplicationStatus = Arrays.deepEquals(serialMultiplication, parallelMultiplication) ? "Success" : "Failed";
    System.out.println("Matrixes Multiplication: " + multiplicationStatus);

    parallelBinarySearch(target);
  }

  // Serial Matrix Addition
  private static int[][] serialMatrixesAddition() {
    int[][] resultMatrix = new int[matrixA.length][matrixA[0].length];
    long start = System.currentTimeMillis();

    for (int i = 0; i < resultMatrix.length; i++) {
      for (int j = 0; j < resultMatrix[0].length; j++) {
        resultMatrix[i][j] = matrixA[i][j] + matrixB[i][j];
        for (int k = 0; k < 1000; k++) {
          // Adding workload
        }
      }
    }

    long end = System.currentTimeMillis();

    String formattedTime = numberFormat.format(end - start); // Format the time with commas
    System.out.println("Time Spent in Serial Matrix Addition: " + formattedTime + "ms");

    return resultMatrix;
  }

  // Serial Matrix Multiplication
  private static int[][] serialMatrixesMultiplication() {
    int[][] resultMatrix = new int[matrixC.length][matrixD[0].length];
    long start = System.currentTimeMillis();

    // Multiply the matrices
    for (int i = 0; i < matrixC.length; i++) {
      for (int j = 0; j < matrixD[0].length; j++) {
        for (int k = 0; k < matrixC[0].length; k++) {
          resultMatrix[i][j] += matrixC[i][k] * matrixD[k][j];
        }
      }
    }

    long end = System.currentTimeMillis();

    String formattedTime = numberFormat.format(end - start); // Format the time with commas
    System.out.println("Time Spent in Serial Matrix Multiplication: " + formattedTime + "ms");

    return resultMatrix;
  }

  // Parallel Matrix Addition using ForkJoinPool
  private static int[][] parallelMatrixesAddition() {
    ForkJoinPool pool = ForkJoinPool.commonPool();
    int[][] resultMatrix = new int[matrixA.length][matrixA[0].length];
    MatrixesAdditionTask task = new MatrixesAdditionTask(resultMatrix, 0, matrixA.length);

    long start = System.currentTimeMillis();
    pool.invoke(task);
    long end = System.currentTimeMillis();

    String formattedTime = numberFormat.format(end - start); // Format the time with commas
    System.out.println("Time Spent in Parallel Matrix Addition: " + formattedTime + "ms");

    return resultMatrix;
  }

  // Parallel Matrix Multiplication using ForkJoinPool
  private static int[][] parallelMatrixesMultiplication() {
    ForkJoinPool pool = ForkJoinPool.commonPool();
    int[][] resultMatrix = new int[matrixC.length][matrixD[0].length];
    MatrixesMultiplicationTask task = new MatrixesMultiplicationTask(resultMatrix, 0, matrixC.length);

    long start = System.currentTimeMillis();
    pool.invoke(task);
    long end = System.currentTimeMillis();

    String formattedTime = numberFormat.format(end - start); // Format the time with commas
    System.out.println("Time Spent in Parallel Matrix Multiplication : " + formattedTime + "ms");
    return resultMatrix;
  }

  private static void parallelBinarySearch(int target) {
    ForkJoinPool pool = ForkJoinPool.commonPool();
    var binarySearchTask = new BinarySearchTask(arrayA, target, 0, arrayA.length);

    long start = System.nanoTime();
    int result = pool.invoke(binarySearchTask);
    long end = System.nanoTime();

    String formattedTime = numberFormat.format(end - start); // Format the time with commas

    switch (result) {
      case -1:
        System.err.println("Element Not Found");
        break;
      default:
        System.out.println("Element Found at Index : " + result);
        System.out.println("Array Index of " + numberFormat.format(result) + " is : " + arrayA[result]);
        System.out.println("Time Spent in Parallel Binary Search : " + formattedTime + " nano second");
        break;
    }
  }

  // Task for parallel matrix addition
  static class MatrixesAdditionTask extends RecursiveTask<Void> {
    private static final int THRESHOLD = 100; // Threshold for task splitting
    private int[][] resultMatrix;
    private int startRow, endRow;

    public MatrixesAdditionTask(int[][] resultMatrix, int startRow, int endRow) {
      this.resultMatrix = resultMatrix;
      this.startRow = startRow;
      this.endRow = endRow;
    }

    @Override
    protected Void compute() {
      if (endRow - startRow <= THRESHOLD) {
        for (int i = startRow; i < endRow; i++) {
          for (int j = 0; j < matrixA[0].length; j++) {
            resultMatrix[i][j] = matrixA[i][j] + matrixB[i][j];
            for (int k = 0; k < 1000; k++) {
              // Simulating workload
            }
          }
        }
      } else {
        int mid = (startRow + endRow) / 2;
        MatrixesAdditionTask task1 = new MatrixesAdditionTask(resultMatrix, startRow, mid);
        MatrixesAdditionTask task2 = new MatrixesAdditionTask(resultMatrix, mid, endRow);
        invokeAll(task1, task2);
      }
      return null;
    }
  }

  // Task for parallel matrix multiplication
  static class MatrixesMultiplicationTask extends RecursiveTask<Void> {
    private static final int ROW_THRESHOLD = 100;
    private static final int COLUMN_THRESHOLD = 100;

    private int[][] resultMatrix;
    private int startRow, endRow;

    public MatrixesMultiplicationTask(int[][] resultMatrix, int startRow, int endRow) {
      this.resultMatrix = resultMatrix;
      this.startRow = startRow;
      this.endRow = endRow;
    }

    @Override
    protected Void compute() {
      // If the row range is smaller than threshold, split the columns further
      if (endRow - startRow <= ROW_THRESHOLD) {
        for (int i = startRow; i < endRow; i++) {
          ColumnMultiplicationTask columnTask = new ColumnMultiplicationTask(resultMatrix, i, 0, matrixD[0].length);
          columnTask.fork(); // Fork and execute column task
          columnTask.join(); // Join the result after task is completed
        }
      } else {
        // Split the task row-wise
        int mid = (startRow + endRow) / 2;
        MatrixesMultiplicationTask task1 = new MatrixesMultiplicationTask(resultMatrix, startRow, mid);
        MatrixesMultiplicationTask task2 = new MatrixesMultiplicationTask(resultMatrix, mid, endRow);
        invokeAll(task1, task2);
      }
      return null;
    }

    // Nested RecursiveTask for column-wise parallelism
    private static class ColumnMultiplicationTask extends RecursiveTask<Void> {
      private int[][] resultMatrix;
      private int row;
      private int startCol, endCol;

      public ColumnMultiplicationTask(int[][] resultMatrix, int row, int startCol, int endCol) {
        this.resultMatrix = resultMatrix;
        this.row = row;
        this.startCol = startCol;
        this.endCol = endCol;
      }

      @Override
      protected Void compute() {
        if (endCol - startCol <= COLUMN_THRESHOLD) {
          for (int j = startCol; j < endCol; j++) {
            for (int k = 0; k < matrixC[0].length; k++) {
              resultMatrix[row][j] += matrixC[row][k] * matrixD[k][j];
            }
          }
        } else {
          // Split the task column-wise
          int mid = (startCol + endCol) / 2;
          ColumnMultiplicationTask task1 = new ColumnMultiplicationTask(resultMatrix, row, startCol, mid);
          ColumnMultiplicationTask task2 = new ColumnMultiplicationTask(resultMatrix, row, mid, endCol);
          invokeAll(task1, task2);
        }
        return null;
      }
    }
  }

  static class BinarySearchTask extends RecursiveTask<Integer> {
    private final int[] array;
    private final int target;
    private final int start;
    private final int end;

    public BinarySearchTask(int[] array, int target, int start, int end) {
      this.array = array;
      this.target = target;
      this.start = start;
      this.end = end;
    }

    @Override
    protected Integer compute() {
      // Base condition: if the search range is invalid
      if (start > end) {
        return -1; // Element not found
      }

      int mid = (start + end) / 2;

      if (array[mid] == target) {
        return mid; // Target found
      } else if (array[mid] > target) {
        // Search the left half
        BinarySearchTask leftTask = new BinarySearchTask(array, target, start, mid - 1);
        leftTask.fork(); // Fork the left task
        return leftTask.join(); // Wait for the result
      } else {
        // Search the right half
        BinarySearchTask rightTask = new BinarySearchTask(array, target, mid + 1, end);
        rightTask.fork(); // Fork the right task
        return rightTask.join(); // Wait for the result
      }
    }
  }

}

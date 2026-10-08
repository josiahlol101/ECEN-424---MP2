import java.util.Arrays; //this will be used to compare the two matrices at the end
import java.util.Random; //used to generate random numbers for the matrices

public class MatrixMultiplication {
    //below, define values that'll be used throughout the code
    private static final int SIZE = 20; //Each matrix is 20x20
    private static final int THREAD_COUNT = 5; //5 worker threads created
    private static final int ROWS_PER_THREAD = SIZE / THREAD_COUNT; //each thread handles 4 rows

    // Each thread calculates four rows of the result matrix
    static class MatrixWorker extends Thread {
        private final int[][] matrixA; //first matrix to be multiplied
        private final int[][] matrixB; //2nd matrix
        private final int[][] result; //where calculations are stored
        private final int startRow; //the first row thread will calculate
        private final int endRow; //where thread stops (not included)

        //give each worker the matrices and the rows it's responsible for
        MatrixWorker(int[][] matrixA, int[][] matrixB, int[][] result,
                     int startRow, int endRow) {
            this.matrixA = matrixA;
            this.matrixB = matrixB;
            this.result = result;
            this.startRow = startRow;
            this.endRow = endRow;
        }

        //this runs when the thread starts, calculating its assigned rows
        @Override
        public void run() {
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < SIZE; j++) {
                    int sum = 0;

                    //multiply row i of A by column j of B
                    for (int k = 0; k < SIZE; k++) {
                        sum += matrixA[i][k] * matrixB[k][j];
                    }

                    result[i][j] = sum; //store the calculated value
                }
            }
        }
    }

    //fill the matrices with random integers from 0 to 9
    private static void fillRandom(int[][] matrix, Random random) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                matrix[i][j] = random.nextInt(10);
            }
        }
    }

    //normal matrix multiplication without using extra threads
    //we'll use this later to check if our multithreaded result is correct
    private static int[][] multiplyNormally(int[][] matrixA,
                                             int[][] matrixB) {
        int[][] result = new int[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int sum = 0;

                for (int k = 0; k < SIZE; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }

                result[i][j] = sum;
            }
        }

        return result;
    }

    //prints each row of the matrix, with spacing to make it readable
    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.printf("%5d", value);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws InterruptedException {

        //create the two input matrices and one for our result
        int[][] matrixA = new int[SIZE][SIZE];
        int[][] matrixB = new int[SIZE][SIZE];
        int[][] threadedResult = new int[SIZE][SIZE];

        //using 42 as a seed so we get the same random values each time
        Random random = new Random(42);

        fillRandom(matrixA, random);
        fillRandom(matrixB, random);

        System.out.println("Matrix A:");
        printMatrix(matrixA);

        System.out.println("\nMatrix B:");
        printMatrix(matrixB);

        //create an array to hold our 5 worker threads
        MatrixWorker[] workers = new MatrixWorker[THREAD_COUNT];

        for (int t = 0; t < THREAD_COUNT; t++) {
            //figure out which 4 rows each thread will work on
            int startRow = t * ROWS_PER_THREAD;
            int endRow = startRow + ROWS_PER_THREAD;

            workers[t] = new MatrixWorker(
                    matrixA, matrixB, threadedResult, startRow, endRow);

            workers[t].setName("Thread " + (t + 1));
            workers[t].start(); //start the thread so it can begin its calculations

            System.out.println(workers[t].getName()
                    + " assigned rows " + (startRow + 1)
                    + " through " + endRow);
        }

        //wait for all 5 threads to finish before printing the result
        for (MatrixWorker worker : workers) {
            worker.join();
        }

        System.out.println("\nMultithreaded product matrix (A x B):");
        printMatrix(threadedResult);

        //calculate the same product normally so we can compare results
        int[][] normalResult = multiplyNormally(matrixA, matrixB);

        //check if every element matches between the two matrices
        if (Arrays.deepEquals(threadedResult, normalResult)) {
            System.out.println(
                "\nVerification PASSED: Multithreaded and normal results match.");
        } else {
            System.out.println(
                "\nVerification FAILED: Results do not match.");
        }
    }
}

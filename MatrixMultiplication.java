import java.util.Arrays; //used for comparing matrices at end
import java.util.Random; //used for randmom number generation

//defining multiplication class
public class MatrixMultiplication {

    //constants used throughout program(ie.final)
    private static final int SIZE = 20; //Each matrix is 20x20
    private static final int THREAD_COUNT = 5; //5 total worker threads
    private static final int ROWS_PER_THREAD = SIZE / THREAD_COUNT; //4 rows per thread

    // Each thread calculates four rows of final matrix
    static class MatrixWorker extends Thread {
        //working with [][] --- 2d arrays
        private final int[][] matrixA; //first matrix
        private final int[][] matrixB; //2nd matrix
        private final int[][] result; 
        private final int startRow; //the first row thread calculates
        private final int endRow; //where thread stops

        //Define function that gives each thread access to matrices and corresponding rows
        MatrixWorker(int[][] matrixA, int[][] matrixB, int[][] result,
                     int startRow, int endRow) {
            this.matrixA = matrixA;
            this.matrixB = matrixB;
            this.result = result;
            this.startRow = startRow;
            this.endRow = endRow;
        }

        //this function runs the multiplication for each thread
        @Override //override replaces the threads built-in run method with our method
        public void run() {
            //goes through each row
            for (int i = startRow; i < endRow; i++) {
                // goes through each column
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

    //goes through every cell and fills with random values from 0-9
    private static void fillRandom(int[][] matrix, Random random) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                matrix[i][j] = random.nextInt(10); // 10 not included
            }
        }
    }

    //normal matrix multiplication without using extra threads
    private static int[][] multiplyNormally(int[][] matrixA,
                                             int[][] matrixB) {
        int[][] result = new int[SIZE][SIZE];//create array to store matrix

        //going through rows
        for (int i = 0; i < SIZE; i++) {//iterate through matrix
            //going through columns
            for (int j = 0; j < SIZE; j++) {
                int sum = 0;

                for (int k = 0; k < SIZE; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];//add multiplied values in variable sum
                }

                result[i][j] = sum;//store sum in result matrix 
            }
        }

        return result;//return resulting matrix
    }

    //prints each row of the matrix, with spacing to make it readable
    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.printf("%5d", value); //padded by 5 characters for spacing
            }
            System.out.println();
        }
    }

    //Here is out Main fucntion, where multiplication will take place
    public static void main(String[] args) throws InterruptedException {

        //create the two input matrices and one for our result
        int[][] matrixA = new int[SIZE][SIZE];
        int[][] matrixB = new int[SIZE][SIZE];
        int[][] threadedResult = new int[SIZE][SIZE];

        //using 42 as a seed for reproducibility
        Random random = new Random(42);

        //fill matrices w random numbers
        fillRandom(matrixA, random);
        fillRandom(matrixB, random);

        System.out.println("Matrix A:");
        printMatrix(matrixA);

        System.out.println("\nMatrix B:");
        printMatrix(matrixB);

        //create an array to hold the 5 worker threads
        MatrixWorker[] workers = new MatrixWorker[THREAD_COUNT];

        for (int t = 0; t < THREAD_COUNT; t++) {
            //define which 4 rows each thread will work on
            int startRow = t * ROWS_PER_THREAD; // start row for each thread
            int endRow = startRow + ROWS_PER_THREAD; //end row for each thread

            //defining all of the worker threads...AND starting them
            workers[t] = new MatrixWorker( //creates a new MatrixWorker object and stores it in workers array.
                    matrixA, matrixB, threadedResult, startRow, endRow);

            workers[t].setName("Thread " + (t + 1));//Name threads
            workers[t].start(); //start the thread so it can begin its calculations

            //displays which rows each frame was assigned
            System.out.println(workers[t].getName()
                    + " assigned rows " + (startRow + 1)
                    + " through " + endRow);
        }

        //wait for all 5 threads to finish before printing the result
        for (MatrixWorker worker : workers) {
            worker.join(); //join -- pauses main thread until worker threads are finished
        }

        System.out.println("\nMultithreaded product matrix (A x B):");
        printMatrix(threadedResult);

        //calculate the same product normally so we can compare results
        int[][] normalResult = multiplyNormally(matrixA, matrixB);
        System.out.println("\nNormal (single-threaded) product matrix (A x B):");
        printMatrix(normalResult);
        //check if every element matches between the two matrices
        if (Arrays.deepEquals(threadedResult, normalResult)) {//Pass case
            System.out.println(
                "\nVerification PASSED: Multithreaded and normal results match.");
        } else {//fail case
            System.out.println(
                "\nVerification FAILED: Results do not match.");
        }
    }
}

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final int N = 2000;

    private static final int[][] A = new int[N][N]; // input array
    private static final int[][] B = new int[N][N]; // input array
    private static final int[][] C = new int[N][N]; // output array


    public static void main(String[] args) throws InterruptedException {
        initMatrixes();
//        printMatrix(A);
//        System.out.println("");
//        printMatrix(B);

        long start = System.nanoTime();

//        multiplySerial();
        // Serial, 2K x 2K
        // Run 1 -> 68.91 seconds
        // Run 2 -> 68.53 seconds
        // Run 3 -> 70.86 seconds

        multiplyParallel();
        // Parallel, 2K x 2K (4 threads)
        // Run 1 -> 30.00 seconds
        // Run 2 -> 33.13 seconds
        // Run 3 -> 36.30 seconds

        long end = System.nanoTime();

        System.out.println("Execution time = " + (end - start));

        checkResult();
//        printMatrix(C);
    }

    static void multiplyParallel() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(4);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                service.submit(new MatrixMultiplyTask(i, j));
            }
        }

        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);
    }

    static class MatrixMultiplyTask implements Runnable {

        private final int line;
        private final int col;

        public MatrixMultiplyTask(int line, int col) {
            this.line = line;
            this.col = col;
        }

        @Override
        public void run() {
            C[line][col] = 0;
            for (int k = 0; k < N; k++) {
                C[line][col] += A[line][k] * B[k][col];
            }
        }
    }

    static void multiplySerial() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                C[i][j] = 0;
                for (int k = 0; k < N; k++) {
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
    }

    static void initMatrixes() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = 1;
                B[i][j] = 1;
            }
        }
    }

    static void checkResult() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (C[i][j] != N) {
                    System.out.println("Incorrect!");
                }
            }
        }
        System.out.println("Correct!");
    }

    static void printMatrix(int[][] matrix) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}

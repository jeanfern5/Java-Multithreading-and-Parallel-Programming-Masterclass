import java.util.Arrays;
import java.util.concurrent.*;

public class Main {

    private static int[] array = new int[] {1, 2, 3, 4, 5, 6, 7, 8};

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Useful for "divide and conquer" problems or tasks that can be executed in parallel
        // it allows work stealing where idle threads can steal work from busy ones to maximize efficiency
        // Creates threads based on available CPU
        ForkJoinPool pool = new ForkJoinPool(2); // very hungry threads, if no work, it'll find it
//        pool.getStealCount(); // threads can steal work from each other
        Future<?> future = pool.submit(new IncrementTask(0, 8)); // same as ThreadPoolExecutor
//        pool.execute(); // same as ThreadPoolExecutor

        future.get();

        System.out.println("The array is: " + Arrays.toString(array));
    }

    static class IncrementTask extends RecursiveAction {
        private final int left;
        private final int right;

        public IncrementTask(int left, int right) {
            this.left = left;
            this.right = right;
        }

        @Override
        protected void compute() {
            if (right - left < 3) {
                for (int i = left; i < right; i ++ ) {
                    array[i]++;
                }
            } else {
                int mid = (left + right) / 2;
                invokeAll(new IncrementTask(left, mid), new IncrementTask(mid, right)); // will execute tasks in parallel
            }
        }
    }
}

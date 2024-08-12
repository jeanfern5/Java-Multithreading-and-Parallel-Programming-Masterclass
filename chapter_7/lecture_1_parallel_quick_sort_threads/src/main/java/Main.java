import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class Main {

    private static Random random = new Random();
    private static int arraySize = 100000000;
    private static int[] array = new int[arraySize];

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        for (int i = 0; i < arraySize; i++) {
            array[i] = random.nextInt(arraySize);
        }

//        System.out.println(Arrays.toString(array));

        long start = System.nanoTime();

        quickSort(0, arraySize - 1);
        // Classical recursive execution
        // Run 1 -> 13.223 seconds
        // Run 2 -> 12.260 seconds
        // Run 3 -> 12.364 seconds
        // Run 4 -> 14.279 seconds
        // Run 5 -> 12.459 seconds

//        ForkJoinPool forkJoinPool = new ForkJoinPool();
//        ForkJoinTask<Void> future = forkJoinPool.submit(new QuickSortTask(0, arraySize - 1));
//        future.get();
        // Fork Join execution
        // Run 1 -> 3.151 seconds
        // Run 2 -> 2.857 seconds
        // Run 3 -> 2.858 seconds
        // Run 4 -> 2.768 seconds
        // Run 5 -> 3.509 seconds

        long end = System.nanoTime();

        System.out.println("Execution time = " + (end - start));
//        System.out.println(Arrays.toString(array));
    }

    static class QuickSortTask extends RecursiveAction {
        private final int left;
        private final int right;

        public QuickSortTask(int left, int right) {
            this.left = left;
            this.right = right;
        }

        @Override
        protected void compute() {
            if (left < right) {
                int pivotIndex = partition(left, right);

                // Fork Join
//                invokeAll(new QuickSortTask(left, pivotIndex - 1),
//                        new QuickSortTask(pivotIndex + 1, right));

                // Classical recursive
                quickSort(left, pivotIndex - 1);
                quickSort(pivotIndex + 1, right);
            }
        }

    }

    static void quickSort(int left, int right) {
        if (left < right) {
            int pivotIndex = partition(left, right);

            quickSort(left, pivotIndex - 1);
            quickSort(pivotIndex + 1, right);
        }
    }

    static int partition(int left, int right) {
        int pivot = array[right];
        int swapIndex = left - 1;

        for (int i = left; i < right; i ++) {
            if (array[i] < pivot) {
                swapIndex++;
                swap(swapIndex, i);
            }
        }

        swapIndex++;
        swap(swapIndex, right);
        return swapIndex;
    }

    static void swap(int leftIndex, int rightIndex) {
        int temp = array[leftIndex];
        array[leftIndex] = array[rightIndex];
        array[rightIndex] = temp;
    }

}
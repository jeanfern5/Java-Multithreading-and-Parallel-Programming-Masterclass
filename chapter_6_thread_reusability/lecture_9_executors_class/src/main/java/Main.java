import java.util.Arrays;
import java.util.concurrent.*;

public class Main {
    // Executors - utility class that allows threads to be created with 1 line of code
    // captures common thread creations
    public static void main(String[] args) {
        // a lot of the thread pools done in previous lectures are available in the "Executors" utility class
        ExecutorService executorService = Executors.newFixedThreadPool(5); // creates instance with unbounded queue (non-resizable), uses "LinkedBlockingQueue"
        ExecutorService executorService1 = Executors.newCachedThreadPool(new MyThreadFactory()); // handoff mechanism, if no worker available it will create new threads up to max value (~2 billion), uses "SynchronousQueue"
        ExecutorService executorService2 = Executors.newWorkStealingPool(); // work stealing pool, uses "ForkJoinPool"
        ExecutorService executorService3 = Executors.newSingleThreadExecutor();
        ExecutorService executorService4 = Executors.newSingleThreadScheduledExecutor();
    }

    static class MyThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);

            t.setPriority(4);
            t.setName("my-thread");

            return t;
        }
    }

}

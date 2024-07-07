import java.util.concurrent.*;

public class Main {
    // Lecture 5: Managing Rejected Tasks in ThreadPool - related to "RejectedExecutionHandler"
    // Lecture 6: Monitoring the Performance of a ThreadPool - related to metrics
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                2,
                3,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(2),
                (Runnable r, ThreadPoolExecutor executer) -> { // if RejectedExecutionHandler used, the exception won't be thrown anymore
                    // push some metric to system
                    System.out.println("Task rejected");
                }
//                new ThreadPoolExecutor.CallerRunsPolicy() // retry on the caller thread
//                new ThreadPoolExecutor.DiscardOldestPolicy() // makes room in the queue for new task to be retried
//                new ThreadPoolExecutor.DiscardPolicy() // does nothing - task will get discarded
        );
////        Lecture 6: Monitoring the Performance of a ThreadPool
        threadPoolExecutor.getPoolSize(); // returns the number of threads currently used
        threadPoolExecutor.getActiveCount(); // return number of threads are executing tasks
        threadPoolExecutor.getTaskCount(); // correlates to the queue size
        threadPoolExecutor.getCompletedTaskCount(); // calculate completed tasks
        // should add metric to rejected tasks


////        Lecture 5: Managing Rejected Tasks in ThreadPool - related to "RejectedExecutionHandler"
//        threadPoolExecutor.submit(new SleepingTask(1));
//        threadPoolExecutor.submit(new SleepingTask(2));
//
//        System.out.println("[1] Pool size: " + threadPoolExecutor.getPoolSize());
//
//        threadPoolExecutor.submit(new SleepingTask(3));
//        threadPoolExecutor.submit(new SleepingTask(4));
//        threadPoolExecutor.submit(new SleepingTask(5));
//
//        System.out.println("[2] Pool size: " + threadPoolExecutor.getPoolSize());
//
//        threadPoolExecutor.submit(new SleepingTask(6)); // expected to be rejected since the max pool size is 3
//
//        System.out.println("[3] Pool size: " + threadPoolExecutor.getPoolSize());
    }

    static class SleepingTask implements Runnable {
        private final int id;

        public SleepingTask(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(99999);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

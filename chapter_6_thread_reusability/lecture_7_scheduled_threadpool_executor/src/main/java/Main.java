import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        // Extends ThreadPoolExecutor which can be schedule
        // starts with an initial capacity but can increase if it needs more - it could reach OOM
        ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(3);

//        threadPoolExecutor.schedule(() -> System.out.println("Task"), 5, TimeUnit.SECONDS);
//        ScheduledFuture <?> future = threadPoolExecutor.scheduleAtFixedRate(() -> System.out.println("Task"), 5, 1, TimeUnit.SECONDS); // initial delay then it executes at every period - good if you need to schedule task such as polling or pushing metrics/job
//        threadPoolExecutor.setRemoveOnCancelPolicy(true); // will remove cancelled tasks from the queue
//        future.cancel(true); // cancelled task are not removed from queue and will remain taking up memory
        threadPoolExecutor.shutdown(); // graceful shutdown
        threadPoolExecutor.shutdownNow(); // forces shutdown quickly
        threadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(true); // currently delays tasks will be executed even after shutdown
        threadPoolExecutor.setContinueExistingPeriodicTasksAfterShutdownPolicy(true); // similar to above but to be used with "scheduleAtFixedRate"

    }
}

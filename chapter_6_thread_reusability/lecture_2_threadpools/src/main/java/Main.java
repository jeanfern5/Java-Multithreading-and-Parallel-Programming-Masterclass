import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new CustomThreadPoolExecuter(
                2,
                2,
                1,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(2)
                // Types of Work Queues:
                // bounded: new ArrayBlockingQueue<>(3) - has limited capacity and default access policy
                // unbounded: new LinkedBlockingQueue<>() - not used often due to memory impact where queue can get full and lead to OOM
                // synchronous: new SynchronousQueue<>() - has nonfair access policy therefore it doesn't store any tasks instead it hands-off tasks
        );
// //  Lecture 4: Handling Exception
// //        approach 1: within the task - best way since it's better to keep closer to the tasks, makes it easier for debugging
//        threadPoolExecutor.submit(() -> {
//            try {
//                throw new RuntimeException();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
// //        approach 2: as a future
//        Future<?> future = threadPoolExecutor.submit(() -> {
//            throw new RuntimeException("test exception");
//        });
//        try {
//            future.get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
// //       approach 3: with custom executer  - see  CustomThreadPoolExecuter - not the best way but good to know since it can be in legacy code
//        Future<?> future = threadPoolExecutor.submit(() -> {
//            throw new RuntimeException("test exception");
//        });

// //  Lecture 3: Work Queues
//        threadPoolExecutor.submit(() -> {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        System.out.println("Task 1");
//
//        threadPoolExecutor.submit(() -> {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        System.out.println("Task 2");
//
//        // Expected to be rejected since the thread limit is 2
//        threadPoolExecutor.submit(() -> {
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        System.out.println("Task 3");

// // Lecture 2: Getting familiarized with ThreadPoolExecutor
//        threadPoolExecutor.prestartAllCoreThreads(); // creates all corePoolSize threads
//        System.out.println("Pool size: " + threadPoolExecutor.getPoolSize());

//        // Methods to close the application
//        threadPoolExecutor.shutdown(); // graceful way to close thread pool by stop accepting new tasks into the queue and wait to complete existing tasks
////        threadPoolExecutor.shutdownNow(); // stops actively executing tasks and return tasks that were waiting in to be executed
//        threadPoolExecutor.awaitTermination(3, TimeUnit.SECONDS); // blocks until all tasks have completed execution after a shutdown request, timeout occurs, or thread is interrupted - whichever happens first


//        Future<Integer> future = threadPoolExecutor.submit(new CallableTask()); // submits tasks to the ThreadPoolExecutor
//        // Do other stuff
//        Integer result = future.get(); // checks if result is available - if yes, returns result - if not, waits for results and then returns it
    }

    ////       approach 3: with custom executer
    static class CustomThreadPoolExecuter extends  ThreadPoolExecutor {

        public CustomThreadPoolExecuter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        public CustomThreadPoolExecuter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        public CustomThreadPoolExecuter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        }

        public CustomThreadPoolExecuter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        @Override
        protected  void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);

            if (t != null) {
                System.out.println("CustomThreadPoolExecuter.afterExecuter.t = " + t);
            }
        }
    }

    static class CallableTask implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            // Do some work
            return 4;
        }
    }
}

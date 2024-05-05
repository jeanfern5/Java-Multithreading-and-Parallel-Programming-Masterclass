import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    // similar to a lock but more complex
    private static Semaphore semaphore = new Semaphore(2);

//    public static void main(String[] args) throws InterruptedException {
//        // you can set negative permits which will require release to be called until >=1 is available
//         Semaphore semaphore = new Semaphore(5);
//         Lock lock = new ReentrantLock();
//
//         // only one thread can execute critical section
//         lock.lock();
//         // critical section
//         lock.unlock();
//
//        // multiple threads can execute critical section based on the permits allotted
//        semaphore.acquire(); // lock
//        // critical section
//        semaphore.release(); // unlock
//    }

    public static void main(String[] args) throws InterruptedException {
        Executor executor = new Executor();

        executor.submit(new Job(5000));
        executor.submit(new Job(10000));
        executor.submit(new Job(1000));
    }

    static class Executor {
        public void submit(Job job) throws InterruptedException {
            System.out.println("Launching job: " + job.getWork());
            semaphore.acquire();

            Thread t = new Thread(() -> {
                try {
                    System.out.println("Executing job: " + job.getWork());
                    Thread.sleep(job.getWork());

                    semaphore.release();
                    System.out.println("Job finished with id: " + job.getWork());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            t.start();
        }
    }

    static class Job {
        private final int work;

        public Job (int work) {
            this.work = work;
        }

        public int getWork() {
            return work;
        }
    }
}

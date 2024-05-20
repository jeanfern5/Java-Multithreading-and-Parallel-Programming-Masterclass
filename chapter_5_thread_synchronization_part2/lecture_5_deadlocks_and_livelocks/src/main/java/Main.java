import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
//    public static void main(String[] args) throws InterruptedException {
//        Thread.currentThread().join(); // will not exit (ie deadlock) - best to use join w/ timeout
//    }
    private static Lock lock1 = new ReentrantLock();
    private static Lock lock2 = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        // acquires lock1, but lock2 is taken by thread 2
        Thread t1 = new Thread(() -> {
            lock1.lock(); // tryLock() w/ specific timeout to interrupt deadlocks
            System.out.println("Thread 1 acquired lock1");

            lock2.lock();
            System.out.println("Thread 1 acquired lock2");
            lock2.unlock();

            lock1.unlock();
        });

        // acquires lock2, but lock1 is taken by thread 1 -> leads to deadlock since both threads are waiting for each other to release resources which keeps thread live and "stuck"
        // `deadlocks` threads are blocked due to circular dependency
        // can try to avoid with timeouts and process terminations
        // if locks were set exactly as above -> no deadlock would be encountered
        // `livelocks` threads are not able to make progress (not blocked) because they respond to each other but don't complete a task
        // harder to detect because threads are active and consuming CPU resources
        Thread t2 = new Thread(() -> {
            lock2.lock();
            System.out.println("Thread 2 acquired lock2");

            lock1.lock();
            System.out.println("Thread 2 acquired lock1");
            lock1.unlock();

            lock2.unlock();
        });

        t1.start();
        t2.start();
    }
}

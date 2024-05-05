import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static Lock readLock = readWriteLock.readLock();
    private static Lock writeLock = readWriteLock.writeLock(); // write locks have priority
    private static List<Integer> list = new ArrayList<>();

//    public static void main(String[] args) {
//        Lock lock = new ReentrantLock();
//        lock.lock(); // remember to call unlock for each lock (useful to use with try/catch/finally)
//        // try to keep critical sections small as possible
//        lock.unlock(); // can be used for read or write
//    }
    public static void main(String[] args) {
        Thread writer = new Thread(new WriterThread());

        Thread reader1 = new Thread(new ReaderThread());
        Thread reader2 = new Thread(new ReaderThread());
        Thread reader3 = new Thread(new ReaderThread());

        writer.start();
        reader1.start();
        reader2.start();
        reader3.start();
    }

    static class WriterThread implements Runnable {
        @Override
        public void run() {
            while(true) {
                try {
                    writeData();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void writeData() throws InterruptedException {
            Thread.sleep(10000);

            writeLock.lock();

            // critical section
            int value = (int) (Math.random() * 10);
            System.out.println("Producing data: " + value);

            Thread.sleep(3000);

            list.add(value);

            writeLock.unlock();
        }
    }

    static class ReaderThread implements Runnable {
        @Override
        public void run() {
            while(true) {
                try {
                    readData();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        private void readData() throws InterruptedException {
            Thread.sleep(3000);

//            readLock.lock(); - without the "spin lock", it keeps the lock in the "waiting state"

            // "spin lock" - will keep the lock in "runnable state" until the lock is available
            // useful when blocking is undesirable such as OS Kernels
            while (true) {
                boolean acquired = readLock.tryLock();
                if (acquired) {
                    break;
                } else {
                    System.out.println("Waiting for read lock...");
                }
            }

            // critical section
            System.out.println("List is: " + list);

            readLock.unlock();
        }
    }



}

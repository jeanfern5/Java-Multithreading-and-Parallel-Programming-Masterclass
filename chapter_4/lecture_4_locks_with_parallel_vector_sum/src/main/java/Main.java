import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static int S = 0;
    private static int[] array = new int[10];
    private static ReentrantLock lockObject = new ReentrantLock();

//    public static void main(String[] args) {
//        Lock lockObject = new ReentrantLock();
//
////        synchronized (new Object()) {
////        }
//
//        lockObject.tryLock(); // non-blocking, returns boolean if the lock is released or not
//
//        lockObject.lock();
//        lockObject.lock();
//        lockObject.lock();
//
//        // Thread 1 is holding the lock
//        System.out.println("Instruction");
//
//        lockObject.unlock();
//
////        try {
////        } catch (Exception ignored) {
////        } finally {
////            lockObject.unlock();
////        }
//    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            array[i] = 10;
        }

        List<Thread> threadList = new ArrayList<>();

        int threadSlice = array.length / 2;

        // will create and execute 2 threads
        for (int i = 0; i < 2; i++) {
            Thread t = new Thread(new WorkerThread(i * threadSlice, (i + 1) * threadSlice));
            t.start();
            threadList.add(t);
        }

        threadList.forEach(t -> {
            try {
                t.join();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("The sum is: " + S);
    }

    static class WorkerThread implements Runnable {
        private final int left;
        private final int right;
        public WorkerThread(int left, int right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public void run() {
            for (int i = left; i < right; i++) {
                lockObject.lock();
                S = S + array[i];
                lockObject.unlock();
            }
        }
    }

}

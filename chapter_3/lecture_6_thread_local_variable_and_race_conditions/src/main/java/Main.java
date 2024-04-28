// Chapter 3 Lecture 6: Thread Local Variables and Race Conditions

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static int globalCounter = 0;

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();

        ThreadGroup group = new ThreadGroup("Group1");

        for (int i = 1; i<=1000; i++) {
            Thread t = new Thread(new MyThread());
            t.start();
            threads.add(t);
        }

        group.interrupt();

        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Total = " + globalCounter);
    }

    static class MyThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(9999);
            } catch (InterruptedException ignored) {

            }

            globalCounter++;

//            // Race Condition issue!!
//            // problem with this is that if multiple threads share a global variable and read variable value the same
//            // example: running 3 threads, where thread 1 and 3 both read the globalCounter=1,
//            // then both threads increment globalCounter to 2 instead of 3 (1 -> thread1++1 -> thread3++1 -> 3)
//            // race condition behaviour: (1 -> thread1++1 -> [2]
//            //                            1 -> thread3++1 -> [2])
//            // expected behaviour: (1 -> thread1++1 -> 2 -> thread3++1 -> [3])
//            int localCounter = globalCounter;
//            localCounter = localCounter + 1;
//            globalCounter = localCounter;

        }
    }

//    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();
//    private static ThreadLocal<String> threadLocal = ThreadLocal.withInitial(() -> "initialValue");

//    public static void main(String[] args) {
//        Thread t1 = new Thread(new MyThread());
//        Thread t2 = new Thread(new MyThread());
//
//        t1.start();
//        t2.start();
//
//    }
//
//    static class MyThread implements Runnable {
//        @Override
//        public void run() {
//            int counter = 0;
//
//            threadLocal.set("myValue");
//            threadLocal.get();
//
//            System.out.println("ThreadLocal: " + threadLocal.get());
//        }
//    }

}
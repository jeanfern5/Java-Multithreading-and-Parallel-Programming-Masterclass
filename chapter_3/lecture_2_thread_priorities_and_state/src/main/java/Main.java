// Chapter 3 Lecture 2: Thread Priority and States

import javax.swing.plaf.TableHeaderUI;

public class Main {
//    public static void main(String[] args) throws InterruptedException {
//        // main thread
//        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
//
//        // new thread
//        Thread thread1 = new Thread(() -> {
//            Thread currentThread = Thread.currentThread();
//            System.out.println(currentThread.getName() + ": priority = " + currentThread.getPriority());
//        });
//        thread1.setName("Thread_1");
//        thread1.setPriority(Thread.MAX_PRIORITY);
//
//        Thread thread2 = new Thread(() -> {
//            Thread currentThread = Thread.currentThread();
//            System.out.println(currentThread.getName() + ": priority = " + currentThread.getPriority());
//        });
//        thread2.setName("Thread_2");
//        thread2.setPriority(Thread.MIN_PRIORITY);
//
//
//        thread1.start();
//        thread2.start();
//
//        thread1.join();
//        thread2.join();
//    }

    public static void main(String[] args) throws InterruptedException {
        // new thread
        Thread thread = new Thread(() -> {
            Thread currentThread = Thread.currentThread();
            System.out.println("[1] State: " + currentThread.getState()); // RUNNABLE
        });

        System.out.println("[2] State: " + thread.getState()); // NEW

        thread.start(); // started new thread
        System.out.println("[3] State: " + thread.getState()); // RUNNABLE

        thread.join(); // joins main thread
        System.out.println("[4] State: " + thread.getState()); // TERMINATE
    }
}
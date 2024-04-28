// Chapter 3 Lecture 4: Daemon Threads and User Threads
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(new MyThread(10), "Thread1"); // converted to daemon thread
        Thread thread2 = new Thread(new MyThread(3), "Thread2"); // user thread

        // considered low priority by jvm
        // not required to be completed before the jvm exits
        // in this example, thread1 will exit with thread2 finishes
        thread1.setDaemon(true); // daemon thread

        thread1.start();
        thread2.start();

        // joining daemon thread will make the jvm to wait until the thread is complete (user thread like)
        // example above, thread1 will print all 10 seconds
        thread1.join();
    }

    static class MyThread implements Runnable {
        private final int numberOfSeconds;
        public MyThread (int numberOfSeconds) {
            this.numberOfSeconds = numberOfSeconds;
        }

        @Override
        public void run() {
            for (int i = 0; i < numberOfSeconds; i++) {
                try {
                    System.out.println("Sleeping for 1 second, thread: " + Thread.currentThread().getName());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }}
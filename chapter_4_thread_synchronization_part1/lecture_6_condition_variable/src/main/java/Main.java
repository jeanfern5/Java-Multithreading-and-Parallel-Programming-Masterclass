import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    private static Lock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();

//    public static void main(String[] args) throws InterruptedException {
//        // condition await/signal is similar to synchronized blocks
//        // the biggest advantage is that await/signal can set multiple conditions per thread while wait/notify can only do 1 object per thread
//        // conditions are best for managing multiple threads based on a condition or complex producer-consumer situations
//        Condition condition = lock.newCondition();
//        Condition condition1 = lock.newCondition();
//        Condition condition2 = lock.newCondition();
//
//        // Thread 1
//        lock.lock();
//        condition.await();
//        lock.unlock();
//
//        // Thread 2
//        lock.lock();
//        condition.signal();
//        lock.unlock();
//    }

    public static void main(String[] args) {
        Queue<String> queue = new LinkedList<>();

        Thread producer = new Thread(new Producer(queue));
        Thread consumer = new Thread(new Consumer(queue));

        producer.start();
        consumer.start();
    }

    static class Producer implements Runnable {
        private final Queue<String> queue;

        public Producer(Queue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    producerData();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void producerData() throws InterruptedException {
            lock.lock();
            if (queue.size() == 10) {
                System.out.println("In producer, waiting...");
                condition.await();
            }

            Thread.sleep(1000);

            System.out.println("Producing data with id " + queue.size());
            queue.add("element_" + queue.size());

            if (queue.size() == 1) {
                // will unlock consumer from the waiting state
                condition.signal();
            }
            lock.unlock();
        }
    }

    static class Consumer implements Runnable {
        private final Queue<String> queue;

        public Consumer(Queue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    consumerData();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void consumerData() throws InterruptedException {
            lock.lock();
            if (queue.isEmpty()) {
                System.out.println("Consumer is waiting ...");
                condition.await();
            }

            Thread.sleep(700);

            String data = queue.remove();
            System.out.println("Consumer data: " + data);

            if (queue.size() == 9) {
                // will unlock producer from the waiting state
                condition.signal();
            }
            lock.unlock();
        }
    }
}

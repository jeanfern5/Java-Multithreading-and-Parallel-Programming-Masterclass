import java.util.LinkedList;
import java.util.Queue;

public class Main {

    private static final Object obj = new Object();

//    public static void main(String[] args) throws InterruptedException {
//
//        // Thread 1
//        synchronized (obj) {
//            obj.wait();
//            System.out.println("Next instructions");
//        }
//
//        // Thread 2
//        synchronized (obj) {
//            obj.notifyAll();
//            System.out.println("Next instructions");
//        }
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
            synchronized (queue) {
                if (queue.size() == 10) {
                    System.out.println("In producer, waiting ...");
                    queue.wait();
                }

                Thread.sleep(500);

                System.out.println("Producing data with id " + queue.size());
                queue.add("element_" + queue.size());

                if (queue.size() == 1) {
                    // will unlock consumer from the waiting state
                    queue.notify();
                }
            }
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
            synchronized (queue) {
                if (queue.isEmpty()) {
                    System.out.println("Consumer is waiting ...");
                    queue.wait();
                }

                Thread.sleep(250);

                String data = queue.remove();
                System.out.println("Consumer data " + data);

                if (queue.size() == 9) {
                    // will unlock producer from the waiting state
                    queue.notify();
                }
            }
        }
    }
}

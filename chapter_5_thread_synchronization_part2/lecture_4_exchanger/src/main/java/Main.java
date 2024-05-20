import java.util.concurrent.Exchanger;

public class Main {
//    public static void main(String[] args) throws InterruptedException {
//        // allows a pair of threads to exchange information - uneven pairs will be "stuck" and never execute
//        Exchanger<String> exchanger = new Exchanger<>();
//
//        // Thread 1
//        String receivedType1 = exchanger.exchange("value1");
//
//        // Thread 2
//        String receivedType2 = exchanger.exchange("value2");
//    }

    public static void main(String[] args) throws InterruptedException {
        Exchanger<String> exchanger = new Exchanger<>();

        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    String threadName = Thread.currentThread().getName();
                    String receivedValue = exchanger.exchange("value_from_" + threadName);
                    System.out.println("Received: " + receivedValue + " in thread " + threadName);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

        String receivedValue = exchanger.exchange("value_from_main"); // main thread
        System.out.println("Received: " + receivedValue + " in thread " + Thread.currentThread().getName());
    }
}

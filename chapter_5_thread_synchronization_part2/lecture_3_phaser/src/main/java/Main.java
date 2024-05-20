import java.util.concurrent.Phaser;

public class Main {
//    public static void main(String[] args) {
//        // similar to cyclic barrier but more flexible
//        Phaser phaser = new Phaser(3);
//
//        phaser.register(); // threads can register themselves and increase counter
//        phaser.arriveAndAwaitAdvance(); // similar to cyclic barrier "await()"
//        phaser.getPhase(); // gets internal counter
//        phaser.awaitAdvance(3); // takes phase ID to surpass that phase
//        phaser.arriveAndDeregister(); // non-block and decrease counter
//    }

    private static int S = 0;
    private static final int[] array = new int[] {1, 2, 3, 4, 5, 6, 7, 8 };

    private static final Phaser phaser = new Phaser(1); // 1 to account for the main thread
    public static void main(String[] args) {
        for (int i = 0; i < array.length; i++) {
            new Thread(new WorkerThread(i)).start();
        }

        phaser.arriveAndAwaitAdvance(); // main thread
        phaser.arriveAndAwaitAdvance(); // need to wait remainder thread from the custom threads

        System.out.println("The sum is: " + S);
        System.out.println("Phase count: " + phaser.getPhase());
    }

    static class WorkerThread implements Runnable {

        private final int threadIndex;

        public WorkerThread(int threadIndex) {
            this.threadIndex = threadIndex;
            phaser.register();
        }

        @Override
        public void run() {
            array[threadIndex] = array[threadIndex] * 2; // double values in-place
            phaser.arriveAndAwaitAdvance(); // threads wait here until all 8 have arrived here, once all here to next phase

            System.out.println("===> threadIndex: " + threadIndex);
            if (threadIndex == 0) {// 1 thread reaches here while the rest deregister
                for (int i : array) {
                    S = S + i; // sum doubled values
                }
                phaser.arriveAndAwaitAdvance(); // 1 thread
            } else {
                phaser.arriveAndDeregister();
            }
        }
    }
}
public class Main {
    /**
     *  There is a delay between cache to update in main memory so in this scenario where
     * private static int counter = 0;
     * Thread1 will not see the update `counter` value and will get stuck in an infinity loop
     * due to T1.localCounter never updating to what's in the main memory (RAM).
    */
//    private static int counter = 0;
    /**
     * "volatile" keyword read's directly from the shared variable in the main memory
     * to avoid stale data from the cached value
     * Trade off: Increases consistency but decreases performance because cache look up is quicker
     *  than the main memory (RAM)
     */
    private static volatile int counter = 0;
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            int localCounter = counter;

            while (localCounter < 10) {
                if (localCounter != counter) {
                    System.out.println("[T1] Local counter is changed " + localCounter);
                    localCounter = counter;
                }
            }
        });

        Thread t2 = new Thread(() -> {
            int localCounter = counter;

            while (localCounter < 10) {
                System.out.println("[T2] Incremented local counter to " + (localCounter + 1));
                counter = ++localCounter;

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();
    }
}

// Chapter 3 Lecture 3: Thread Groups
class Ch3_Lect3 {
    public static void main(String[] args) throws InterruptedException {
//        Thread thread1 = new Thread(() -> System.out.println("Thread1"));
//        Thread thread2 = new Thread(() -> System.out.println("Thread2"));
//        Thread thread3 = new Thread(() -> System.out.println("Thread3"));
//        thread1.start();
//        // ...
//        thread1.join();
//        // ..

//        // Instead of above we can be managed by ThreadGroups
//        ThreadGroup subGroup = new ThreadGroup("subGroup");
//        ThreadGroup rootGroup = new ThreadGroup(subGroup, "Group1"); // creates child/parent relation
//        rootGroup.getParent();

        // ThreadGroups help manage multiple threads as once
        ThreadGroup group1 = new ThreadGroup("Group1");

        ThreadGroup parent = group1.getParent();
        System.out.println("Parent name: " + parent.getName() + " priority = " + parent.getMaxPriority());

        group1.setMaxPriority(7); // sets boundary for subgroup threads

        Thread thread1 = new Thread(group1, new MyThread(), "Thread1");
        Thread thread2 = new Thread(group1, new MyThread(), "Thread2");
        Thread thread3 = new Thread(group1, new MyThread(), "Thread3");

        thread1.setPriority(Thread.MAX_PRIORITY); // will be reduced to groups max priority

        thread1.start();
        thread2.start();
        thread3.start();

        System.out.println("Sleeping for 3 seconds...");
        Thread.sleep(3000);

//
//        thread1.interrupt(); // lets the thread exit WAITING state
        group1.interrupt(); // applies to the whole group fo thread
    }

    static class  MyThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread currentThread = Thread.currentThread();
                    System.out.println("Name: " + currentThread.getName() + " priority = " + currentThread.getPriority());
                }
            }
        }
    }
}

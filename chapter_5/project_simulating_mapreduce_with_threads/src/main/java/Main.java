import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * Simulating MapReduce with Threads
 */
public class Main {
    /*

    -> Intermediate result:

    [
        friend: 1,
        need: 1,
        ...
    ]

    -> Reducers input:

    [
        [
            friend: 1,
            friend: 1
        ],
        [
            a: 1,
            a: 1
        ],
        [
            need: 1
        ],
        ...
    ]
     */
    private static final String input = "a friend in need is a friend indeed";
    private static final CountDownLatch countDownLatch = new CountDownLatch(2); // 2 counts == 2 Mapper threads
    private static final List<Map.Entry<String, Integer>> intermediateResult = Collections.synchronizedList(new ArrayList<>());
    private static final List<List<Map.Entry<String, Integer>>> reducersInput = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) throws InterruptedException {
        List<String> inputList = Arrays.asList(input.split(" "));

        // Map - map words in parallel between two threads and create "inputReducer"
        int midListIdx = inputList.size() / 2;
        new Thread(new Mapper(inputList.subList(0, midListIdx))).start();
        new Thread(new Mapper(inputList.subList(midListIdx, inputList.size()))).start();

        // Partition - filer unique words from inputReducer and re-order words group same words together
        Thread partitioner = new Thread(new Partitioner());
        partitioner.start();
        partitioner.join(); // makes sure the partitioner is complete before going to the Reducer - waits for the thread to die

        // Reducer - count words in each group and print count of each word
        for (List<Map.Entry<String,Integer>> reducerInput: reducersInput) {
            new Thread(new Reducer(reducerInput)).start();
        }

//        System.out.println("Test"); // useful breakpoint to see values from `intermediateResult` and `reducersInput`
    }

    static class Mapper implements Runnable {
        private final List<String> input;
        public Mapper(List<String> input) {
            this.input = input;
        }

        @Override
        public void run() {
            for (String word : input) {
                intermediateResult.add(Map.entry(word, 1));
            }
            // guarantees that `Partitioner` thread won't start before `Mapper` threads are done
            countDownLatch.countDown();
        }
    }

    static class Partitioner implements Runnable {
        @Override
        public void run() {
            try {
                // guarantees that `Partitioner` thread won't start before `Mapper` threads are done
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            List<String> uniqueWords = intermediateResult.stream()
                    .map(Map.Entry::getKey)
                    .distinct()
                    .collect(Collectors.toList());

            for (String word : uniqueWords) {
                List<Map.Entry<String, Integer>> reducerInput = intermediateResult.stream()
                        .filter(entry -> entry.getKey().equals(word))
                        .collect(Collectors.toList());
                reducersInput.add(reducerInput);
            }
        }
    }

    static class Reducer implements Runnable {
        private final List<Map.Entry<String, Integer>> reducerInput;
        public Reducer(List<Map.Entry<String, Integer>> reducerInput) {
            this.reducerInput = reducerInput;
        }

        @Override
        public void run() {
            int sum = 0;
            for (Map.Entry<String, Integer> entry : reducerInput) {
                sum += entry.getValue();
            }

            System.out.println("The word: " + reducerInput.get(0).getKey() + " -> occurrences: " + sum);
        }
    }
}

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
/**
 * === REQUIREMENTS ===
 * 1. Periodically scans the ./src/main/resources directory and watches for new files
 * 2. For each file found into this directory, a new thread should take care of its processing
 * 3. Processing = each line of the file will be hashed using a hashing algorithm
 * 4. The program will create new files, marked _output suffix, and they will placed into ./src/main/output
 * 5. Throw an exception if a line is empty
 */

public class Main {
    // local caching to track process files to not process them more than once
    // Not a perfect solution due to a new thread being created per file and running in parallel
    // Some files will still be re-run more than once due to race condition where a file is still being processed
    // but a new thread gets created for said file before added to the map
    // some potential improvements:
    // 1. increase sleep timer - will give process more time to add file to map before new thread starts (reduce re-runs of the same file)
    // 2. add file to map before completing the process - risk file has an error and marked as "successful" when in fact it failed (file misses)
    // 3. add a "pending" state where it shows the file is in progress so that the new thread doesn't work on processing the file <---- best choice

//    static Map<String, Set<String>> processedFiles = new HashMap<>(); // Not a perfect solution due to race conditions

    /**
     * improved but not perfect: adding "volatile" keyword seems to have helped with race condition issue where the same file was being
     * processed more than once - tried about 20 times
     * this is due to the shared variable been up-to-date since it's getting the value from the main memory instead
     * of the thread's cache which can be stale since there's a delay between the main memory and cache being synced
     *
     * This can still lead to a race condition but for this specific setup it doesn't since it's basic enough
     * */
    static volatile Map<String, Set<String>> processedFiles = new HashMap<>();

    public static void main(String[] args) {
        new Thread(new Watcher(processedFiles)).start();
    }
}

class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final File file;
    Map<String, Set<String>> processedFiles;
    String PENDING = "pending";
    String FAILED = "failed";

    public ExceptionHandler(Map<String, Set<String>> processedFiles, File file) {
        this.processedFiles = processedFiles;
        this.file = file;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        processedFiles.get(PENDING).remove(file.getName());
        processedFiles.get(FAILED).add(file.getName());
        System.out.println("Files failed to process: " + processedFiles.get(FAILED));
        System.out.println("Exception handled: " + e.getMessage());
    }
}

class Watcher implements Runnable {
    Map<String, Set<String>> processedFiles;
    String SUCCESSFUL = "successful";
    String PENDING = "pending";
    String FAILED = "failed";

    public Watcher(Map<String, Set<String>> processedFiles) {
        this.processedFiles = processedFiles;
        this.processedFiles.put(SUCCESSFUL, new HashSet<>());
        this.processedFiles.put(PENDING, new HashSet<>());
        this.processedFiles.put(FAILED, new HashSet<>());
    }

    @Override
    public void run() {
        File inputDirectory = new File("./src/main/resources");

        while (true) {
            if (inputDirectory.listFiles().length != 0) {
                Arrays.stream(inputDirectory.listFiles()).forEach(
                    file -> {
                        boolean fileIsNotInSuccessful = !processedFiles.get(SUCCESSFUL).contains(file.getName());
                        boolean fileIsNotInPending = !processedFiles.get(PENDING).contains(file.getName());
                        if (fileIsNotInSuccessful && fileIsNotInPending) {
                            /** When "volatile" wasn't used for processedFiles there was a weird situation where
                             * ```Thread t = new Thread(new FileProcessor(file, processedFiles));```
                             * leads to a race condition compared to variable reference when tried over 20 times each
                             *  ```FileProcessor proc = new FileProcessor(file, processedFiles);
                             *     Thread t = new Thread(proc);```
                             * I would have expected both method to have the same behaviour but doesn't seem to be the case
                             * I wonder if the variable reference slows down the thread enough to
                             * 1. give Thread1 enough time to update the map and Thread2 to read that file is "pending" OR
                             * 2. read from the `processedFiles` map where the cached variable has been synced with the main memory
                            */
                             Thread t = new Thread(new FileProcessor(file, processedFiles));
                            t.setUncaughtExceptionHandler(new ExceptionHandler(processedFiles, file));
                            t.start();
                        }
                    }
                );
            }

            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class FileProcessor implements Runnable {

    private final File file;
    private final static String OUTPUT_PATH = "./src/main/output/";

    Map<String, Set<String>> processedFiles;
    String SUCCESSFUL = "successful";
    String PENDING = "pending";

    public FileProcessor(File file, Map<String, Set<String>> processedFiles) {
        this.file = file;
        this.processedFiles = processedFiles;
        processedFiles.get(PENDING).add(file.getName());
    }

    @Override
    public void run() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_PATH + file.getName()));

            Files.lines(Path.of(file.getCanonicalPath()))
                    .map(this::hash)
                    .map(line -> line + "\n")
                    .forEach(el  -> {
                        try {
                            writer.write(el);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            writer.close();
            processedFiles.get(SUCCESSFUL).add(file.getName());
            processedFiles.get(PENDING).remove(file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " -> processed file: " + file.getName());
    }

    private String hash(String input) {
        if (input.equals("")) {
            throw new RuntimeException("The line is empty, hashing cannot be done");
        }

        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            final byte[] hashbytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashbytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
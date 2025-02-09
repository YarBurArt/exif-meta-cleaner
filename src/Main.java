import java.awt.image.BufferedImage;
import java.io.*; // for File
import java.security.*; // for md5
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import static java.lang.System.exit;

public class Main {

    public static void main(String[] args) {
        // default variables args.length == 0
        final String defaultFolderPath = "images/"; // from project root
        // like array reference final because executorService and threads
        final String[] folder_path = {defaultFolderPath};
        if (args.length > 0) {
            switch (args[0]) { // crutchy but pure java
                case "--help":
                    printHelp();
                    return;
                case "--path":
                    assert(args.length > 1); // DEBUG
                    folder_path[0] = args[1] + "/";
                    System.out.println("Set path to " + folder_path[0]);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + args[0]
                            + "\nWho're you trying to crack? We're in jvm.");
            }
        }

        List<String> files_formats = new ArrayList<>(
                Arrays.asList("png", "jpg", "jpeg", "jfif"));

        File dir = new File(folder_path[0]);
        File[] files = Objects.requireNonNull(dir.listFiles());

        int numThreads = Runtime.getRuntime().availableProcessors(); // get available cores
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads); // create threads pool

        // for catch errors from executorService
        try {
            // walking through files in a folder
            for (File file : files)
                executorService.submit(() -> {
                    // for catch errors from ImageIO
                    try {
                        processImage(file, files_formats, folder_path[0]);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            // to end executor
            executorService.shutdown();
            // check correct complete tasks
            boolean allTasksCompleted = executorService.awaitTermination(
                    Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            if (allTasksCompleted) System.out.println("All work completed successfully!");
            else System.out.println("Some tasks did not complete for non error reason...");
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void printHelp() {
        System.out.println(
            """
             Hi from the dir img metadata cleanup tool!
            \t--help to print this message
            \t--path ./to/images/ to set path start at working dir
             Example: java ./Main.class --path images/\s
             Check the decompiled from byte code if you don't trust me.
            """);
    }

    public static void processImage(
        File file, List<String> files_formats, String folder_path
    ) throws IOException {
        if (!isValidImageFile(file))
            return; // if exploit is too simple, it's not interesting
        if (file.isFile()) {
            // split name by dot, list<String> type for get()
            List<String> sliced_file_name = new ArrayList<>(
                    Arrays.asList(file.getName().split("\\b.\\b"))
            );
            // get latest file format from name
            String file_format = sliced_file_name.get(sliced_file_name.size() -1);

            // image format only
            if (files_formats.contains(file_format)) {
                // write to memory without meta n exif
                BufferedImage image = ImageIO.read(file);
                // changing filename to hash with salt
                String name_hash = MD5(file.getName() + "lhjb");
                String new_file_name = (name_hash != null) ? name_hash : file.getName();
                // save to new file
                ImageIO.write(image, file_format, new File(
                        folder_path + new_file_name + "." + file_format)
                );
                if (new_file_name.equals(name_hash)) // delete old file
                    if (file.delete()) System.out.printf("Old file %s is delete \n", file.getName());

                System.out.printf("File %s is clean ... \n", file.getName());
            }
        }
    }
    private static boolean isValidImageFile(File file) {
        try (InputStream is = new FileInputStream(file)) {
            byte[] header = new byte[8];
            int read = is.read(header);
            if (read != 8) return false; // too small
            return isValidImageHeader(header); // else check headers
        } catch (IOException e) {
            return false; // no access to file
        }
    }
    private static boolean isValidImageHeader(byte[] header) {
        // check for PNG header
        if (header[0] == (byte) 0x89 && header[1] == (byte) 0x50
            && header[2] == (byte) 0x4E && header[3] == (byte) 0x47) return true;

        // check for JPEG/JFIF header
        if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8) {
            // double check for JFIF
            if (header.length >= 10 && header[2] == (byte) 0xFF && header[3] == (byte) 0xE0 &&
                header[6] == (byte) 0x4A && header[7] == (byte) 0x46
                && header[8] == (byte) 0x49 && header[9] == (byte) 0x46) return true;
            // then JPEG
            return true;
        }
        return false;
    }

    public static String MD5(String md5) {
        try {
            // md5 calculation as a byte array
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            // convert to normal string
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
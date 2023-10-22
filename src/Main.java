import java.awt.image.BufferedImage;
import java.io.*;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

public class Main {

    public static void main(String[] args) {
        String folder_path = "images/"; // from project root
        List<String> files_formats = new ArrayList<>(
                Arrays.asList("png", "jpg", "jpeg", "jfif"));

        // for catch errors from ImageIO
        try {
            File dir = new File(folder_path);
            // walking through files in a folder
            for (File file : dir.listFiles()) {
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
                        if (new_file_name.equals(name_hash))
                            file.delete(); // delete old file

                        System.out.printf("File %s is clean ... \n", file.getName());
                    }
                }
            }
            System.out.println("All work completed successfully!");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    public static String MD5(String md5) {
        try {
            // md5 calculation as a byte array
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            // convert to normal string
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
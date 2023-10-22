import java.awt.image.BufferedImage;
import java.io.*;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

public class Main {

    public static void main(String[] args) {
        String folder_path = "images/";
        List<String> files_formats = new ArrayList<String>(Arrays.asList("png", "jpg", "jpeg", "jfif"));

        try {
            File dir = new File(folder_path);
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    List<String> sliced_file_name = new ArrayList<String>(
                            Arrays.asList(file.getName().split("\\b.\\b"))
                    );
                    String file_format = sliced_file_name.get(sliced_file_name.size() -1);
                    //System.out.println(file_format);
                    if (files_formats.contains(file_format)) {
                        BufferedImage image = ImageIO.read(file);
                        String name_hash = MD5(file.getName());
                        String new_file_name = (name_hash != null) ? name_hash : file.getName();
                        System.out.println(new_file_name);
                        ImageIO.write(image, file_format, new File(
                                folder_path + new_file_name + "." + file_format)
                        );
                        if (new_file_name == name_hash)
                            file.delete();

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
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
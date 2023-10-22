import java.io.*;

import javax.imageio.ImageIO;

public class Main {

    public static void main(String[] args) {
        String folder_path = "images/";

        try(File dir = new File(folder_path))
        {
//            // читаем посимвольно
//            int c;
//            while((c=reader.read())!=-1){
//
//                System.out.print((char)c);
//            }
            System.out.println("");
        }
        catch(IOException ex){

            System.out.println(ex.getMessage());
        }
    }
}
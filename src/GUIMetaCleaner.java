import java.io.*; 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.*;

import java.awt.image.BufferedImage;
import java.security.*; // for md5
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;

public class GUIMetaCleaner extends JFrame implements ActionListener {

    private JButton button;
    private JButton st_button;
    //private JLabel label;
    private JTextArea textarea;
    private JTextArea infoArea;
    private JTextField defaultText; 

    public GUIMetaCleaner() {
        super("Выбор папки");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        button = new JButton("Выбрать папку");
        button.addActionListener(this);
        add(button);

        st_button = new JButton("Стереть всю мету");
        st_button.addActionListener(this);
        
        defaultText = new JTextField("Выберите папку...", 20);
        add(defaultText);        
        
        textarea = new JTextArea("В выбранной папке у всех изображений будут\n отчищенны метаданные, нажмите старт чтобы начать\n (никакой загрузки пока нет, просто немного зависнет окно)");
        textarea.setEditable(false);
        add(textarea);
        add(st_button);
        
        JTextArea infoArea = new JTextArea("---------------- Здесь лог выполнения -------------");
        infoArea.setEditable(false); // Make it non-editable
        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.SOUTH); // Add to the bottom

        pack();
        setVisible(true);
        setSize(400, 200);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            JFileChooser chooser = new JFileChooser();
            // Updated constant for directory selection
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
            int result = chooser.showDialog(this, "Выберите папку");

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                String absolutePath = selectedFile.getAbsolutePath();
                defaultText.setText(absolutePath);            
            }
        } else if (e.getSource() == st_button) {
            clean_folder(defaultText.getText());
        }
    }

    public static void main(String[] args) {
        GUIMetaCleaner clean = new GUIMetaCleaner();
        // System.out.println(clean.defaultText.getText());
    }
    public void clean_folder(String folder_path) {
        JScrollPane scrollPane = (JScrollPane) this.getContentPane().getComponent(4); 
        JTextArea infoArea = (JTextArea) scrollPane.getViewport().getView(); // Get the JTextArea inside
        infoArea.append("\nПолучен путь ...\n"); // Append initial message

        //String folder_path = "images/"; // from project root
        List<String> files_formats = new ArrayList<>(
                Arrays.asList("png", "jpg", "jpeg", "jfif"));

        // for catch errors from ImageIO
        try {
            File dir = new File(folder_path);
            // walking through files in a folder
            for (File file : Objects.requireNonNull(dir.listFiles())) {
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
                                folder_path + "/" + new_file_name + "." + file_format)
                        );
                        if (new_file_name.equals(name_hash)) // delete old file
                            if (file.delete()) infoArea.append("\nOld file %s is delete \n" + file.getName());

                        infoArea.append("\nFile %s is clean ... \n" + file.getName());
                    }
                }
            }
            infoArea.append("\nAll work completed successfully!");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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

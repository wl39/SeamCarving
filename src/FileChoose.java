import javax.swing.JFileChooser;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.awt.*;
import java.awt.Desktop;

public class FileChoose extends JFrame{

    static private int new_val;
    static private String selected_file_path;
    static private FileChoose fc = new FileChoose();
    static private JFrame frame = new JFrame();
    static private JButton b1 = new JButton("Choose a File");
    static private JButton b2 = new JButton("Horizontal Seam");
    static private JPanel panel = new JPanel();
    static private JLabel label1 = new JLabel("Test");
    static private JLabel label2 = new JLabel("Test");
    static private JLabel label3 = new JLabel("Test");
    static private Boolean boolean_val = false;
    static private int count = 0;
    static private JTextField textField = new JTextField(20);
    static private JLabel picLabel;
    static private Timer timer;
    static private String newLine = "\n";


    public static void main(String[] args){
        FileChoose fc = new FileChoose();

        fc.frontendGUI();

    }



    public void openBrowse() throws IOException{
        JFileChooser file_choose = new JFileChooser();
        FileNameExtensionFilter file_filter= new FileNameExtensionFilter("All image related files: jpg/jpeg/png/gif", "jpg", "gif", "jpeg", "png");
        file_choose.setFileFilter(file_filter);
        new_val = file_choose.showOpenDialog(null);
        if(new_val == JFileChooser.APPROVE_OPTION) {
            selected_file_path = file_choose.getSelectedFile().getAbsolutePath();
            //
            SeamCarving seamCarving1 = new SeamCarving(selected_file_path);
            SeamCarving seamCarving2 = new SeamCarving(selected_file_path);

            if(textField.getText().equals("")) {
                JOptionPane.showMessageDialog(frame, "Please Input the Number");
            }

            int number_pixels = Integer.parseInt(textField.getText());


            if(number_pixels <= 0) {
                JOptionPane.showMessageDialog(frame, "Invalid Input");
            }

            try {

                Color[][] seeSeam = seamCarving1.seamCarving(number_pixels,boolean_val);
                Color[][] newRGB = seamCarving2.seamAdding(number_pixels, boolean_val, seamCarving1.multipleSeams(boolean_val));

                seamCarving1.makeFile(seeSeam, "Seam");
                seamCarving1.makeFile(seamCarving1.rgb, seamCarving1.getX(), seamCarving1.getY(), "Removal");
                seamCarving1.makeFile(newRGB, seamCarving2.getX(), seamCarving2.getY(), "Insertion");

                File processed_image_file = new File("Removal.jpg");
                Desktop dt = Desktop.getDesktop();
                dt.open(processed_image_file);




            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Please Input the Integer");
            } catch (ArrayIndexOutOfBoundsException e) {
                if(boolean_val) {
                    JOptionPane.showMessageDialog(frame, "Input has to be bigger than 0 and the maximum value is " + seamCarving1.getY());
                } else {
                    JOptionPane.showMessageDialog(frame, "Input has to be bigger than 0 and the minimum value is " + seamCarving1.getX());
                }
            }
        }


    }

    public void frontendGUI(){

//        add(progressBar);
        panel.setLayout(null);
        label1.setText("SEAM CARVING ON INPUT IMAGE");
        label2.setText("<html>ENTER NUMBER OF<br />SEAMS TO REMOVE:</html>");
        frame.setVisible(true);
        frame.setTitle("Seam Carving");
        frame.setSize(400,357);
        label1.setBounds(50,10,400,50);
        panel.add(label1);
        label1.setFont(new Font("Times", Font.BOLD, 17));
        label2.setBounds(220, 280, 300, 50);
        label2.setFont(new Font("Times", Font.BOLD, 10));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        b1.setBounds(0,90,400,200);
        b1.setBackground(Color.LIGHT_GRAY);
        b2.setBounds(0,290,200,30);
        b2.setBackground(Color.LIGHT_GRAY);

        //frame.add(panel);

        textField.setBounds(350, 290, 200,30);





        label1.setForeground(Color.red);
        b1.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {

                try{

                    fc.openBrowse();

                }
                catch(Exception a){
                    a.printStackTrace();
                    System.out.println(a);
                }
            }
        });

        b2.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e){
                count ++;
                if(count%2 == 1) {
                    b2.setText("Vertical Seam");
                    boolean_val = true;
                }

                else {
                    b2.setText("Horizontal Seam");
                    boolean_val = false;
                }

            }
        });

        frame.add(panel);
        panel.add(label1);
        panel.add(label2);
        panel.add(b1);
        panel.add(b2);
        panel.add(textField);




    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaEEG;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Admin
 */
public class Widok extends JFrame{
    
    JButton jeden=new JButton("Jeden");
    JButton dwa=new JButton("Jeden");
    JButton trzy=new JButton("Jeden");
    JButton cztery=new JButton("Jeden");
    JButton piec=new JButton("Jeden");
    JButton szesc=new JButton("Jeden");
    Widok()
    {
        JFrame frame = new JFrame("EEG");
        frame.setLayout(new BorderLayout());
        SpringLayout layout=new SpringLayout();
        JPanel panel= new JPanel(layout);
        setFullScreen(frame, true); //Pełny ekran
        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.setBackground(new Color(32,113,154));
        panel.setBackground(new Color(32,113,154));
        frame.add(panel,BorderLayout.CENTER);
        
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        CreateSelectFile(frame);    // wywołanie FileChooser
        JSlider slider = new JSlider(); slider.setPreferredSize(new Dimension (660,20));
        JTextArea mozg = new JTextArea("\n \n \n \n \n MOZG");  mozg.setColumns(70); mozg.setRows(40);
        JTextArea graf = new JTextArea("\n \n \n \n \n GRAF");  graf.setColumns(60); graf.setRows(35);
        panel.add(mozg); panel.add(graf); panel.add(slider); panelButtons.setPreferredSize(new Dimension(660, 30));
       panel.add(panelButtons);
            layout.putConstraint(SpringLayout.WEST, mozg, 40, SpringLayout.NORTH, panel);
            layout.putConstraint(SpringLayout.NORTH, mozg, 50, SpringLayout.WEST, panel);
            layout.putConstraint(SpringLayout.WEST, graf, 40, SpringLayout.EAST, mozg);
            layout.putConstraint(SpringLayout.NORTH, graf, 50, SpringLayout.WEST, panel);
            layout.putConstraint(SpringLayout.WEST, slider, 40, SpringLayout.EAST, mozg);
            layout.putConstraint(SpringLayout.NORTH, slider, 15, SpringLayout.SOUTH, graf);
            layout.putConstraint(SpringLayout.WEST, panelButtons, 40, SpringLayout.EAST, mozg);
            layout.putConstraint(SpringLayout.NORTH, panelButtons, 15, SpringLayout.SOUTH, slider);
          panelButtons.add(jeden);panelButtons.add(dwa);panelButtons.add(trzy);panelButtons.add(cztery);panelButtons.add(piec);panelButtons.add(szesc);
            
        frame.setVisible(true);
        frame.setResizable(true);
    }
 
    public static void CreateSelectFile(JFrame frame)
    {   String userDir = System.getProperty("user.home");
        JFileChooser fc = new JFileChooser(userDir+"/Desktop");
        
       
        fc.setFileFilter(new FileNameExtensionFilter("Pliki tekstowe", "txt"));     // tylko rozszerzenie txt
        int returnVal = fc.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            ReadFile(fc);
           // log.append("Opening: " + file.getName() + "." + newline);
        } 
        else if(returnVal == JFileChooser.CANCEL_OPTION)
        {
            System.exit(0);
        }
    }
    
    public static void setFullScreen(JFrame frame, boolean fullScreen) {
  frame.dispose();
  frame.setResizable(!fullScreen);
  if (fullScreen) {
    frame.setLocation(0, 0);
    frame.setSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
  }
  frame.setVisible(true);
  frame.repaint();
}
 
    public static void ReadFile(JFileChooser fc)
    {
        
        File plik = fc.getSelectedFile();
        String nazwaPlik=plik.getName();
        String path = plik.getAbsolutePath();
        JOptionPane.showMessageDialog(null, "Wybrany Plik to: "+ nazwaPlik +" Scieżka: "+ path);
       // final File currentDir = new File(katalog);
       // System.out.println("Wybrano plik: " + plik);
       // System.out.println("w katalogu: "+ katalog);
       // System.out.println("Ścieżka: "+ katalog + plik);
    }
    public static void main(String[] args) {
        // TODO code application logic here
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run()
            {
                new Widok();
            }
        });
        
    }
    
   
    
    
}

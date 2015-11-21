/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaEEG;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Admin
 */
public class Widok {

    //Jfile chooser
    public static void CreateSelectFile(JFrame frame)
    {   String userDir = System.getProperty("user.home");
        JFileChooser fc = new JFileChooser(userDir+"/Desktop");                      //ustawienie katalogu startowego
        
       
        fc.setFileFilter(new FileNameExtensionFilter("Pliki tekstowe", "txt"));     // tylko rozszerzenie txt
        int returnVal = fc.showOpenDialog(frame);                                   //okno
        if (returnVal == JFileChooser.APPROVE_OPTION) { ReadFile(fc); }             //naciśnięcie "Wczytaj"
        else if(returnVal == JFileChooser.CANCEL_OPTION) { }                        // naciśnięcie "Anuluj"
    }
    

    public static void ReadFile(JFileChooser fc)                                    //czytanie pliku
    {
        
        File plik = fc.getSelectedFile();
        String nazwaPlik=plik.getName();                                            //nazwa pliku
        String path = plik.getAbsolutePath();                                       //ścieżka do pliku
        JOptionPane.showMessageDialog(null, "Wybrany Plik to: "+ nazwaPlik +" Scieżka: "+ path);    //okno z informacją
   
        
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));           
            String str;
            String str2;
            str = in.readLine();
            str = in.readLine();
            int lines=0;
            int i=0;
            
            while ((str=in.readLine()) != null){ lines++;} in.close();              // zliczanie ilości linii w pliku
            double[] chanel1 = new double[lines];
            double[] chanel2 = new double[lines];                                   //deklaracja tablic dla poszególnych kanałów 
            double[] chanel3 = new double[lines];
            double[] chanel4 = new double[lines];
            
            BufferedReader in2 = new BufferedReader(new FileReader(path));
            str2 = in2.readLine();
            str2 = in2.readLine();
            
            
            while ((str2 = in2.readLine()) != null) {                                   
                String[] ar=str2.split(",");                                          //rozdzielenie liczb po przecinku
                
              chanel1[i]=Double.parseDouble(ar[0]);                                     //wpisanie wartości do talbic
              chanel2[i]=Double.parseDouble(ar[1]);
              chanel3[i]=Double.parseDouble(ar[2]);
              chanel4[i]=Double.parseDouble(ar[3]);
              i++;
            }
           for (int j=0; j<lines; j++) System.out.println(chanel1[j]+" "+chanel2[j]+" "+chanel3[j]+" " +chanel4[j]);
            in2.close();
        } 
        catch (FileNotFoundException fne) {
                        JOptionPane.showMessageDialog(null, "Nie znaleziono pliku");                    
            }
        catch (IOException e) {
           JOptionPane.showMessageDialog(null, "Błąd odczytu danych");
        }
        
    
    }
    public static void main(String[] args) {
        // TODO code application logic here
        
        
    }
    
   
    
    
}

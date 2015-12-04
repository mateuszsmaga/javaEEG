/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaEEG;
import javaEEG.DFT;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;
import biz.source_code.dsp.math.Complex;
import org.jtransforms.utils.*;
import org.jtransforms.fft.DoubleFFT_1D;
import org.jtransforms.fft.DoubleFFT_2D;

/**
 *
 * @author Admin
 */
public class Widok {

  public static  double[] chanel1;
  public static  double[] chanel2;
  public static  double[] chanel3;
  public static  double[] chanel4;

                  
    
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
            chanel1= new double[lines];
            chanel2 = new double[lines];                                   //deklaracja tablic dla poszególnych kanałów 
            chanel3 = new double[lines];
            chanel4 = new double[lines];
            
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
          // for (int j=0; j<lines; j++) System.out.println(chanel1[j]+" "+chanel2[j]+" "+chanel3[j]+" " +chanel4[j]+" "+lines);
            in2.close();

        } 
        catch (FileNotFoundException fne) {
                        JOptionPane.showMessageDialog(null, "Nie znaleziono pliku");                    
            }
        catch (IOException e) {
           JOptionPane.showMessageDialog(null, "Błąd odczytu danych");
        }
        
        
       // Complex[] r=DFT.directDft(chanel1);
       //   for (int j=0; j<chanel1.length;j++) System.out.println(r[j]+"      " +j);
          
        DoubleFFT_1D fft = new DoubleFFT_1D(chanel1.length);
        double FS= 250;
        double[] fft2 = new double[chanel1.length*2];
        System.arraycopy(chanel1, 0, fft2, 0, chanel1.length);
        fft.realForward(fft2);
        double real;
        double im;
        double[] magnitude = new double[fft2.length/2];
        for (int a=0; a<=fft2.length; a++)
        {
            real=fft2[2*a];
            im=fft2[2*a+1];
        magnitude[a]= Math.sqrt(real*real+im*im);
        }
        for(int j=0; j<magnitude.length;j++) System.out.println(magnitude[j]+"   "+j);
    /*    int a=0;
        for(double d: fft2)
        {
            a=a+1;
            System.out.println(d+"   "+a);
        }*/
    
    }

    public static void main(String[] args) {
        // TODO code application logic here
        
        
    }
    
   
    
    
}

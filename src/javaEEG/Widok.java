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
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jtransforms.fft.DoubleFFT_1D;

/**
 *
 * @author Admin
 */
public class Widok {

  public static  double[] chanel1;
  public static  double[] chanel2;
  public static  double[] chanel3;
  public static  double[] chanel4;
  public static int lines=0;
                  
    
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
        
     /*
      DoubleFFT_1D fftDo = new DoubleFFT_1D(chanel1.length);
      double[] fft= new double[chanel1.length*2];
      
      System.arraycopy(chanel1, 0, fft, 0, chanel1.length);
      for(double d: fft) System.out.println(d);
        
      
      System.out.println("**********************************");
     
      
      fftDo.realForwardFull(fft);
      for (double d: fft) System.out.println(d);
      
      
        System.out.println("--------------------------------");
       
        fftDo.complexInverse(fft, true);
        for(double d:fft) System.out.println(d);
       */
    licz(chanel1);
     
    }
    
    public static void licz (double[] chanel)
    {   
        File plik = new File("C:\\Users\\Admin\\Desktop\\wyniki.txt");
        File plik2 = new File("C:\\Users\\Admin\\Desktop\\Dane.txt");
            PrintWriter zapis=null;
            PrintWriter zapis2=null;
            
      try {
         zapis = new PrintWriter("C:\\Users\\Admin\\Desktop\\wyniki.txt");
         zapis2= new PrintWriter("C:\\Users\\Admin\\Desktop\\Dane.txt");
          
        int N= chanel.length;
        int Fs=25600;
        double re;
        double im;
        double fft[]=new double[N*2];
        double[] magnitude = new double[N/2];
      zapis2.println("liczba próbek z kanału:"+chanel.length);
            
      //System.arraycopy(chanel,0,fft,0,chanel.length);
      
      for (int i=0; i<N-1; i++)
        {
            fft[2*i]=chanel[i];
            fft[2*i+1]=0;
        }
      
       
       //        for (int i=0; i<N*2; i++)
      //  {
       //   zapis.println("chanel przed fft: "+String.format("%.9f",fft[i]));
           // System.out.println("chanel przed fft: "+String.format("%.9f",fft[i]));
      //  }
       /*  zapis.println("****************************************");
        System.out.println("*************************************");*/
         
        DoubleFFT_1D fftDo =new DoubleFFT_1D(N);
        fftDo.complexForward(fft);
        
        for (double d: fft) 
        {   
            zapis.println("chanel po fft: "+String.format("%.9f",d));
           // System.out.println("chanel po fft:"+String.format("%.9f",d));
        }
        
        zapis.println("*****************************************");
       //   System.out.println("*******************************************");
        zapis2.println("część real            część imaginary");
        for(int i=0;i<N/2-1;i++)
        {
            re=fft[2*i];
            im=fft[2*i+1];
            zapis2.println(String.format("%.9f",re)+"   "+String.format("%.9f",im));
            magnitude[i]=Math.sqrt(re*re+im*im);
           // System.out.println("napięcie: "+String.format("%.9f",magnitude[i]));
            zapis.println("magnitude: "+String.format("%.9f",magnitude[i]));
        }
        
        double max_magnitude =-999999999;
        int max_index = -1;
        for (int i=0; i<N/2-1;i++)
        {
            if (magnitude[i]>max_magnitude)
            {
                max_magnitude=magnitude[i];
                max_index=i;
            }
        }
        
        double freq = (max_index*Fs)/(fft.length);
        System.out.println("freq:"+String.format("%.9f",freq)+"  max_index: "+max_index+"  max_magnitude "+max_magnitude);
        
        
        zapis.println("freq:"+String.format("%.9f",freq)+" max_index"+max_index+" max_magnitude:"+max_magnitude);
        zapis.close();
        zapis2.close();
      }
   
      
      catch (FileNotFoundException ex) {
          Logger.getLogger(Widok.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
          Logger.getLogger(Widok.class.getName()).log(Level.SEVERE, null, ex);
      }
    
    }

    public static void main(String[] args) {
        // TODO code application logic here
        
        
    }
    
   
    
    
}

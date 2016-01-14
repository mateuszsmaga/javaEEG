package javaEEG;


import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;
import biz.source_code.dsp.filter.*;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fastica.*;
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
    licz(chanel2);
     
    }
    
    public static void licz (double[] chanel)
    {   
        File plik = new File("C:\\Users\\Admin\\Desktop\\wyniki.txt");
        File plik2 = new File("C:\\Users\\Admin\\Desktop\\Dane.txt");
        File plik3 = new File("C:\\Users\\Admin\\Desktop\\Chanel.txt");
        File gamma = new File ("C:\\Users\\Admin\\Desktop\\Gamma.txt");
        File beta = new File ("C:\\Users\\Admin\\Desktop\\Beta.txt");
        File alfa = new File ("C:\\Users\\Admin\\Desktop\\Alfa.txt");
        File theta = new File ("C:\\Users\\Admin\\Desktop\\Theta.txt");
            PrintWriter zapis=null;
            PrintWriter zapis2=null;
            PrintWriter zapis3=null;
            PrintWriter zapis4=null;
            PrintWriter zapis5=null;
            PrintWriter zapis6=null;
            PrintWriter zapis7=null;
      try {
         zapis = new PrintWriter("C:\\Users\\Admin\\Desktop\\wyniki.txt");
         zapis2= new PrintWriter("C:\\Users\\Admin\\Desktop\\Dane.txt");
         zapis3 = new PrintWriter(plik3);
         zapis4 = new PrintWriter(gamma);
         zapis5 = new PrintWriter(beta);
         zapis6 = new PrintWriter(alfa);
         zapis7 = new PrintWriter(theta);
         for(int i=0;i<chanel.length;i++)
         {
           zapis3.println(String.format("%.9f", chanel[i]));
         }
         
        
         
          
        int N= chanel.length;
        double  Fs=256;
        double re;
        double im;
        double fft[]=new double[N*2];
        double[] magnitude = new double[N/2];
      zapis2.println("liczba próbek z kanału:"+chanel.length);
            
      //System.arraycopy(chanel,0,fft,0,chanel.length);
     
             FilterPassType dolnopaswmowy = FilterPassType.lowpass;
        FilterCharacteristicsType typFilt = FilterCharacteristicsType.butterworth;
        double odciecie=40/Fs;
        
         
          FilterPassType gornopaswmowy = FilterPassType.highpass;
        FilterCharacteristicsType typFilt2 = FilterCharacteristicsType.butterworth;
        double odciecie2=0.3/Fs;
        
            IirFilterCoefficients coefss2 = IirFilterDesignFisher.design(gornopaswmowy, typFilt, 6, 0, odciecie2, 0);
            IirFilter filtr3 = new IirFilter(coefss2);
            for(int a=0; a<chanel.length; a++)
            {
                przefiltrowany[a]=filtr3.step(chanel[a]);
                //zapis4.println(String.format("%.9f",przefiltrowany[a]));
            }
          IirFilterCoefficients coefss = IirFilterDesignFisher.design(dolnopaswmowy, typFilt2, 8, 0, odciecie, 0);
            IirFilter filtr2 = new IirFilter(coefss);
            for(int a=0; a<chanel.length; a++)
            {
                przefiltrowany[a]=filtr2.step(przefiltrowany[a]);
                zapis4.println(String.format("%.9f",przefiltrowany[a]));
            } 
      double[] przefiltrowanyGamma = new double[chanel.length];
      double[] przefiltrowanyBeta = new double[chanel.length];
      double[] przefiltrowanyAlfa = new double[chanel.length];
      double[] przefiltrowanyTheta = new double[chanel.length];
      
             FilterPassType srodkowopaswmowy = FilterPassType.bandpass;
             FilterCharacteristicsType typFilt3 = FilterCharacteristicsType.butterworth;
        
        
        double dolnyGamma = 30/Fs;
        double gornyGamma = 90/Fs;
            IirFilterCoefficients coefssGamma = IirFilterDesignFisher.design(srodkowopaswmowy, typFilt3, 2, -3, dolnyGamma, gornyGamma);
            IirFilter filtrgamma = new IirFilter(coefssGamma);
            for(int a=0; a<chanel.length; a++)
            {
                przefiltrowanyGamma[a]=filtrgamma.step(chanel[a]);
                zapis4.println(String.format("%.9f",przefiltrowanyGamma[a]));
            }   
            zapis4.close();
            
        double dolnyBeta = 12/Fs;
        double gornyBeta = 28/Fs;
            IirFilterCoefficients coefssBeta = IirFilterDesignFisher.design(srodkowopaswmowy, typFilt3, 3, -3, dolnyBeta, gornyBeta);
            IirFilter filtrBeta = new IirFilter(coefssBeta);
            for(int a=0; a<chanel.length; a++)
            {
                przefiltrowanyBeta[a]=filtrBeta.step(chanel[a]);
                zapis5.println(String.format("%.9f",przefiltrowanyBeta[a]));
            }    
            zapis5.close();
            
        double dolnyAlfa = 8/Fs;
        double gornyAlfa = 12/Fs;
            IirFilterCoefficients coefssAlfa = IirFilterDesignFisher.design(srodkowopaswmowy, typFilt3, 3, -3, dolnyAlfa, gornyAlfa);
            IirFilter filtrAlfa = new IirFilter(coefssAlfa);
            for(int a=0; a<chanel.length; a++)
            {
                przefiltrowanyAlfa[a]=filtrAlfa.step(chanel[a]);
                zapis6.println(String.format("%.9f",przefiltrowanyAlfa[a]));
            } 
            zapis6.close();
            
       double dolnyTheta = 4/Fs;
        double gornyTheta = 7/Fs;
            IirFilterCoefficients coefssTheta = IirFilterDesignFisher.design(srodkowopaswmowy, typFilt3, 3, -3, dolnyTheta, gornyTheta);
            IirFilter filtrTheta = new IirFilter(coefssTheta);
            for(int a=0; a<chanel.length; a++)
            {
                przefiltrowanyTheta[a]=filtrTheta.step(chanel[a]);
                zapis7.println(String.format("%.9f",przefiltrowanyTheta[a]));
            }
            zapis7.close();
            
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
          //  zapis.println("chanel po fft: "+String.format("%.9f",d));
           // System.out.println("chanel po fft:"+String.format("%.9f",d));
        }
        
        zapis.println("*****************************************");
       //   System.out.println("*******************************************");
        zapis2.println("real part            imag part");
        
        double step = Fs/chanel.length;
          System.out.println("Fs="+Fs+" N="+N+" FS/N="+step);
        double freq = 0;
        double freqs[] =new double[N/2];
        for(int i=0;i<N/2-1;i++)
        {
            re=fft[2*i];
            im=fft[2*i+1];
           zapis2.println(String.format("%.9f",re)+"   "+String.format("%.9f",im));
            magnitude[i]=Math.sqrt(re*re+im*im);
            zapis.println("magnitude: "+String.format("%.9f",magnitude[i]));
            
            
              freqs[i] = freq;
              freq = freq + step;
            zapis.println("freq: "+String.format("%.9f",freqs[i]));
        }
          System.out.println("ile magnit:"+magnitude.length+" ile freq:"+freqs.length);
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
        
        double dominantFreq =  max_index*Fs/N;
        System.out.println("freq:"+String.format("%.9f",dominantFreq)+"  max_index: "+max_index+"  max_magnitude "+max_magnitude);
        
        
        zapis.println("freq:"+String.format("%.9f",dominantFreq)+" max_index"+max_index+" max_magnitude:"+max_magnitude);
      
        FilterPassType lowpass = FilterPassType.lowpass;
        FilterCharacteristicsType typFiltru = FilterCharacteristicsType.butterworth;
        double cutOff=25.71997/Fs;
        double[] tabPoLowPass = new double[chanel.length];
            IirFilterCoefficients coefs = IirFilterDesignFisher.design(lowpass, typFiltru, 1, 0, cutOff, 0);
            IirFilter filtr = new IirFilter(coefs);
            for(int a=0; a<chanel.length/2-1; a++)
            {
                tabPoLowPass[a]=filtr.step(magnitude[a]);
               // zapis4.println(String.format("%.9f",tabPoLowPass[a]));
            }
           
             for (int i = 0; i < coefs.a.length; i++) {
      System.out.println("A[" + i + "] = " + coefs.a[i]); }
   System.out.println();
   for (int i = 0; i < coefs.b.length; i++) {
      System.out.println("B[" + i + "] = " + coefs.b[i]); }
                            System.out.println("Zrobione");
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

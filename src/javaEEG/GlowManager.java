package javaEEG;
import java.util.Random;


//Zarządzanie podświetleniem
public class GlowManager {
    
    //chwilowa tablica częstotliwości do wyświetlenia
    private static double[][][] frequencyArray;
    private static int arrayLength=300;
    private static int numberOfChannels = 19;
    
    private static double time = 0;
    
    public static int getArrayLength(){
        return arrayLength-1;
    }
    
    public static int getNumberofChannels(){
        return numberOfChannels;
    }
    
    public static int getWidth(){
        return (int)Math.ceil(time)+1;
    }
    
    public static double getFrequency(int channel, int value){
        return frequencyArray[channel][value][1];
    }
    
     public static double getTime(int channel, int value){
        return frequencyArray[channel][value][0];
    }
    
    public static void randomizeChannels(){
        frequencyArray = new double[numberOfChannels][arrayLength][2];
        Random generator = new Random();
        time = 0;
        for(int i=0; i<arrayLength; i++){
            for(int j=0; j<numberOfChannels; j++){
                frequencyArray[j][i][0]= time;
                frequencyArray[j][i][1]= generator.nextDouble()*50;
            }
            time+= generator.nextDouble()*0.5;
        }
        
    }
    
    
    //Zwracanie rodzaju fali w zależności od częstotliwości
    public static String getColor(int channel, int value){
        double frequency = frequencyArray[channel][value][1];
        if(frequency>=0.5 && frequency<=3){
            //zielony
            if(BrainView.getButtonState(0))
                return "delta";
        }else if(frequency>=8 && frequency<=13){
            //czerwony
            if(BrainView.getButtonState(1))
                return "alfa";
        }else if(frequency>=12 && frequency<=28){
            //niebieski
            if(BrainView.getButtonState(2))
                return "beta";
        }else if(frequency>=4 && frequency<=7){
            //zolty
            if(BrainView.getButtonState(3))
                return "theta";
        }else if(frequency>=40){
            //rozowo-fioletowy
            if(BrainView.getButtonState(4))
                return "gamma";
        }
        
        return "noColor";
    }
    
    
}

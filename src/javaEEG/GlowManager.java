package javaEEG;
import java.util.Random;


//Zarządzanie podświetleniem
public class GlowManager {
    
    //chwilowa tablica częstotliwości do wyświetlenia
    private static double[][] frequencyArray;
    private static int arrayLength=500;
    private static int numberOfChannels = 19;
    
    public static int getArrayLength(){
        return arrayLength-1;
    }
    
    public static int getNumberofChannels(){
        return numberOfChannels;
    }
    
    public static double getFrequency(int channel, int value){
        return frequencyArray[channel][value];
    }
    
    public static void randomizeChannels(){
        frequencyArray = new double[numberOfChannels][arrayLength];
        Random generator = new Random();
        
        for(int i=0; i<arrayLength; i++){
            for(int j=0; j<numberOfChannels; j++){
                frequencyArray[j][i]= generator.nextDouble()*50;
            }
        }
        
    }
    
    
    //Zwracanie rodzaju fali w zależności od częstotliwości
    public static String getColor(int channel, int value){
        double frequency = frequencyArray[channel][value];
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

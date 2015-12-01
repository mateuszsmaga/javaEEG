package javaEEG;

import java.util.Random;

public class GlowManager {
    //chwilowa tablica częstotliwości do wyświetlenia
    private static double[][] frequencyArray;
    private static int arrayLength=30;
    
    public static int getArrayLength(){
        return arrayLength-1;
    }
    
    public static double getFrequency(int channel, int value){
        return frequencyArray[channel][value];
    }
    
    public static void randomizeChannels(){
        frequencyArray = new double[4][30];
        Random generator = new Random();
        
        for(int i=0; i<30; i++){
            for(int j=0; j<4; j++){
                frequencyArray[j][i]= generator.nextDouble()*50;
            }
        }
        
    }
    
    public static String getColor(int channel, int value){
        double frequency = frequencyArray[channel][value];
        if(frequency>=0.5 && frequency<=3){
            //zielony
            return "green";
        }else if(frequency>=8 && frequency<=13){
            //czerwony
            return "red";
        }else if(frequency>=12 && frequency<=28){
            //niebieski
            return "blue";
        }else if(frequency>=4 && frequency<=7){
            //zolty
            return "yellow";
        }else if(frequency>=40){
            //rozowo-fioletowy
            return "purple";
        }
        
        return "noColor";
    }
    
    
}

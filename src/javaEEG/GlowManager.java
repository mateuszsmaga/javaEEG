package javaEEG;

public class GlowManager {
    //chwilowa tablica częstotliwości do wyświetlenia
    private static double[] frequencyArray = {0.7, 35, 12, 2, 85, 12.15, 0.3,
                            3, 7, 22, 43, 11, 17, 32, 37, 31,
                            1, 18, 21, 22, 23, 25, 11, 37, 41,
                            24, 65, 15, 18, 41, 31, 13, 5, 17,
                            2, 87, 3, 8, 5, 3, 4, 11, 21, 32};
    private static int arrayLength = frequencyArray.length;
    
    public static int getArrayLength(){
        return arrayLength-1;
    }
    
    public static double getFrequency(int value){
        return frequencyArray[value];
    }
    
    public static String getColor(int value){
        double frequency = frequencyArray[value];
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

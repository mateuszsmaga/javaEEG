package javaEEG;
import java.util.HashMap;
import java.util.Random;


//Zarządzanie podświetleniem
public class GlowManager {
    
    //wszystkie nazwy obiektów
    public static String[] objectNames = {"Fp1","Fp2","F7","F3","FZ","F4","F8","T3","C3","CZ","C4","T4","T5","P3","PZ","P4","T6","O1","O2"};
    
    public static String[] channels = {"Kanał 1", "Kanał 2", "Kanał 3", "Kanał 4","Kanał 5", "Kanał 6", "Kanał 7", "Kanał 8",
                                        "Kanał 9", "Kanał 10", "Kanał 11", "Kanał 12", "Kanał 13", "Kanał 14", "Kanał 15", "Kanał 16",
                                        "Kanał 17", "Kanał 18", "Kanał 19"};
    //Mapa przypisanych przez użytkownika kanałów
    public static HashMap<String, String> channelsMap=
            new HashMap<String, String>();
    
    //chwilowa tablica częstotliwości do wyświetlenia
    private static double[][][][] frequencyArray;
    private static int arrayLength=20;
    private static int numberOfChannels = channels.length;
    
    private static double time = 0;
    
    public static int getArrayLength(){
        return arrayLength-1;
    }
    
    private static void fillChannelsMap(){
        for(int i=0; i<channels.length; i++)
            channelsMap.put(channels[i], objectNames[i]);
    }
    
    public static int getNumberofChannels(){
        return numberOfChannels;
    }
    
    public static int getWidth(){
        return (int)Math.ceil(time)+1;
    }
    
    public static double getMaxTime(int channel, int wave){  
        return frequencyArray[channel][getArrayLength()][wave][0];
    }
    
    public static double getFrequency(int channel, int wave, int value){
        return frequencyArray[channel][value][wave][1];
    }
    
     public static double getTime(int channel, int wave, int value){
        return frequencyArray[channel][value][wave][0];
    }
     
    public static void initialize(){
        fillChannelsMap();
        randomizeChannels();
    }
    
    public static void randomizeChannels(){
        frequencyArray = new double[numberOfChannels][arrayLength][4][2];
        Random generator = new Random();
        time = 0;
        for(int i=0; i<arrayLength; i++){
            for(int j=0; j<numberOfChannels; j++){
                for(int k=0; k<4; k++){
                    frequencyArray[j][i][k][0]= time;
                    frequencyArray[j][i][k][1]= generator.nextDouble()*30;
                    time+= generator.nextDouble()*0.05;
                    //System.out.println("i="+i+", j="+j+", k="+k+". Wygenerowany czas="+frequencyArray[j][i][k][0]+". Wygenerowana wartość="+frequencyArray[j][i][k][1]);
                }
            }
            
        }
        
    }
    
    
    //Zwracanie rodzaju fali w zależności od częstotliwości
    public static String getColor(int channel, int wave, int value){
        double frequency = frequencyArray[channel][value][wave][1];
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
        }    
        return "noColor";
    }
    
    
}

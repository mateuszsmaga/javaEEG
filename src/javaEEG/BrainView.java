package javaEEG;

import javaEEG.Swing.SwingView;
import com.jme3.collision.CollisionResults;
import com.jme3.texture.Texture;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import java.util.HashMap;
import java.util.logging.Logger;
import com.jme3.font.BitmapText;
import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import java.awt.Color;
import org.lwjgl.opengl.Display;

public class BrainView extends SimpleApplication {
    
    private static final Logger logger =
            Logger.getLogger(BrainView.class.getName());
    
    //Boolean sprawdzający, czy przeciągamy kursor myszy
    private boolean isDragging = false;
    
    //ChaseCam
    private ChaseCamera chaseCam;

    //zabawy
    private static float bloomIntensity = 1.3f;
    private static BloomFilter bloom;
    private BitmapText hudBloomIntensity;
    
    //testy poprawnosci wyswietlania czestotliwosci
    private BitmapText hudFrequency;
            
    //Zerowa pozycja
    private Quaternion rootNodeLocation;
    //Zaczep na czesci mozgu
    private Node pivot;

    //Mapa materiałów
    private static HashMap<String, Material> materialMap = 
            new HashMap<String, Material>();
    
    //Mapa objektów
    private static HashMap<String, Spatial> objectMap =
            new HashMap<String, Spatial>();
    
    //Mapa kolorow
    private static HashMap<String, ColorRGBA> colorMap = 
            new HashMap<String, ColorRGBA>();
    
    //Mapa tekstur
    private static HashMap<String, String> glowTextureMap=
            new HashMap<String, String>();
    
    private int multiplier=1;
    
    //kontrola czasu
    private long totalTimePulse;
    private long totalTimePlayer;
    private long currentTimeForPulse;
    private long currentTimeForPlayer;
    private long oneSecond = 1000;
    private long bloomChangeTime = 100;
    
    //stop/start
    private boolean stopStart = true;
    private static boolean[] buttonStates = new boolean[5];
    
    //Czy trzeba cos zmienic?
    private static boolean bloomChange=true;
    private static boolean stageChange=true;
    private static boolean enablePlay=false;
    private static boolean waveChange=false;
    
    private float[] recentColor = new float[4];
    
    
    //wybrane miejsce na linii czasu
    private static int chosenStage = 0;
    private static int playingStartCounter = 0;
    private static int lastChange;
    
    //dlugosc tablicy odtwarzania
    private static int arrayLength = 0;
    
    //obrót o kąt
    private float rotation=FastMath.PI/270;
    private float previousVerticalRotation = 0;
    private float previousHorizontalRotation = FastMath.PI/2;
    
    
    //ustawienia
    private static float blurScale = 0.2f;
    
    public static void setBloomScale(float newValue){
        blurScale = newValue;
        bloom.setBlurScale(blurScale);
    }
    
    public static void setBloomIntensity(float bloom){
        bloomIntensity=bloom;
        bloomChange=true;
    }
    
    public static void setChosenStage(int stage){
       chosenStage=stage;
       stageChange=true;
    }
    
    public static void setWaveChange(boolean bool){
        waveChange=bool;
    }
     public static void setStageChange(boolean bool){
        stageChange=bool;
    }
    
    public static void flipButtonState(int value, boolean bool){
        if(value>=0 && value <=5)
                buttonStates[value]=bool; 
    }
    
    public static boolean getButtonState(int value){
         if(value>=0 && value <=5){
             return buttonStates[value];
         }
         return false;
    }
    public static void setEnablePlay(){
        if(enablePlay)
            enablePlay=false;
        else{
            enablePlay=true;
            playingStartCounter = chosenStage;
            arrayLength=GlowManager.getArrayLength();
        }
                    
    }
    
    //Stworzenie nowej aplikacji i jej ustawienia
    public static void main(String[] args) {
        //Ustawienia aplikacji
        AppSettings settings = new AppSettings(true);
        //settings.setFrameRate(60);
        settings.setResolution(640,480);
        settings.setTitle("BrainView");
        settings.setAudioRenderer(null);
       
        //Stworzenie aplikacji i uruchomienie jej
        BrainView app = new BrainView();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start(JmeContext.Type.Display);
        
    }
       
    @Override
    public void simpleInitApp() {
        //UStawienie shaderów
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloom.setBlurScale(blurScale);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
        

        //wyłączenie statystyk
        setDisplayStatView(false);
        setDisplayFps(false);
        
        
        Display.setResizable(true);
       
        //Pobranie czasu startu
        totalTimePlayer = System.currentTimeMillis();
        totalTimePulse = System.currentTimeMillis();
        
        //Tworzenie HUD
        createHUD();
        createFrequencyMessage();
        
        //Stworzenie i skonfigurowanie kamery
        createChaseCam();

        //Utworzenie mapy kolorwo potrzebnej do podswietlania obszarow
        fillColorMap();
        
        //Utworzenie mapy kolorwo potrzebnej do podswietlania obszarow
        fillGlowTetureMap();
        
        //Ustawienie koloru tla
        viewPort.setBackgroundColor(ColorRGBA.Black);

        //Zainicjalizowanie mózgu
        addAllParts();
        
        //Obsługa klawiszy
        addInputs();
     
    }
    
    private void createChaseCam(){
        //Wylaczenie obslugi kamery klawiatura i mysza
        flyCam.setEnabled(false);
                
        chaseCam = new ChaseCamera(cam, rootNode, inputManager);
        chaseCam.setDefaultHorizontalRotation(FastMath.PI/2);
        chaseCam.setDefaultVerticalRotation(0);
        chaseCam.setDefaultDistance(11);
        chaseCam.setRotationSensitivity(10f);
        chaseCam.setMinDistance(10);
        chaseCam.setMaxDistance(20);
        chaseCam.setInvertVerticalAxis(true);
    }
    
    private void createFrequencyMessage(){
        //testy poprawnosci podwietlania obszarow
        hudFrequency = new BitmapText(guiFont, false);          
        //Rozmiar tekstu
        hudFrequency.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        //kolor czcionki
        hudFrequency.setColor(ColorRGBA.White);   
        //Treść tekstu
        hudFrequency.setText(" ");
        //Pozycja tekstu
        hudFrequency.setLocalTranslation(
                0.2f,
                settings.getHeight()-settings.getHeight()*0.01f, 
                0);
        guiNode.attachChild(hudFrequency);
    }
    
    private void pulse(){
        bloom.setBloomIntensity(bloomIntensity);
        bloomIntensity+=multiplier*0.5;
        if(bloomIntensity>=5)
            multiplier=-1;
        else if(bloomIntensity<=1)
            multiplier=1;
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        
        
        
        if(stageChange){
            for(int i=0; i<GlowManager.getNumberofChannels();i++)
                changeMaterial(i, chosenStage);
            stageChange=false;
            lastChange=chosenStage;
        }
        
        
        

        //pulsowanie
        currentTimeForPulse = System.currentTimeMillis();
        if(currentTimeForPulse - totalTimePulse >= bloomChangeTime){
            pulse();
            totalTimePulse=currentTimeForPulse;
        }
        
        //Zmiana materiału co sekundę
        if(enablePlay){
            currentTimeForPlayer = System.currentTimeMillis();
            if(currentTimeForPlayer - totalTimePlayer >= oneSecond){
                totalTimePlayer=currentTimeForPlayer;
                if(playingStartCounter<=arrayLength){
                    for(int i=0; i<GlowManager.channels.length;i++)
                        changeMaterial(i,playingStartCounter);
                    SwingView.setSlider(playingStartCounter);
                    lastChange=playingStartCounter;
                    playingStartCounter++;
                }else{
                    playingStartCounter=0;
                }          
            }    
        }
        
        if(waveChange){
            for(int i=0; i<GlowManager.channels.length;i++)
                changeMaterial(i,lastChange);     
            waveChange=false;
        }

    }
    
    
    public static void fillColorMap(){
        
       
        colorMap.put("delta", new ColorRGBA(0, 1, 0, 1));//green
        colorMap.put("alfa", new ColorRGBA(1, 0.2f, 0.2f, 1));//red
        colorMap.put("beta", new ColorRGBA(0.2f, 0.9725f, 0.9725f, 1));//blue
        colorMap.put("theta", new ColorRGBA(0.9725f, 0.9725f, 0.2f, 1));//yellow
        colorMap.put("gamma", new ColorRGBA(0.792f, 0.117f, 1, 1));//purple
    }
    
    public static void setColor(String wave, Color color){
        float[] compArray=new float[4];
        color.getRGBComponents(compArray);
        ColorRGBA newColor = new ColorRGBA(compArray[0], compArray[1], compArray[2], compArray[3]);
        colorMap.put(wave, newColor);
    }
    
    public static ColorRGBA getColor(String wave){    
        return colorMap.get(wave);
    }
    
    private void fillGlowTetureMap() {
        
        for(int i=0; i<GlowManager.objectNames.length;i++){
            loadGlowTexture(GlowManager.objectNames[i]);
        }

    }
    
    
    
    private void loadGlowTexture(String name){
        glowTextureMap.put(name, "Textures/glowMap/"+name+".png");
    }
    
    
    public void changeMaterial(int channel, int stageNumber){
        String glowColor = GlowManager.getColor(channel,SwingView.getChosenWave(),stageNumber);
        String objectName = GlowManager.channelsMap.get(GlowManager.channels[channel]);
        if(glowColor.equals("noColor")){
            removeGlowMaterial(objectName);
        }else{
            addGlowMaterial(objectName, colorMap.get(glowColor), glowTextureMap.get(objectName));   
        }
    }
    
    
    public static void removeAllGlow(){
        for(int i=0; i<GlowManager.objectNames.length;i++){
            String objectName = GlowManager.objectNames[i];
            Material selectedMaterial = materialMap.get(objectName).clone();
            selectedMaterial.setTexture("GlowMap", null);
            //Zmiana materiału zaznaczonego obiektu
            objectMap.get(objectName).setMaterial(selectedMaterial);
        }
    }

    
    
    private void removeGlowMaterial(String objectName){
         //Nowy materiał
        Material selectedMaterial = materialMap.get(objectName).clone();
        selectedMaterial.setTexture("GlowMap", null);
        //Zmiana materiału zaznaczonego obiektu
        objectMap.get(objectName).setMaterial(selectedMaterial);
    }
    
    
    //dodanie podświetlonego obszaru
    private void addGlowMaterial(String objectName, ColorRGBA glowColor, String glowMapTexture){
        
        //Nowy materiał
        Material selectedMaterial = materialMap.get(objectName).clone();
        selectedMaterial.setColor("GlowColor", glowColor);
        selectedMaterial.setTexture("GlowMap", assetManager.loadTexture(glowMapTexture));

        //Zmiana materiału zaznaczonego obiektu
        objectMap.get(objectName).setMaterial(selectedMaterial);
   
    }
    
    //Obracanie przy pomocy strzałek kierunkowych
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String binding, float value, float tpf) {
            
            if(previousHorizontalRotation!=chaseCam.getHorizontalRotation() &&
                    previousVerticalRotation!=chaseCam.getVerticalRotation()){
                previousHorizontalRotation=chaseCam.getHorizontalRotation();
                previousVerticalRotation=chaseCam.getVerticalRotation();
            }
                
            if (binding.equals("leftRot")) {
                chaseCam.setDefaultHorizontalRotation(previousHorizontalRotation+rotation);
                previousHorizontalRotation+=rotation;
            } 
            if (binding.equals("rightRot")) {
                chaseCam.setDefaultHorizontalRotation(previousHorizontalRotation-rotation);
                previousHorizontalRotation-=rotation;
            } 
            if (binding.equals("upRot")) {
                chaseCam.setDefaultVerticalRotation(previousVerticalRotation+rotation);
                previousVerticalRotation+=rotation;
            } 
            if (binding.equals("downRot")) {
                chaseCam.setDefaultVerticalRotation(previousVerticalRotation-rotation);
                previousVerticalRotation-=rotation;
            } 
        }
    };


    private ActionListener actionListener = new ActionListener() {
        public void onAction(String binding, boolean keyPressed, float tpf) {

            if (binding.equals("Space") && keyPressed) {
                chaseCam.setDefaultHorizontalRotation(FastMath.PI/2);
                chaseCam.setDefaultVerticalRotation(0);
                previousHorizontalRotation=FastMath.PI/2;
                previousVerticalRotation=0;
            }   
   
            if (binding.equals("RightMouseButtonClick") && keyPressed) {
                //Pobranie listy wyników
                CollisionResults results = mouseClickResults();
 
                //Zamiana materiału najbliższego obiektu
                if(results.size() > 0){
                    //Wybierz najbliższy obiekt
                    Spatial target = results.getClosestCollision().getGeometry();
                    
                    //Testy
                    String name = target.getName();                 
                    System.out.println("Nazwa obiektu = "+name+" = koniec nazwy");
                    
                    //Obsługa po zaznaczeniu
                    //Nowy materiał
                    Material selectMaterial = materialMap.get(target.getName()).clone();
                    selectMaterial.setColor("GlowColor", ColorRGBA.randomColor());
                    
                    //Zmiana materiału zaznaczonego obiektu
                    target.setMaterial(selectMaterial);
                }
            }
            
             if (binding.equals("LeftMouseButtonClick")) {
                //Pobranie listy wyników
                CollisionResults results = mouseClickResults();
                
                //Zamiana materiału najbliższego obiektu
                if(results.size() > 0){
                    //Wybierz najbliższy obiekt
                    Spatial target = results.getClosestCollision().getGeometry();
                    //Obsługa po zaznaczeniu
                    //Znalezienie oryginalnego materiału zaznaczonego obiektu
                    Material originalMaterial = materialMap.get(target.getName());
                    //Zmiana materiału zaznaczonego obiektu
                    target.setMaterial(originalMaterial);
                }
            }
        }
    };
    
    
    //Obsluga klawiszy
    public void addInputs(){
        
        /*
        //Zaznaczanie PPM
        inputManager.addMapping("RightMouseButtonClick", 
                new MouseButtonTrigger(mouseInput.BUTTON_RIGHT));
        inputManager.addListener(actionListener, "RightMouseButtonClick");
        //Odznaczanie LPM
        inputManager.addMapping("LeftMouseButtonClick", 
                new MouseButtonTrigger(mouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "LeftMouseButtonClick");
        */
        //Resetowanie pozycji
        inputManager.addMapping("Space", new KeyTrigger(keyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Space");
        
        inputManager.addMapping("Pause", new KeyTrigger(keyInput.KEY_PAUSE));
        inputManager.addListener(actionListener, "Pause");
        
        inputManager.addMapping("leftRot", new KeyTrigger(keyInput.KEY_LEFT));
        inputManager.addListener(analogListener, "leftRot");
        inputManager.addMapping("rightRot", new KeyTrigger(keyInput.KEY_RIGHT));
        inputManager.addListener(analogListener, "rightRot");
        inputManager.addMapping("downRot", new KeyTrigger(keyInput.KEY_DOWN));
        inputManager.addListener(analogListener, "downRot");
        inputManager.addMapping("upRot", new KeyTrigger(keyInput.KEY_UP));
        inputManager.addListener(analogListener, "upRot");
    }
    
     
    //Dodanie wszystkich elementow mozgu
    public void addAllParts(){
        //Stworzenie nowego zaczepu
        pivot = new Node("pivot");
        
        for(int i=0; i<GlowManager.objectNames.length;i++){
            loadPartElements(GlowManager.objectNames[i]);
        }
        
        addNewBrainPart("bottom", "Models/full/bottom.j3o", "Textures/colorMap/bottom.png");
        
        //Podczepienie zaczepu do rootNode
        rootNode.attachChild(pivot); 
        rootNodeLocation = rootNode.getLocalRotation().clone();
    }
    
    private void loadPartElements(String name){
         addNewBrainPart(name, "Models/full/"+name+".j3o", "Textures/colorMap/"+name+".png");
    }
    
    //Ustawienie materialu dla poszczegolnych czesci mozgu
    public void addNewBrainPart(String brainPartName, String modelDirectory, String textureDirectory){
        //Załadowanie modelu
        Spatial brainPart = assetManager.loadModel(modelDirectory);
        //Stworzenie nowego materiału
        Material brainPartMaterial = new Material(assetManager,
                "Shaders/glowMapColor.j3md");
        //Załadowanie tekstury
        Texture brainPartTexture = assetManager.loadTexture(textureDirectory);
        //UStawienie tekstury materiału
        brainPartMaterial.setTexture("ColorMap", brainPartTexture);
        //Ustawienie materiału obiektu
        brainPart.setMaterial(brainPartMaterial);
        //Dodanie gotowego materiału do mapy materiałów
        objectMap.put(brainPartName,brainPart);
        materialMap.put(brainPartName,brainPartMaterial);
        //Podczepienie obiektu pod zaczep
        pivot.attachChild(brainPart);
    }
    
    //Dodanie nowego tekstu HUD
    private void newHUDText(String hudString, float width, float height){
        //Stworzenie nowego elementu hud
        BitmapText hudText = new BitmapText(guiFont, false);          
        //Rozmiar tekstu
        hudText.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        //kolor czcionki
        hudText.setColor(ColorRGBA.White);   
        //Treść tekstu
        hudText.setText(hudString);
        //Pozycja tekstu
        hudText.setLocalTranslation(
                width,
                settings.getHeight()-settings.getHeight()*height, 
                0);
        guiNode.attachChild(hudText);
    }
    
    //Tworzenie HUD
    public void createHUD(){
        /*
        newHUDText("Alfa - 8-13Hz - kolor czerwony", 0.2f, 0.25f);
        newHUDText("Beta - 12-28Hz - kolor niebieski", 0.2f, 0.28f);
        newHUDText("Gamma - 40+Hz - kolor fioletowy/rózowy", 0.2f, 0.31f);
        newHUDText("Delta - 0.5-3Hz - kolor zielony", 0.2f, 0.34f);
        newHUDText("Theta - 4-7Hz - kolor żółty", 0.2f, 0.37f);
        */
        
    }
    
    //Funkcja zwracająca  kliknięte obiekty
    private CollisionResults mouseClickResults(){
        //Resetowanie listy wyników
        CollisionResults results = new CollisionResults();
        //Konwersja kliknięcia na pozycję w przestrzeni 3D
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y) , 0f);
        Vector3f dir = cam.getWorldCoordinates(
                new Vector2f(click2d.x, click2d.y) , 1f).subtractLocal(click3d).normalizeLocal();
        //Wypuszczenie promienia z klikniętego miejsca do przodu
        Ray ray = new Ray(click3d, dir);
        //Zbierz wszystkie obiekty, które zostały przecięte przez promień
        //do listy wyników Results
        rootNode.collideWith(ray, results);
        //Zwróć wyniki
        return results;
    }
    
    @Override
    public void simpleRender(RenderManager rm) {
        
    }

    
    
}

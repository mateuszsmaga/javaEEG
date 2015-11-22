package javaEEG;

import com.jme3.collision.CollisionResults;
import com.jme3.texture.Texture;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
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
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

public class BrainView extends SimpleApplication {
    
    private static final Logger logger =
            Logger.getLogger(BrainView.class.getName());
    
    //Boolean sprawdzający, czy przeciągamy kursor myszy
    private boolean isDragging = false;
    
    //ChaseCam
    ChaseCamera chaseCam;

    //zabawy
    public float bloomIntensity = 2.0f;
    BloomFilter bloom;
    BitmapText hudBloomIntensity;
    
    //testy poprawnosci wyswietlania czestotliwosci
    BitmapText hudFrequency;
            
    //Zerowa pozycja
    Quaternion rootNodeLocation;
    //Zaczep na czesci mozgu
    public Node pivot;

    //Mapa materiałów
    public HashMap<String, Material> materialMap = 
            new HashMap<String, Material>();
    
    //Mapa objektów
    public HashMap<String, Spatial> objectMap =
            new HashMap<String, Spatial>();
    
    //kontrola czasu
    private long totalTime;
    private long currentTime;
    private long oneSecond = 1000;
    
    //stop/start
    boolean stopStart = true;
    
    //chwilowa tablica częstotliwości do wyświetlenia
    public double[] frequencyArray = {0.7, 35, 12, 2, 85, 12.15, 0.3,
                            3, 7, 22, 43, 11, 17, 32, 37, 31,
                            1, 18, 21, 22, 23, 25, 11, 37, 41,
                            24, 65, 15, 18, 41, 31, 13, 5, 17,
                            2, 87, 3, 8, 5, 3, 4, 11, 21, 32};
    public int arrayLength = frequencyArray.length;
    private int arrayCounter = 0;

    //Stworzenie nowej aplikacji i jej ustawienia
    public static void main(String[] args) {
        //Ustawienia aplikacji
        AppSettings settings = new AppSettings(true);
        settings.setFrameRate(60);
        settings.setResolution(1200, 320);
        settings.setTitle("EEG");
        settings.setAudioRenderer(null);
        
        //Stworzenie aplikacji i uruchomienie jej
        BrainView app = new BrainView();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start();
        
    }
    
    
    
    
    @Override
    public void simpleInitApp() {
        //UStawienie shaderów
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloom.setBlurScale(3.0f);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);

        //wyłączenie statystyk
        setDisplayStatView(false);
       
        //Pobranie czasu startu
        totalTime = System.currentTimeMillis();
        
        //Tworzenie HUD
        createHUD();
        
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
                settings.getHeight()-settings.getHeight()*0.19f, 
                0);
        guiNode.attachChild(hudFrequency);

        
        //Wylaczenie obslugi kamery klawiatura i mysza
        flyCam.setEnabled(false);
                
        chaseCam = new ChaseCamera(cam, rootNode, inputManager);
        chaseCam.setDefaultHorizontalRotation(FastMath.PI/2);
        chaseCam.setDefaultVerticalRotation(0);
        chaseCam.setDefaultDistance(8);
        chaseCam.setRotationSensitivity(10f);
        chaseCam.setMinDistance(10);
        chaseCam.setMaxDistance(20);
        chaseCam.setInvertVerticalAxis(true);
        
        //Ustawienie koloru tla
        viewPort.setBackgroundColor(ColorRGBA.Black);

        //Zainicjalizowanie mózgu
        addAllParts();
        
        //Obsługa klawiszy
        addInputs();
     
    }
    
    @Override
    public void simpleUpdate(float tpf) {   
        if(stopStart){
            //Zmiana materiału co sekundę.
            currentTime = System.currentTimeMillis();
            if(currentTime - totalTime >= oneSecond){
                //tutaj zmiana materiali
                changeMaterial(frequencyArray[arrayCounter]);
                arrayCounter++;
                if(arrayCounter==arrayLength){
                    arrayCounter=0;
                }
                totalTime=currentTime;
            }
        }
        
        
        
    }
    
    
    public void changeMaterial(double frequency){
        if(frequency>=0.5 && frequency<=3){
            //zielony
            addGlowMaterial("left_half-geom-0", new ColorRGBA(0.247f, 0.749f, 0.247f, 1), "Textures/glowMaps/glowMap_lh.png");
            addGlowMaterial("right_half-geom-0", new ColorRGBA(0.247f,  0.749f, 0.247f, 1), "Textures/glowMaps/glowMap_rh.png");
        }else if(frequency>=8 && frequency<=13){
            //czerwony
            addGlowMaterial("left_half-geom-0", new ColorRGBA(1, 0.2f, 0.2f, 1), "Textures/glowMaps/glowMap_lh.png");
            addGlowMaterial("right_half-geom-0", new ColorRGBA(1, 0.2f, 0.2f, 1), "Textures/glowMaps/glowMap_rh.png");
        }else if(frequency>=12 && frequency<=28){
            //niebieski
            addGlowMaterial("left_half-geom-0", new ColorRGBA(0.2f, 0.9725f, 0.9725f, 1), "Textures/glowMaps/glowMap_lh.png");
            addGlowMaterial("right_half-geom-0", new ColorRGBA(0.2f, 0.9725f, 0.9725f, 1), "Textures/glowMaps/glowMap_rh.png");
        }else if(frequency>=4 && frequency<=7){
            //zolty
            addGlowMaterial("left_half-geom-0", new ColorRGBA(0.9725f, 0.9725f, 0.2f, 1), "Textures/glowMaps/glowMap_lh.png");
            addGlowMaterial("right_half-geom-0", new ColorRGBA(0.9725f, 0.9725f, 0.2f, 1), "Textures/glowMaps/glowMap_rh.png");
        }else if(frequency>=40){
            //rozowo-fioletowy
            addGlowMaterial("left_half-geom-0", new ColorRGBA(0.792f, 0.117f, 1, 1), "Textures/glowMaps/glowMap_lh.png");
            addGlowMaterial("right_half-geom-0", new ColorRGBA(0.792f, 0.117f, 1, 1), "Textures/glowMaps/glowMap_rh.png");
        }
        
        hudFrequency.setText("Wyswietlana czestotliwosc: "+frequency+"Hz.");
    }
    
    
    
    //dodanie podświetlonego obszaru
    public void addGlowMaterial(String objectName, ColorRGBA glowColor, String glowMapTexture){
        
        //Nowy materiał
        Material selectedMaterial = materialMap.get(objectName).clone();
        selectedMaterial.setColor("GlowColor", glowColor);
        selectedMaterial.setTexture("GlowMap", assetManager.loadTexture(glowMapTexture));

        //Zmiana materiału zaznaczonego obiektu
        objectMap.get(objectName).setMaterial(selectedMaterial);
   
    }
    


    private ActionListener actionListener = new ActionListener() {
        public void onAction(String binding, boolean keyPressed, float tpf) {

            if (binding.equals("Pause") && keyPressed) {
                if(stopStart)
                    stopStart=false;
                    else
                        stopStart=true;
            } 
            
            if (binding.equals("Space") && keyPressed) {
                chaseCam.setDefaultHorizontalRotation(FastMath.PI/2);
                chaseCam.setDefaultVerticalRotation(0);
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
    }
    
     
    //Dodanie wszystkich elementow mozgu
    public void addAllParts(){
        //Stworzenie nowego zaczepu
        pivot = new Node("pivot");
        
        
        //Dodawanie czesci mozgu
        /*
        addNewBrainPart("Models/old_brain/brain_left.j3o", "Textures/brain_left.png");
        addNewBrainPart("Models/old_brain/brain_right.j3o", "Textures/brain_right.png");
        addNewBrainPart("Models/old_brain/back_left.j3o", "Textures/back_left.png");
        addNewBrainPart("Models/old_brain/back_right.j3o", "Textures/back_right.png");
        addNewBrainPart("Models/old_brain/center.j3o", "Textures/center.png");
        */
        
        addNewBrainPart("Models/new_brain/left_half.j3o", "Textures/lh_pial.png");
        addNewBrainPart("Models/new_brain/right_half.j3o", "Textures/rh_pial.png");
        
        //Podczepienie zaczepu do rootNode
        rootNode.attachChild(pivot); 
        rootNodeLocation = rootNode.getLocalRotation().clone();
        
    }
    
    //Ustawienie materialu dla poszczegolnych czesci mozgu
    public void addNewBrainPart(String modelDirectory, String textureDirectory){
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
        objectMap.put(brainPart.getName(), brainPart);
        materialMap.put(brainPart.getName(),brainPartMaterial);
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
        newHUDText("LPM = przywroc oryginalny material", 0.2f, 0.01f);
        newHUDText("PPM = swiecacy material", 0.2f, 0.04f);
        newHUDText("LPM + ruch myszy = obrot", 0.2f, 0.07f);
        newHUDText("Spacja = zresetowanie pozycji", 0.2f, 0.10f);
        newHUDText("Pause/Break = zatrzymanie symulacji", 0.2f, 0.13f);
        
        newHUDText("Alfa - 8-13Hz - kolor czerwony", 0.2f, 0.25f);
        newHUDText("Beta - 12-28Hz - kolor niebieski", 0.2f, 0.28f);
        newHUDText("Gamma - 40+Hz - kolor fioletowy/rózowy", 0.2f, 0.31f);
        newHUDText("Delta - 0.5-3Hz - kolor zielony", 0.2f, 0.34f);
        newHUDText("Theta - 4-7Hz - kolor żółty", 0.2f, 0.37f);
        
        
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

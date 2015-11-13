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

public class Main extends SimpleApplication {
    
    private static final Logger logger =
            Logger.getLogger(Main.class.getName());
    
    //Boolean sprawdzający, czy przeciągamy kursor myszy
    public boolean isDragging = false;
    //Koordynaty myszy
    Vector2f mouseCoords = Vector2f.ZERO;
    //Poprzednie koordynaty myszy
    Vector2f oldCoords = Vector2f.ZERO;
    
    //ChaseCam
    ChaseCamera chaseCam;

    //zabawy
    float bloomIntensity = 2.0f;
    BloomFilter bloom;
    BitmapText hudBloomIntensity;
            
    //Zerowa pozycja
    Quaternion rootNodeLocation;
    //Zaczep na czesci mozgu
    public Node pivot;

    //Mapa materiałów
    public HashMap<String, Material> materialMap = 
            new HashMap<String, Material>();
    

    //Stworzenie nowej aplikacji i jej ustawienia
    public static void main(String[] args) {
        //Ustawienia aplikacji
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        settings.setTitle("EEG");
        settings.setAudioRenderer(null);
        settings.setFrameRate(60);
        //Stworzenie aplikacji i uruchomienie jej
        Main app = new Main();
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

        setDisplayStatView(false);
       
        //Tworzenie HUD
        createHUD();
        
        //Wylaczenie obslugi kamery klawiatura i mysza
        flyCam.setEnabled(false);
        
        //Ustawienia kamery obracającej się za mózgiem
        Vector3f location = cam.getLocation();
        System.out.print("x="+location.x+", y="+location.y+", z="+location.z);
        
        Quaternion rotation = cam.getRotation();
        System.out.print("W="+rotation.getW()+", X="+rotation.getX()+", Y="+rotation.getY()+", Z="+rotation.getZ());
        cam.setRotation(new Quaternion(0f, 1f, 0f, 0f));
        cam.setLocation(new Vector3f(10f, 0f, 0f));
        
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
   
        bloom.setBloomIntensity(bloomIntensity);
        hudBloomIntensity.setText("Bloom - natezenie: "+bloomIntensity);
        bloomIntensity+=0.07f;
        if(bloomIntensity>=10)
            bloomIntensity=0;

    }

    private ActionListener actionListener = new ActionListener() {
        public void onAction(String binding, boolean keyPressed, float tpf) {

            if (binding.equals("Drag")){
                isDragging = keyPressed;
                //Zresetuj pozycję myszy w porzednim przeciągnieciu
                mouseCoords = new Vector2f(inputManager.getCursorPosition());
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
        //Zaznaczanie PPM
        inputManager.addMapping("RightMouseButtonClick", 
                new MouseButtonTrigger(mouseInput.BUTTON_RIGHT));
        inputManager.addListener(actionListener, "RightMouseButtonClick");
        //Odznaczanie LPM
        inputManager.addMapping("LeftMouseButtonClick", 
                new MouseButtonTrigger(mouseInput.BUTTON_LEFT));
        inputManager.addListener(actionListener, "LeftMouseButtonClick");
        //Resetowanie pozycji
        inputManager.addMapping("Space", new KeyTrigger(keyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "Space");
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
                "Common/MatDefs/Misc/Unshaded.j3md");
        //Załadowanie tekstury
        Texture brainPartTexture = assetManager.loadTexture(textureDirectory);
        //UStawienie tekstury materiału
        brainPartMaterial.setTexture("ColorMap", brainPartTexture);
        //Ustawienie materiału obiektu
        brainPart.setMaterial(brainPartMaterial);
        //Dodanie gotowego materiału do mapy materiałów
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
        //Stworzenie nowego elementu hud
        hudBloomIntensity = new BitmapText(guiFont, false);          
        //Rozmiar tekstu
        hudBloomIntensity.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        //kolor czcionki
        hudBloomIntensity.setColor(ColorRGBA.White);   
        //Treść tekstu
        hudBloomIntensity.setText("Bloom - natezenie:");
        //Pozycja tekstu
        hudBloomIntensity.setLocalTranslation(
                0.2f,
                settings.getHeight()-settings.getHeight()*0.13f, 
                0);
        guiNode.attachChild(hudBloomIntensity);
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

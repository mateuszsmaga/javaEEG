package javaEEG.Swing;

import javaEEG.Swing.SettingsDialog;
import javaEEG.Swing.ProgressBar;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.util.JmeFormatter;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javaEEG.Swing.SwingView.createCanvas;
import static javaEEG.Swing.SwingView.startApp;
import static javaEEG.Widok.CreateSelectFile;
import static javaEEG.Widok.ReadFile;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javaEEG.BrainView;
import javaEEG.GlowManager;

import javax.swing.*;

public class SwingView extends JFrame{

    private static JmeCanvasContext context;
    private static Canvas canvas;
    private static Application app;
    private static JFrame frame;
    private static JSlider numberSlider;
    private static AppSettings settings;
    private static final String appClass = "javaEEG.BrainView";
    private static JButton iconButton;
    private static JList channelList;
    private static boolean playStop = false;
    private static GraphPanel graphPanel;
    private static List<Double> score = new ArrayList();
    private static List<Double> time = new ArrayList();
    private JPanel mainPanel = new JPanel();
    public static ProgressBar progressBar;
    private JToolBar toolbar;
    
    
    
    private static Color backgroundColor = Color.lightGray.brighter();
    private static Color mainBackgroundColor = new Color(192, 192, 192);
    
    private static String chosenChannel = "";
    
    public SwingView() throws HeadlessException {
        GlowManager.randomizeChannels();
        setScore(0);
        BrainView.fillColorMap();
        createFrame();    
    }
    
    public JPanel getMainPanel() {
        return mainPanel;
    }
    
    
    //Wypełnienie tablicy przed stworzeniem nowego wykresu
    private static void setScore(int channel){
        int length = GlowManager.getArrayLength();
        score.clear();
        time.clear();
        for(int i=0; i<=length; i++){
            score.add(GlowManager.getFrequency(channel,i));
            time.add(GlowManager.getTime(channel, i));
            System.out.print(i+" element listy: "+GlowManager.getTime(channel, i)+","+GlowManager.getFrequency(channel, i)+".\n");
        }
    }
    
    //Zmiana pozycji slidera
    public static void setSlider(int sliderState){
        numberSlider.setValue(sliderState);
    }
    

    private void createFrame(){
        frame = new JFrame("EEG");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosed(WindowEvent e) {
                app.stop();
                System.exit(0);
            }
        });
    
        frame.addComponentListener(new ComponentListener() {

                    public void componentResized(ComponentEvent e) {
                        System.out.println("H="+frame.getHeight()+" W="+frame.getWidth());
                    }

                    public void componentMoved(ComponentEvent e) {
                        
                    }

                    public void componentShown(ComponentEvent e) {
                        
                    }

                    public void componentHidden(ComponentEvent e) {
                        
                    }
                });    
        
       createBottomScreen();
       createToolBar();
    }
    
    //Stworzenie całego paska na dole (play/stop, wykres, zmiana kanałów, linia czasu)
    public static void createBottomScreen(){
        
        numberSlider = new JSlider(JSlider.HORIZONTAL, 0, GlowManager.getArrayLength(), 0);   
        int length=GlowManager.getArrayLength()/30;
        length=(int)Math.pow(10, Math.ceil(Math.log10(length)));
        numberSlider.setMajorTickSpacing(length);
        numberSlider.setMinorTickSpacing(1);
        numberSlider.setPaintTicks(true);
        numberSlider.setPaintLabels(true);
        numberSlider.setBorder(BorderFactory.createTitledBorder("Linia czasu."));
        numberSlider.setBackground(mainBackgroundColor);
        
        
        numberSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                BrainView.setChosenStage(numberSlider.getValue());
            }
        });

        iconButton = new JButton(new ImageIcon("assets/Icons/play.png"));
        iconButton.setBackground(backgroundColor);
        iconButton.setFocusPainted(false);
        iconButton.setToolTipText("Odtwórz");
        iconButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
               BrainView.setEnablePlay();
               if(!playStop){
                   iconButton.setIcon(new ImageIcon("assets/Icons/pause.png"));
                    iconButton.setToolTipText("Pauza");
                   playStop=true;
               }else{
                   iconButton.setIcon(new ImageIcon("assets/Icons/play.png"));
                    iconButton.setToolTipText("Odtwórz");
                   playStop=false;
               }
               
            }
        });
        
        
        
        String[] channels = {"Channel 1", "Channel 2", "Channel 3", "Channel 4"};
        
        
        channelList = new JList(channels);
        channelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        channelList.setLayoutOrientation(JList.VERTICAL);
        channelList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String name = (String) channelList.getSelectedValue();
                    if(name.equals("Channel 1")){
                        setScore(0);
                    }else if(name.equals("Channel 2")){
                        setScore(1);
                    }else if(name.equals("Channel 3")){
                        setScore(2);
                    }else if(name.equals("Channel 4")){
                        setScore(3);
                    }
                    graphPanel.setScores(score);
                    graphPanel.setBorder(BorderFactory.createTitledBorder("Wykres - "+name));
                }
            }
        });
        
        JScrollPane listScroller = new JScrollPane(channelList);
        listScroller.setPreferredSize(new Dimension(83,0));
        
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.add(iconButton, BorderLayout.BEFORE_LINE_BEGINS);
        playerPanel.add(numberSlider, BorderLayout.CENTER);
        
        JPanel chartPanel = new JPanel(new BorderLayout());
        graphPanel = new GraphPanel(time, score);
        graphPanel.setPreferredSize(new Dimension(300, 150));
        graphPanel.setBorder(BorderFactory.createTitledBorder("Wykres aktualnie wybranego kanału"));
        graphPanel.setBackground(mainBackgroundColor);
        chartPanel.add(listScroller, BorderLayout.BEFORE_LINE_BEGINS);
        chartPanel.add(graphPanel, BorderLayout.CENTER);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(playerPanel, BorderLayout.SOUTH);
        southPanel.add(chartPanel, BorderLayout.CENTER);
        
        
        
        frame.add(southPanel, BorderLayout.SOUTH);
        
    }
       
    public static void CreateSelectFile(JFrame frame) throws FileNotFoundException{
        String userDir = System.getProperty("user.home");
        JFileChooser fc = new JFileChooser(userDir+"/Desktop");
  
        fc.setFileFilter(new FileNameExtensionFilter("Pliki tekstowe", "txt"));     // tylko rozszerzenie txt
        int returnVal = fc.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            ReadFile(fc);
        } 
        else if(returnVal == JFileChooser.CANCEL_OPTION)
        {
           
        }
    }
    
    public static void createCanvas(String appClass){
        AppSettings settings = new AppSettings(true);
        settings.setResolution(937, 350);
        //settings.setFrameRate(60);
        
        try{
            Class<? extends Application> clazz = (Class<? extends Application>) Class.forName(appClass);
            app = clazz.newInstance();
        }catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }catch (InstantiationException ex){
            ex.printStackTrace();
        }catch (IllegalAccessException ex){
            ex.printStackTrace();
        }

        app.setPauseOnLostFocus(false);
        app.setSettings(settings);
        app.createCanvas();
        app.startCanvas();

        context = (JmeCanvasContext) app.getContext();
        canvas = context.getCanvas();
        canvas.setSize(settings.getWidth(), settings.getHeight());
    }

    public static void startApp(){
        app.startCanvas();
        app.enqueue(new Callable<Void>(){
            public Void call(){
                if (app instanceof SimpleApplication){
                    SimpleApplication simpleApp = (SimpleApplication) app;
                    simpleApp.getFlyByCamera().setDragToRotate(true);
                }
                return null;
            }
        });
        
    }
    
    
    private static void resetSize(){
        settings.setResolution(frame.getWidth(), frame.getHeight());
        app.setSettings(settings);
        app.restart();
    }

    
    //Stworzenie paska narzędziowego
    private void createToolBar() {
        
        toolbar = new JToolBar(null, JToolBar.HORIZONTAL);
        toolbar.setBackground(mainBackgroundColor);
        toolbar.setFloatable(false);
        toolbar.addSeparator();

        ImageIcon exitIcon = new ImageIcon("assets/Icons/close.png");
        ImageIcon settingsIcon = new ImageIcon("assets/Icons/settings.png");
        ImageIcon openIcon = new ImageIcon("assets/Icons/open.png");
        
        JButton openButton = new JButton(openIcon);
        openButton.setBackground(backgroundColor);
        openButton.setFocusPainted(false);
        toolbar.add(openButton);
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    createProgressBar();
                    CreateSelectFile(frame);    // wywołanie FileChooser
                    
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SwingView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        openButton.setToolTipText("Otwórz plik");
        
        JButton settingsButton = new JButton(settingsIcon);
        settingsButton.setFocusPainted(false);
        settingsButton.setBackground(backgroundColor);
        toolbar.add(settingsButton);
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showSettingsDialog();
            }
        });
        settingsButton.setToolTipText("Ustawienia");
        
        /*
        JButton testButton = new JButton(new ImageIcon("assets/Icons/background.png"));
        testButton.setFocusPainted(false);
        toolbar.add(testButton);
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                createProgressBar();
            }
        });
        testButton.setToolTipText("Test");
        */
        
        
        //Podział lewa/prawa toolbara
        toolbar.add(Box.createHorizontalGlue());

        addNewToolBarButton("Włącz/wyłącz wyświetlanie fali delta.","DELTA","delta",0);
        addNewToolBarButton("Włącz/wyłącz wyświetlanie fali ALFA.","ALFA","alfa",1);
        addNewToolBarButton("Włącz/wyłącz wyświetlanie fali beta.","BETA","beta",2);
        addNewToolBarButton("Włącz/wyłącz wyświetlanie fali theta.","THETA","theta",3);
        addNewToolBarButton("Włącz/wyłącz wyświetlanie fali gamma.","GAMMA","gamma",4);
        
        JButton exitButton = new JButton(exitIcon);
        exitButton.setFocusPainted(false);
        exitButton.setBackground(backgroundColor);
        toolbar.add(exitButton);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
                 
        exitButton.setToolTipText("Zamknij");
        
        toolbar.addSeparator();
       
        
        frame.add(toolbar, BorderLayout.NORTH);        
    }
    
    
    //Nowy guzik na Toolbarze
    private void addNewToolBarButton(String toolTip, String waveText, String waveColor, int number){
        JPanel panel = new JPanel();
        panel.setMaximumSize(new Dimension(60, 60));
        panel.setLayout(new GridLayout(2, 1));
        panel.add(createWaveButton(toolTip,waveText,number));
        panel.add(createColorButton(waveColor));
        toolbar.add(panel);
        toolbar.addSeparator();
    }
    
    //Button odpowiedzialny za włączenie/wyłączenie wyświetlania danej fali
    private JToggleButton createWaveButton(String toolTip, String text, final int flipSwitch){
        final JToggleButton button = new JToggleButton(text, true);
        button.setToolTipText(toolTip);
        button.setFocusPainted(false);
        button.setBackground(backgroundColor);
        BrainView.flipButtonState(flipSwitch, true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(button.isSelected()){
                    BrainView.flipButtonState(flipSwitch, true);
                    BrainView.setWaveChange(true);
                }else{
                    BrainView.flipButtonState(flipSwitch, false);
                    BrainView.setWaveChange(true);
                }
            }
        });
        return button;
    }
    
    //Button odpowiedzialny za zmianę koloru podświetlania danej fali
    private JButton createColorButton(final String wave){
        final JButton button = new JButton(new ImageIcon("assets/Icons/background.png"));
        button.setToolTipText("Wybierz kolor.");
        button.setBackground(backgroundColor);
        ColorRGBA rgba = BrainView.getColor(wave);
        Color color = new Color(rgba.getRed(), rgba.getGreen(), rgba.getBlue(), rgba.getAlpha());
        button.setBackground(color);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color initialBackground = button.getBackground();
                Color background = JColorChooser.showDialog(null, "JColorChooser Sample", initialBackground);
                if (background != null) {
                  button.setBackground(background);
                  BrainView.setColor(wave, background);
                  BrainView.setWaveChange(true);
                }

            }
        });
        button.setFocusPainted(false);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
            }
        });
        return button;
    }
    
    //Uruchomienie okna opcji
    private void showSettingsDialog(){
        SettingsDialog settingsDialog = new SettingsDialog(this);
        settingsDialog.pack();
        settingsDialog.setLocationRelativeTo(frame);
        settingsDialog.setVisible(true);
    }
    
    
    //Uruchomienie paska postępu
    public void createProgressBar(){
        progressBar = new ProgressBar(this);
        progressBar.pack();
        progressBar.setLocationRelativeTo(frame);
        progressBar.setVisible(false); 
    }
    
    public static void main(String[] args){
        JmeFormatter formatter = new JmeFormatter();

        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);

        Logger.getLogger("").removeHandler(Logger.getLogger("").getHandlers()[0]);
        Logger.getLogger("").addHandler(consoleHandler);
        
        createCanvas(appClass);

        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                JPopupMenu.setDefaultLightWeightPopupEnabled(false);
                SwingView swingFrame = new SwingView();
                frame.add(canvas, BorderLayout.CENTER);
                frame.pack();
                startApp();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
            }
        });
    }

}
package javaEEG.Swing;

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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javaEEG.BrainView;
import javaEEG.GlowManager;

import javax.swing.*;
import javax.swing.border.Border;

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
    
    private static JLabel playTimeLabel = new JLabel("Tutaj odliczamy1");
    
   
    public static Color backgroundColor = Color.lightGray.brighter();
    private static Color mainBackgroundColor = new Color(192, 192, 192);
    
    private static String nameToShow = GlowManager.channels[0];
    private static String waveToShow = "alfa";
    
    public static Border blackline = BorderFactory.createLineBorder(Color.BLACK);
    
    //zarządzanie licznikiem czasu
    public static double lastMaxTime=0;
    public static double lastPlayTime = 0;
    
    //przeliczony czas
    public static String lastMaxTimeString;
    public static String lastPlayTimeString;
    
    //Ktory wykres zostal wybrany?
    private static int lastChannel = 0;
    
    //oznaczenia kolorów na pasku
    public static JLabel alfaLabel = new JLabel("   ");
    public static JLabel betaLabel = new JLabel("   ");
    public static JLabel deltaLabel = new JLabel("   ");
    public static JLabel thetaLabel = new JLabel("   ");
    
    //Wybrana fala do wyświetlania
    private static int chosenWave = 0; 
    private static int chosenScore = 0;
    
    private static void convertTimes(){
        long maxTime = (long)(lastMaxTime*1000);
        long playTime =  (long)(lastPlayTime*1000);
        lastMaxTimeString=(new SimpleDateFormat("mm:ss:SSSS")).format(new Date(maxTime));
        lastPlayTimeString=(new SimpleDateFormat("mm:ss:SSSS")).format(new Date(playTime));
    }
    
    public static int getChosenWave(){
        return chosenWave;
    }
    
    public SwingView() throws HeadlessException {
        GlowManager.initialize();
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
        lastChannel = channel;
        for(int i=0; i<=length; i++){
            score.add(GlowManager.getFrequency(channel,chosenWave, i));
            time.add(GlowManager.getTime(channel, chosenWave, i));
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
        
        Image image = new ImageIcon("assets/Icons/testIcon.png").getImage();
        frame.setIconImage(image);
        
       createBottomScreen();
       createToolBar();
    }
    
    //Zmiana licznika czasu
    private static void changeTimeLabel(){
        convertTimes();
        playTimeLabel.setText("<html><font color=black>"+lastPlayTimeString+
                "</font><font color=red>/</font><font color=black>"+
                lastMaxTimeString+"</font></html>");
    }
    
    
    //Stworzenie całego paska na dole (play/stop, wykres, zmiana kanałów, linia czasu)
    public static void createBottomScreen(){
        
        numberSlider = new JSlider(JSlider.HORIZONTAL, 0, GlowManager.getArrayLength(), 0);   
        int length=GlowManager.getArrayLength()/30;
        length=(int)Math.pow(10, Math.ceil(Math.log10(length)));
        numberSlider.setMajorTickSpacing(length);
        numberSlider.setBorder(BorderFactory.createTitledBorder(blackline, "Linia czasu."));
        numberSlider.setBackground(mainBackgroundColor);
        numberSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                BrainView.setChosenStage(numberSlider.getValue());
                lastPlayTime=GlowManager.getTime(lastChannel, chosenWave, numberSlider.getValue());
                changeTimeLabel();
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
        
        //tutaj Jlabel playTimeLabel!!!1
        lastMaxTime=GlowManager.getMaxTime(0, chosenWave);
        changeTimeLabel();
        playTimeLabel.setBackground(Color.LIGHT_GRAY);
        playTimeLabel.setOpaque(true);
        
       
        
        
        playTimeLabel.setBorder(BorderFactory.createTitledBorder(blackline, "Czas"));
        channelList = new JList(GlowManager.channels);
        channelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        channelList.setLayoutOrientation(JList.VERTICAL);
        channelList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String name = (String) channelList.getSelectedValue();

                    for(int i = 0; i<GlowManager.channels.length; i++){
                        if(name.equals(GlowManager.channels[i])){
                            setScore(i);
                            nameToShow=name;
                            chosenScore = i;
                            lastMaxTime=GlowManager.getMaxTime(i, chosenWave);
                            lastPlayTime=GlowManager.getTime(lastChannel, chosenWave, numberSlider.getValue());
                            changeTimeLabel();
                            break;
                        }
                    }
                    graphPanel.setScores(score);
                    graphPanel.setBorder(BorderFactory.createTitledBorder(blackline, "Wykres - "+name+". Fala - "+waveToShow));
                }
            }
        });
        
        JScrollPane listScroller = new JScrollPane(channelList);
        listScroller.setPreferredSize(new Dimension(83,0));
        
        //odtwarzacz
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.add(iconButton, BorderLayout.BEFORE_LINE_BEGINS);
        playerPanel.add(numberSlider, BorderLayout.CENTER);
        playerPanel.add(playTimeLabel, BorderLayout.AFTER_LINE_ENDS);
        
        
        
        
        //wykresy
        JPanel chartPanel = new JPanel(new BorderLayout());
        graphPanel = new GraphPanel(time, score);
        graphPanel.setPreferredSize(new Dimension(300, 150));
        graphPanel.setBorder(BorderFactory.createTitledBorder(blackline, "Wykres - "+nameToShow+". Fala - "+waveToShow));
        graphPanel.setBackground(mainBackgroundColor);
        JPanel waveType = new JPanel(new GridLayout(4, 1));
        createWaveButtons(waveType);
        chartPanel.add(listScroller, BorderLayout.BEFORE_LINE_BEGINS);
        chartPanel.add(graphPanel, BorderLayout.CENTER);
        chartPanel.add(waveType, BorderLayout.AFTER_LINE_ENDS);
        
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(playerPanel, BorderLayout.SOUTH);
        southPanel.add(chartPanel, BorderLayout.CENTER);
        
        
        
        
        frame.add(southPanel, BorderLayout.SOUTH);
        
    }
    
    private static void createWaveButtons(JPanel panel){
        panel.setBorder( BorderFactory.createEmptyBorder(8, 3, 3, 3));
        panel.setBackground(Color.gray.brighter());
        final JToggleButton alfaButton = createWaveButton("α");
        final JToggleButton betaButton = createWaveButton("β");
        final JToggleButton deltaButton = createWaveButton("δ");
        final JToggleButton thetaButton = createWaveButton("θ");
        
        alfaButton.setSelected(true);
        alfaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    if(alfaButton.isSelected()){
                        betaButton.setSelected(false);
                        deltaButton.setSelected(false);
                        thetaButton.setSelected(false);
                        setNewGraph(0);
                    }
                }
        });
        betaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    if(betaButton.isSelected()){
                        alfaButton.setSelected(false);
                        deltaButton.setSelected(false);
                        thetaButton.setSelected(false);
                        setNewGraph(1);
                    }
                }
        });
        deltaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    if(deltaButton.isSelected()){
                        betaButton.setSelected(false);
                        alfaButton.setSelected(false);
                        thetaButton.setSelected(false);
                        setNewGraph(2);
                    }
                }
        });
        thetaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    if(thetaButton.isSelected()){
                        betaButton.setSelected(false);
                        deltaButton.setSelected(false);
                        alfaButton.setSelected(false);
                        setNewGraph(3);
                    }
                }
        });
        
        panel.add(alfaButton);
        panel.add(betaButton);
        panel.add(deltaButton);
        panel.add(thetaButton);
    }
    
    private static void setNewGraph(int value){
        chosenWave=value;
        setScore(chosenScore);
        if(value==0)waveToShow="alfa";
        if(value==1)waveToShow="beta";
        if(value==2)waveToShow="delta";
        if(value==3)waveToShow="theta";
        graphPanel.setScores(score);
        lastMaxTime=GlowManager.getMaxTime(chosenScore, chosenWave);
        lastPlayTime=GlowManager.getTime(lastChannel, chosenWave, numberSlider.getValue());
        graphPanel.setBorder(BorderFactory.createTitledBorder(blackline, "Wykres - "+nameToShow+". Fala - "+waveToShow));
        changeTimeLabel();  
    }
    
    private static JToggleButton createWaveButton(String text){
        JToggleButton button = new JToggleButton(text);
        button.setToolTipText("Wybór wyświetlanej fali.");
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        return button;
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
        settings.setFrameRate(60);
        
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
        toolbar.setOpaque(true);
        toolbar.addSeparator();

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
        
        
        
        JButton testButton = new JButton(new ImageIcon("assets/Icons/brain.png"));
        testButton.setFocusPainted(false);
        toolbar.add(testButton);
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showChannelsDialog();
            }
    
        });
        testButton.setToolTipText("Test");
        
        
        
        //Podział lewa/prawa toolbara
        toolbar.add(Box.createHorizontalGlue());

        
        addNewToolBarButton("Włącz/wyłącz wyświetlanie fali ALFA.","ALFA",1);
        addNewColorLabel(alfaLabel, "ALFA");
        addNewToolBarButton("Włącz/wyłącz wyświetlanie fali beta.","BETA",2);
        addNewColorLabel(betaLabel, "BETA");
        addNewToolBarButton("Włącz/wyłącz wyświetlanie fali delta.","DELTA",0);
        addNewColorLabel(deltaLabel, "DELTA");
        addNewToolBarButton("Włącz/wyłącz wyświetlanie fali theta.","THETA",3);
        addNewColorLabel(thetaLabel, "THETA");
        

        toolbar.addSeparator();
       
        
        frame.add(toolbar, BorderLayout.NORTH);        
    }
    
    private void addNewColorLabel(JLabel label, String waveText){
        ColorRGBA rgba = BrainView.getColor(waveText.toLowerCase());
        Color color = new Color(rgba.getRed(), rgba.getGreen(), rgba.getBlue(), rgba.getAlpha());
        label.setBackground(color);
        label.setMinimumSize(new Dimension(8, 48));
        label.setMaximumSize(new Dimension(48, 48));
        label.setBorder(blackline);
        label.setOpaque(true);
        toolbar.add(label);
    }
    
    
    //Nowy guzik na Toolbarze
    private void addNewToolBarButton(String toolTip, String waveText, int number){
         
        toolbar.addSeparator();
        toolbar.add(createWaveButton(toolTip,waveText,number)); 
    }
    
    //Button odpowiedzialny za włączenie/wyłączenie wyświetlania danej fali
    private JToggleButton createWaveButton(String toolTip, final String text, final int flipSwitch){
        final JToggleButton button = new JToggleButton(text, true);
        button.setToolTipText(toolTip);
        button.setFocusPainted(false);
        BrainView.flipButtonState(flipSwitch, true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(button.isSelected()){
                    BrainView.flipButtonState(flipSwitch, true);
                    BrainView.setWaveChange(true);
                }else{
                    button.setBackground(backgroundColor);
                    BrainView.flipButtonState(flipSwitch, false);
                    BrainView.setWaveChange(true);
                }
            }
        });
        button.setMaximumSize(new Dimension(120, 60));
        button.setMinimumSize(new Dimension(120, 60));
        return button;
    }
    
    
    //Uruchomienie okna opcji
    private void showSettingsDialog(){
        SettingsDialog settingsDialog = new SettingsDialog(this);
        settingsDialog.pack();
        settingsDialog.setLocationRelativeTo(frame);
        settingsDialog.setVisible(true);
    }
    
    private void showChannelsDialog() {
       ChannelsDialog channelsDialog = new ChannelsDialog(this);
       channelsDialog.pack();
       channelsDialog.setLocationRelativeTo(frame);
       channelsDialog.setVisible(true);
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
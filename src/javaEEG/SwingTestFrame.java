package javaEEG;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.util.JmeFormatter;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javaEEG.SwingView.createCanvas;
import static javaEEG.SwingView.createSliders;
import static javaEEG.SwingView.startApp;
import static javaEEG.Widok.CreateSelectFile;
import static javaEEG.Widok.ReadFile;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SwingTestFrame extends JFrame{

    private static JmeCanvasContext context;
    private static Canvas canvas;
    private static Application app;
    private static JFrame frame;
    private static JSlider bloomSlider, numberSlider;
    private static AppSettings settings;
    private static final String appClass = "javaEEG.BrainView";
    private static JButton iconButton;
    
    private static boolean playStop = false;

    public SwingTestFrame() throws HeadlessException {
        createFrame();
    }
    
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
        
        /*
        frame.addComponentListener(new ComponentListener() {

                    public void componentResized(ComponentEvent e) {
                        resetSize();
                    }

                    public void componentMoved(ComponentEvent e) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    public void componentShown(ComponentEvent e) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    public void componentHidden(ComponentEvent e) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                });    
        */
       createPlayer();
       createToolBar();
    }
    
    public static void createPlayer(){
        
        numberSlider = new JSlider(JSlider.HORIZONTAL, 0, GlowManager.getArrayLength()-1, 0);
        numberSlider.setMajorTickSpacing(1);
        numberSlider.setMinorTickSpacing(1);
        numberSlider.setPaintTicks(true);
        numberSlider.setPaintLabels(true);
        numberSlider.setBorder(BorderFactory.createTitledBorder("Linia czasu."));
        
        
        
        numberSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                BrainView.setChosenStage(numberSlider.getValue());
            }
        });
        
        ImageIcon playIcon = new ImageIcon("assets/Icons/play.png");
        iconButton = new JButton(playIcon);
        iconButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
               BrainView.setEnablePlay();
               if(!playStop){
                   iconButton.setIcon(new ImageIcon("assets/Icons/pause.png"));
                   playStop=true;
               }else{
                   iconButton.setIcon(new ImageIcon("assets/Icons/play.png"));
                   playStop=false;
               }
               
            }
        });
        iconButton.setToolTipText("Odtwarzaj");
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(iconButton, BorderLayout.BEFORE_LINE_BEGINS);
        bottomPanel.add(numberSlider, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        
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
        settings.setResolution(1280, 720);
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

    
    private void createToolBar() {
        
        JToolBar toolbar = new JToolBar();

        ImageIcon exitIcon = new ImageIcon("assets/Icons/close.png");
        ImageIcon settingsIcon = new ImageIcon("assets/Icons/settings.png");
        ImageIcon openIcon = new ImageIcon("assets/Icons/open.png");
        
        JButton openButton = new JButton(openIcon);
        toolbar.add(openButton);
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    CreateSelectFile(frame);    // wywołanie FileChooser
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SwingView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        openButton.setToolTipText("Otwórz plik");
        
        JButton settingsButton = new JButton(settingsIcon);
        toolbar.add(settingsButton);
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                showSettingsDialog();
            }
        });
        settingsButton.setToolTipText("Ustawienia");



        JButton exitButton = new JButton(exitIcon);
        toolbar.add(exitButton);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        exitButton.setToolTipText("Zamknij");

        frame.add(toolbar, BorderLayout.NORTH);        
    }
    
    private void showSettingsDialog(){
        SettingsDialog settingsDialog = new SettingsDialog(this);
        settingsDialog.pack();
        settingsDialog.setLocationRelativeTo(frame);
        settingsDialog.setVisible(true);
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
                SwingTestFrame swingFrame = new SwingTestFrame();
                frame.add(canvas, BorderLayout.CENTER);
                frame.pack();
                startApp();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                
            }
        });
    }

}
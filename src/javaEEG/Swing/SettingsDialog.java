package javaEEG.Swing;
import com.jme3.math.ColorRGBA;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javaEEG.BrainView;
import static javaEEG.Swing.SwingView.backgroundColor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


//Okno ustawień
class SettingsDialog extends JDialog {
    
    private Container pane = getContentPane();
    Hashtable labelTable = new Hashtable();

    public SettingsDialog(Frame parent) {
        super(parent);

        initUI();
    }

    private void initUI() {
        getRootPane().setBorder( BorderFactory.createEmptyBorder(10, 10, 10, 10));
        createLayout();

        setModalityType(ModalityType.APPLICATION_MODAL);

        setTitle("Ustawienia");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
    }

    private void createLayout() {
        GridLayout layout = new GridLayout(5, 2);
        layout.setVgap(10);
        layout.setHgap(10);
        pane.setLayout(layout);
        addNewElement("Intensywność podświetlenia");
        final JSlider slider = new JSlider(0, 20);
        
        createNewLabelTable(5);
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                float value = slider.getValue()/10.0f;
                BrainView.setBloomScale(value);
                System.out.println("Value changed to:"+value);
            }
        });
        pane.add(slider);
        
        createColorOption("alfa");
        createColorOption("beta");
        createColorOption("delta");
        createColorOption("theta");

        pack();
    }
    
    private void createColorOption(String text){
        text=text.toUpperCase();
        addNewElement("Kolor podświetlenia dla fali "+text);
        text=text.toLowerCase();
        pane.add(createColorButton(text));
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
                  if(wave.equals("alfa")){
                      SwingView.alfaLabel.setBackground(background);
                  }else if(wave.equals("beta")){
                      SwingView.betaLabel.setBackground(background);
                  }else if(wave.equals("delta")){
                      SwingView.deltaLabel.setBackground(background);
                  }else if(wave.equals("theta")){
                      SwingView.thetaLabel.setBackground(background);
                  }
                      
                }

            }
        });
        button.setFocusPainted(false);

        return button;
    }
    
    //Tworzenie tabeli podziałek
    private void createNewLabelTable(int numberOfTicks){
        labelTable.clear();
        for(int i=0; i<numberOfTicks; i++){
            float text = i*0.5f;
            String label = String.valueOf(text);
            labelTable.put(new Integer(i*5), new JLabel(label));
        }
    }
    
    private void createNewSlider(){
        
    }
    
    
    private void addNewElement(String text){
        JLabel label = new JLabel(text);
        pane.add(label);
        
    }
}
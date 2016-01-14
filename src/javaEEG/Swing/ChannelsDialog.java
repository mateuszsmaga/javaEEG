package javaEEG.Swing;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javaEEG.BrainView;
import javaEEG.GlowManager;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


//Okno ustawień
class ChannelsDialog extends JDialog {
    
    
    List choiceCheck =  new ArrayList();
    
    private int objectNumber = 0;

    public ChannelsDialog(Frame parent) {
        super(parent);

        initUI();
    }

    private void initUI() {
        createLayout();

        setModalityType(ModalityType.APPLICATION_MODAL);

        setTitle("Przyporządkowanie elektrod");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
    }
    
    
    //sprawdxdzenie czy robić przerwę
    private boolean checkConditions(int number){
        return number==0 || number ==2 || number==4 || number ==20 || number==22 || number==24;
    }

    private void createLayout() {

        final Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        setSize(new Dimension(300, 100));
        
        //Tworzenie listy przypisania kanałów
        
        JPanel lists = new JPanel();
        GridLayout gridLayout = new GridLayout(5, 5);
        gridLayout.setVgap(5);
        gridLayout.setHgap(5);
        
        lists.setLayout(gridLayout);
        lists.setBorder( BorderFactory.createEmptyBorder(0,0,50,0));
        objectNumber = 0;
        for(int i = 0; i<25; i++){
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2, 1));
            if(checkConditions(i)){
                JLabel label = new JLabel("");
                panel.add(label);
            }else{
                JLabel label = new JLabel(GlowManager.objectNames[objectNumber]);
                panel.add(label);

                final int changedSelectionIndex = objectNumber;

                JComboBox list = new JComboBox(GlowManager.channels);
                list.setBackground(SwingView.backgroundColor);
                list.insertItemAt("", 0);
                list.setSelectedIndex(0);
                list.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JComboBox combo = (JComboBox) e.getSource();
                        String selected = (String)combo.getSelectedItem();
                        if(!selected.equals("")){
                            GlowManager.channelsMap.put(selected, GlowManager.objectNames[changedSelectionIndex]);
                            choiceCheck.add(selected);
                        }
                                          
                    }
                });
                panel.add(list);
                objectNumber++;
            }
            lists.add(panel);
        }
            
        //Dodanie obrazu podglądowego po prawej stronie
        JLabel divisionPicture = new JLabel(
                new ImageIcon("assets/Icons/electrodes.jpg"));
        divisionPicture.setBackground(Color.white);

        
        //Przycisk zatwierdzający ustawienia kanałów
        JButton okButton = new JButton("ZATWIERDŹ");
        okButton.setBackground(SwingView.backgroundColor);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                
                boolean duplicates=false;
                for (int j=0;j<choiceCheck.size();j++)
                  for (int k=j+1;k<choiceCheck.size();k++){
                      String kString = (String)choiceCheck.get(k);
                      String jString = (String)choiceCheck.get(j);
                      if (k!=j && kString.equals(jString)){
                          duplicates=true;
                          choiceCheck.remove(j);
                      }         
                  }
                    
                
                if(!duplicates && choiceCheck.size()==GlowManager.getNumberofChannels()){
                    for(Object objname:GlowManager.channelsMap.keySet()) {
                        System.out.println(objname+"="+GlowManager.channelsMap.get(objname));
                    }
                    BrainView.removeAllGlow();
                    BrainView.setWaveChange(true);
                    dispose();
                }else if(!duplicates && !(choiceCheck.size()==GlowManager.getNumberofChannels())){
                    JOptionPane.showMessageDialog(pane, "Nie wybrano wszystkich kanałów. "+
                            "Prosimy wybrać brakujące kanały.",
                            "Błąd", JOptionPane.ERROR_MESSAGE);
      
                }else{
                    JOptionPane.showMessageDialog(pane, "Podano wielokrotnie ten sam kanał. "
                            + "Prosimy ponownie wybrać kanały", 
                            "Błąd", JOptionPane.ERROR_MESSAGE);
                }
                

                objectNumber = 0;

                
            }
        });
        
        JPanel leftSide = new JPanel(new BorderLayout());
        leftSide.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        leftSide.add(lists, BorderLayout.CENTER);
        leftSide.add(okButton, BorderLayout.SOUTH);
        
        pane.add(leftSide, BorderLayout.CENTER);
        pane.add(divisionPicture, BorderLayout.EAST);


        pack();
    }
}
package javaEEG.Swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.border.Border;


//Pasek postępu
public class ProgressBar extends JDialog {
  
  private JProgressBar progressBar;
    
  public ProgressBar(Frame parent){
      super(parent);
      initUI();
  }
  
  
  //tworzenie paska postępu
  private void initUI() {
        Container content = getContentPane();
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        content.add(progressBar, BorderLayout.CENTER);
        setSize(300, 100);
        setVisible(false);
        
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
        setAlwaysOnTop(true);
  }
  
  //Zmiana postępu = nowa nazwa, nowa wartość od 0-100
  public void setNewValues(String newTitle, int newValue){
      if(newValue>=0 && newValue<=100){
        progressBar.setValue(newValue);
         Border border = BorderFactory.createTitledBorder(newTitle);
        progressBar.setBorder(border);
      }
      
  }
  
  //zamknięcie paska postępu
  public void closeProgressBar(){
     dispose();
  }
  

}

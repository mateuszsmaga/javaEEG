package javaEEG;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.border.Border;

public class ProgressBar extends JDialog {
  
  private JProgressBar progressBar;
    
  public ProgressBar(Frame parent){
      super(parent);
      initUI();
  }
  
  private void initUI() {
        Container content = getContentPane();
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        content.add(progressBar, BorderLayout.CENTER);
        setSize(300, 100);
        setVisible(true);
        
        setModalityType(ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
        setAlwaysOnTop(true);
  }
  
  public void setNewValues(String newTitle, int newValue){
      progressBar.setValue(newValue);
      Border border = BorderFactory.createTitledBorder(newTitle);
      progressBar.setBorder(border);
  }
  
  public void closeProgressBar(){
     dispose();
  }
  

}

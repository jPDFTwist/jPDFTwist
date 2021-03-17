package jpdftweak.tabs.input.password;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;

/**
 *
 * @author Vasilis Naskos
 */
public class PdfUnlockDialog extends JDialog {

    private static JPasswordField passwordField;
    private static JLabel passLabel;
    private static JCheckBox removeRestrictionsCheck, overwriteCheck;
    private static JTextField outpathField;
    private static JButton browseBtn, okBtn, cancelBtn;
    private static JFileChooser pdfChooser;
    
    private static PasswordObject unlockInfo;
    private static String lockedFilePath;
    private static boolean ok = false;

    public static PasswordObject askForPassword(JFrame parent, String filename, boolean autoRestrictionsOverwrite, boolean autoRestrictionsNew) {
        PdfUnlockDialog unlockDialog = new PdfUnlockDialog(parent, filename);
        
        if(!(autoRestrictionsOverwrite || autoRestrictionsNew))
            unlockDialog.setVisible(true);
        
        unlockInfo = new PasswordObject();
        lockedFilePath = filename;
        
        if(autoRestrictionsOverwrite) {
            overwriteCheck.setSelected(true);
        } else if(autoRestrictionsNew) {
            String extension = filename.substring(filename.lastIndexOf("."));
            String path = filename.substring(0, filename.lastIndexOf("."));
            String outputPath = path + "-unlocked" + extension;
            
            int i = 1;
            while(new File(outputPath).exists()) {
                outputPath = path + "-unlocked("+i+")" + extension;
                i++;
            }
                
            outpathField.setText(outputPath);
            overwriteCheck.setSelected(false);
        }

        if (!ok && !autoRestrictionsOverwrite && !autoRestrictionsNew)
            unlockInfo.setSuccessfullyUnlocked(false);
        
        unlockInfo.setPassword(passwordField.getPassword());

        if (removeRestrictionsCheck.isSelected() || autoRestrictionsOverwrite || autoRestrictionsNew)
            setUnlockedFilePath();

        return unlockInfo;
    }
    
    public PdfUnlockDialog(JFrame parent, String lockedFile) {
        super(parent, "Password Protected PDF", true);
        setLayout(new FormLayout(
                "$lcgap, 20dlu, $lcgap, f:p:g, 50dlu, 25dlu, 25dlu, $lcgap",
                "$lgap, f:p, f:p, f:p, f:p, f:p, $lgap, f:p, $lgap"));

        initializeDialogInterface(lockedFile);
        setListeners();
        positionComponents();
        
        overwriteCheckItemStateChanged();
        
        getRootPane().setDefaultButton(okBtn);
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeDialogInterface(String lockedFile) {
        pdfChooser = new JFileChooser();
        pdfChooser.setMultiSelectionEnabled(false);
        pdfChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        outpathField = new JTextField(lockedFile);
        
        okBtn = new JButton("OK");
        cancelBtn = new JButton("Cancel");
        browseBtn = new JButton("...");
        
        passLabel = new JLabel("Owner password");
        passwordField = new JPasswordField(60);

        removeRestrictionsCheck = new JCheckBox("Remove Restrictions");
        
        overwriteCheck = new JCheckBox("Overwrite existing PDF");
        overwriteCheck.setSelected(true);
        overwriteCheck.setEnabled(false);
    }
    
    private void setListeners() {
        okBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                okActionPerformed();
            }
        });

        cancelBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cancelActionPerformed();
            }
        });

        browseBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                browseActionPerformed();
            }
        });

        removeRestrictionsCheck.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                removeRestrictionsItemStateChanged();
            }
        });

        overwriteCheck.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                overwriteCheckItemStateChanged();
            }
        });
    }
    
    private void okActionPerformed() {
        ok = true;
        this.dispose();
    }
    
    private void cancelActionPerformed() {
        this.dispose();
    }
    
    private void browseActionPerformed() {
        if (pdfChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
            return;
        
        String filename = pdfChooser.getSelectedFile().getAbsolutePath();
      //  System.out.println("filename:"+filename);
        outpathField.setText(filename);
    }
    
    private void removeRestrictionsItemStateChanged() {
        String labelText = "Owner password";
        
        if (removeRestrictionsCheck.isSelected())
            labelText += " or User password";
        
        passLabel.setText(labelText);

        overwriteCheck.setEnabled(removeRestrictionsCheck.isSelected());
        outpathField.setEnabled(!overwriteCheck.isSelected() && overwriteCheck.isEnabled());
        browseBtn.setEnabled(!overwriteCheck.isSelected() && overwriteCheck.isEnabled());
    }
    
    private void overwriteCheckItemStateChanged() {
        outpathField.setEnabled(!overwriteCheck.isSelected() && overwriteCheck.isEnabled());
        browseBtn.setEnabled(!overwriteCheck.isSelected() && overwriteCheck.isEnabled());
    }
    
    private void positionComponents() {
        CellConstraints cc = new CellConstraints();
        
        add(passLabel, cc.xyw(2, 2, 6));
        add(passwordField, cc.xyw(2, 3, 6));
        add(removeRestrictionsCheck, cc.xyw(2, 4, 6));
        add(overwriteCheck, cc.xyw(2, 5, 6));
        add(outpathField, cc.xyw(2, 6, 5));
        add(browseBtn, cc.xy(7, 6));
        add(okBtn, cc.xy(5, 8));
        add(cancelBtn, cc.xyw(6, 8, 2));
    }
    
    //TODO catch exception and add file to error list
    private static void setUnlockedFilePath() {
        try {
            String unlockedPath = removeRestrictions();
           // System.out.println("unlocked path:"+unlockedPath);
            unlockInfo.setUnlockedFilePath(unlockedPath);
        } catch (Exception ex) {
            unlockInfo.setSuccessfullyUnlocked(false);
            Logger.getLogger(PdfUnlockDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //TODO test the doc==null state
    private static String removeRestrictions() throws IOException {
        String unlockedPath = lockedFilePath;
       
        if (!overwriteCheck.isSelected())
            unlockedPath = outpathField.getText();
            
        boolean isFileWritten = saveUnlockedFile(unlockedPath);
        
        if(!isFileWritten)
            unlockedPath = null;
        
        return unlockedPath;
    }
    
    private static boolean saveUnlockedFile(String unlockedPath) throws IOException {
        PDDocument doc = PDDocument.load(new File(lockedFilePath));
               
        try {
        	
            doc.setAllSecurityToBeRemoved(true);
            
            doc.save(unlockedPath);
        } catch (Exception ex) {
            Logger.getLogger(PdfUnlockDialog.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            doc.close();
        }
        
        return true;
    }

}

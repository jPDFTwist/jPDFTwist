package jpdftweak.tabs.watermark.optionpanels;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jpdftweak.tabs.watermark.WatermarkStyle;

/**
 *
 * @author Vasilis Naskos
 */
public class WatermarkImagePanel extends JPanel {
    
    private JLabel panelTitle;
    private JTextField imagePathField;
    private JButton browseButton;
    private JFileChooser fc;
    private JLabel pageLabel;
    private JSpinner pdfPageSpinner;
    private JPanel pagePanel; 
    
    private WatermarkStyle style;
    
    private final CellConstraints cc;
    
    public WatermarkImagePanel() {
        super(new FormLayout("$lcgap, f:p, 105dlu, f:p, $lcgap",
                "p, $lcgap, p, $lcgap, p"));

        cc = new CellConstraints();
        generateUserInterface();
    }

    private void generateUserInterface() {
        initializeComponents();
        positionComponents();
    }
    
    private void initializeComponents() {
        setBorder(javax.swing.BorderFactory.createTitledBorder("Image"));
        
        fc = new JFileChooser();
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        panelTitle = new JLabel("Image");
        imagePathField = new JTextField();
        browseButton = new JButton("Browse");
        
        browseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                browseActionListener();
            }
        });
        
        imagePathField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                style.setImagePath(imagePathField.getText());
            }
        });
        
        pagePanel = new JPanel(new FormLayout("f:p, $lcgap, f:p", "p"));
        pageLabel = new JLabel("Page:");
        pdfPageSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        pdfPageSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int page = Integer.parseInt(pdfPageSpinner.getValue().toString());
                style.setPdfPage(page);
            }
        });
    }
    
    private void browseActionListener() {
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            imagePathField.setText(file.getAbsolutePath());
        }
    }
    
    private void positionComponents() {
        this.add(panelTitle, cc.xy(2, 1));
        this.add(browseButton, cc.xy(4, 1));
        this.add(imagePathField, cc.xyw(2, 3, 3));
        
        pagePanel.add(pageLabel, cc.xy(1, 1));
        pagePanel.add(pdfPageSpinner, cc.xy(3, 1));
        
        this.add(pagePanel, cc.xyw(2, 5, 3));
    }
    
    public void setStyle(WatermarkStyle style) {
        this.style = style;

        imagePathField.setText(style.getImagePath());
        pdfPageSpinner.setValue(style.getPdfPage());
    }
    
}

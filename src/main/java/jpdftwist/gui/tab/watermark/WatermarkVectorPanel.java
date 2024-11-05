package jpdftwist.gui.tab.watermark;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import jpdftwist.core.PdfReaderManager;
import jpdftwist.core.watermark.WatermarkStyle;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WatermarkVectorPanel extends JPanel {

    private JLabel panelTitle;
    private JTextField vectorPathField;
    private JButton browseButton;
    private JFileChooser fc;
    private JLabel pageLabel;
    private JSpinner pdfPageSpinner;
    private JPanel pagePanel;

    private WatermarkStyle style;
    private final CellConstraints CC;
    
    public WatermarkVectorPanel() {
        super(new FormLayout("$lcgap, f:p, 105dlu, f:p, $lcgap", "p, $lcgap, p, $lcgap, p"));

        CC = new CellConstraints();
        generateUserInterface();
    }

    private void generateUserInterface() {
        initializeComponents();
        positionComponents();
    }

    private void initializeComponents() {
        setBorder(javax.swing.BorderFactory.createTitledBorder(" Vector only / Raster only / mixed Vector+ Raster "));

        fc = new JFileChooser();
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        FileNameExtensionFilter[] filters = {
                new FileNameExtensionFilter("PDF Files(*.pdf)", "PDF"),
                new FileNameExtensionFilter("All supported File types", "PDF")};

        for (FileNameExtensionFilter filter : filters) {
            fc.setFileFilter(filter);
        }

        panelTitle = new JLabel("Ex: *.pdf                      ");
        vectorPathField = new JTextField();
        browseButton = new JButton("Browse");

        browseButton.addActionListener(e -> browseActionListener());
        
        vectorPathField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                style.setVectorPath(vectorPathField.getText());
            }
        });

        pagePanel = new JPanel(new FormLayout("f:p, $lcgap, f:p", "p"));
        pageLabel = new JLabel("All pages are used sequentially:");
        pdfPageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        pdfPageSpinner.setEnabled(false);
        pdfPageSpinner.addChangeListener(e -> {
            try {
                int page = Integer.parseInt(pdfPageSpinner.getValue().toString());
                style.setPdfPage(page);
            } catch (Exception ex) {
                Logger.getLogger(WatermarkVectorPanel.class.getName()).log(Level.SEVERE, "Ex107", ex);
            }
        });
    }

    private void browseActionListener() {
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            vectorPathField.setText(file.getAbsolutePath());
            pdfPageSpinner.setValue(1);
        }
        else {
            JOptionPane.showMessageDialog(null, "Please select a valid file ...", "Info", JOptionPane.WARNING_MESSAGE);
          	return;
          }
    }
    
    private void positionComponents() {
        this.add(panelTitle, CC.xy(2, 1));
        this.add(browseButton, CC.xy(4, 1));
        this.add(vectorPathField, CC.xyw(2, 3, 3));

        pagePanel.add(pageLabel, CC.xy(1, 1));
        pagePanel.add(pdfPageSpinner, CC.xy(3, 1));

        this.add(pagePanel, CC.xyw(2, 5, 3));
    }

    public void setStyle(WatermarkStyle style) {
        this.style = style;

        vectorPathField.setText(style.getVectorPath());
        pdfPageSpinner.setValue(style.getPdfPage()); 
    }
}

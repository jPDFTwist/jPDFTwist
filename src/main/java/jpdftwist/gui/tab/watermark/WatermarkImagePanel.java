package jpdftwist.gui.tab.watermark;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jpdftwist.core.watermark.WatermarkStyle;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WatermarkImagePanel extends JPanel {

    private JLabel panelTitle;
    private JTextField imagePathField;
    private JButton browseButton;
    private JFileChooser fc;
    private JLabel pageLabel;
    private JSpinner pdfPageSpinner;
    private JPanel pagePanel;

    private WatermarkStyle style;

    private final CellConstraints CC;

    public WatermarkImagePanel() {
        super(new FormLayout("$lcgap, f:p, 105dlu, f:p, $lcgap", "p, $lcgap, p, $lcgap, p"));

        CC = new CellConstraints();
        generateUserInterface();
    }

    private void generateUserInterface() {
        initializeComponents();
        positionComponents();
    }

    private void initializeComponents() {
        setBorder(javax.swing.BorderFactory.createTitledBorder("Raster"));

        fc = new JFileChooser();
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        FileNameExtensionFilter[] filters = {
            new FileNameExtensionFilter("PDF Files(*.pdf)", "PDF"),
            new FileNameExtensionFilter("JPEG Image(*.jpg, *.jpeg)", "JPG", "JPEG"),
            new FileNameExtensionFilter("JPEG2000 Image(*.jp2, *.j2k, *.jpf, *.jpx, *.jpm, *.mj2)", "JP2", "J2K", "JPF", "JPX", "JPM", "MJ2"),
            new FileNameExtensionFilter("PNG Images(*.png)", "PNG"),
            new FileNameExtensionFilter("GIF Images(*.gif)", "GIF"),
            new FileNameExtensionFilter("BMP Images(*.bmp)", "BMP"),
            new FileNameExtensionFilter("PCX Images(*.pcx)", "PCX"),
            new FileNameExtensionFilter("TIFF Images(*.tiff, *.tif)", "TIFF", "TIF"),
            new FileNameExtensionFilter("TGA Images(*.tga)", "TGA"),
            new FileNameExtensionFilter("Photoshop Files(*.psd)", "PSD"),
            new FileNameExtensionFilter("PNM Images(*.pnm)", "PNM"),
            new FileNameExtensionFilter("PPM Images(*.ppm)", "PPM"),
            new FileNameExtensionFilter("PGM Images(*.pgm)", "PGM"),
            new FileNameExtensionFilter("PBM Images(*.pbm)", "PBM"),
            new FileNameExtensionFilter("WBM Images(*.wbm)", "WBM"),
            new FileNameExtensionFilter("WBMP Images(*.wbmp)", "WBMP"),
            new FileNameExtensionFilter("All supported file types", "PDF", "JPG", "JPEG", "JP2", "J2K", "JPF", "JPX",
                "JPM", "MJ2", "PNG", "GIF", "BMP", "PCX", "TIFF", "TIF", "TGA", "PSD", "PNM", "PPM", "PGM", "PBM", "WBM", "WBMP")};


        for (FileNameExtensionFilter filter : filters) {
            fc.setFileFilter(filter);
        }

        panelTitle = new JLabel("ex:  *.pdf, *.png, *.tiff");
        imagePathField = new JTextField();
        browseButton = new JButton("Browse");

        browseButton.addActionListener(e -> browseActionListener());

        imagePathField.getDocument().addDocumentListener(new DocumentListener() {
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
                style.setImagePath(imagePathField.getText());
            }
        });

        pagePanel = new JPanel(new FormLayout("f:p, $lcgap, f:p", "p"));
        pageLabel = new JLabel("Page:");
        pdfPageSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        pdfPageSpinner.addChangeListener(e -> {
            try {
                int page = Integer.parseInt(pdfPageSpinner.getValue().toString());
                style.setPdfPage(page);
            } catch (Exception ex) {
                Logger.getLogger(WatermarkImagePanel.class.getName()).log(Level.SEVERE, "Ex107", ex);
            }
        });
    }

    private void browseActionListener() {
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            imagePathField.setText(file.getAbsolutePath());
            pdfPageSpinner.setValue(0);
        }
    }

    private void positionComponents() {
        this.add(panelTitle, CC.xy(2, 1));
        this.add(browseButton, CC.xy(4, 1));
        this.add(imagePathField, CC.xyw(2, 3, 3));

        pagePanel.add(pageLabel, CC.xy(1, 1));
        pagePanel.add(pdfPageSpinner, CC.xy(3, 1));

        this.add(pagePanel, CC.xyw(2, 5, 3));
    }

    public void setStyle(WatermarkStyle style) {
        this.style = style;

        imagePathField.setText(style.getImagePath());
        pdfPageSpinner.setValue(style.getPdfPage());
    }

}

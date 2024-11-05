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
        setBorder(javax.swing.BorderFactory.createTitledBorder(" Raster only "));

        fc = new JFileChooser();
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        FileNameExtensionFilter[] filters = {
                new FileNameExtensionFilter("BMP Images(*.bmp)", "BMP"),
                new FileNameExtensionFilter("GIF Images(*.gif)", "GIF"),
                new FileNameExtensionFilter("DDS Images(*.dds)", "DDS"),
                new FileNameExtensionFilter("HDR Images(*.hdr)", "HDR"),
            	new FileNameExtensionFilter("JPEG Images(*.jpg, *.jpeg)", "JPG", "JPEG"),
                new FileNameExtensionFilter("JPEG2000 Images(*.jp2, *.j2k, *.jpf, *.jpx, *.jpm, *.mj2)", "JP2", "J2K", "JPF", "JPX", "JPM", "MJ2"),
                new FileNameExtensionFilter("AMIGA Images(*.iff)", "IFF"),
                new FileNameExtensionFilter("PNG Images(*.png)", "PNG"),
                new FileNameExtensionFilter("PHOTOSHOP Files(*.psd)", "PSD"),
                new FileNameExtensionFilter("PCX Images(*.pcx)", "PCX"),
                new FileNameExtensionFilter("PICT Images(*.pict, *.pct)", "PICT", "PCT"),
                new FileNameExtensionFilter("PAM Images(*.pam)", "PAM"),
                new FileNameExtensionFilter("PBM Images(*.pbm)", "PBM"),
                new FileNameExtensionFilter("PGM Images(*.pgm)", "PGM"),
                new FileNameExtensionFilter("PNM Images(*.pnm)", "PNM"),
                new FileNameExtensionFilter("PPM Images(*.ppm)", "PPM"),
                new FileNameExtensionFilter("SGI Images(*.sgi)", "SGI"),
                new FileNameExtensionFilter("VECTOR Files(*.svg)", "SVG"),
                new FileNameExtensionFilter("TGA Images(*.tga)", "TGA"),
                new FileNameExtensionFilter("TIFF Images(*.tiff, *.tif)", "TIFF", "TIF"),
                new FileNameExtensionFilter("WEBP Images(*.webp)", "WEBP"),
                new FileNameExtensionFilter("PDF Files(*.pdf)", "PDF"),
//                new FileNameExtensionFilter("WBM Images(*.wbm)", "WBM"),
//                new FileNameExtensionFilter("WBMP Images(*.wbmp)", "WBMP"),
                new FileNameExtensionFilter("All supported File types", "PDF", "JPG", "JPEG", "JP2", "J2K", "JPF", "JPX",
                        "JPM", "MJ2", "PNG", "GIF", "DDS", "BMP", "TIFF", "TIF", "IFF", "TGA", "PSD", "PAM", "PBM", "PGM", 
                        "PNM", "PPM", "SGI", "SVG", "HDR", "WEBP","PCX", "PICT", "PCT")};

        for (FileNameExtensionFilter filter : filters) {
            fc.setFileFilter(filter);
        }

        panelTitle = new JLabel("Ex: *.png, *.tiff, *.pdf");
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
        pageLabel = new JLabel("Select which page to use:");
        pdfPageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
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

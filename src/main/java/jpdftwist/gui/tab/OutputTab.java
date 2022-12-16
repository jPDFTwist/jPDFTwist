package jpdftwist.gui.tab;

import com.itextpdf.text.DocumentException;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import jpdftwist.core.OutputEventListener;
import jpdftwist.core.PDFTwist;
import jpdftwist.core.PdfToImage;
import jpdftwist.gui.MainWindow;
import jpdftwist.gui.component.FileChooser;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class OutputTab extends Tab {

    private final JTextField outputFile;
    private final JSlider qualitySlider;
    private final JCheckBox burst;
    private final JCheckBox multiPageTiffCheckBox;
    private JCheckBox transparent;
    private final JCheckBox uncompressedComboBox;
    private final JCheckBox pageMarksComboBox;
    private final JCheckBox tempfilesComboBox;
    private final JCheckBox optimizeSizeComboBox;
    private final JCheckBox fullyCompressedComboBox;
    private final JLabel colorLabel;
    private final JLabel compressionLabel;
    private final JLabel qualityLabel;
    private final JLabel warning;
    private final JComboBox<PdfToImage.ImageType> fileTypeComboBox;
    private final JComboBox<PdfToImage.ColorMode> colorMode;
    private final JComboBox<PdfToImage.TiffCompression> compressionType;
    private final MainWindow mainWindow;
    private String currentExtension = ".pdf";
    private JPanel panel;
    private JRadioButton splitByPageTextRadioButton;
    private JRadioButton splitByOddPagesRadioButton;
    private JRadioButton splitByEvenPagesRadioButton;
    private JRadioButton splitBySpecificPageRadioButton;
    private JRadioButton splitByChunkRadioButton;
    private JRadioButton splitBySizeRadioButton;
    private JRadioButton splitByBookmarkLevelRadioButton;
    private JRadioButton splitByBookmarkTextRadioButton;
    private JComboBox<String> dpiComboBox;
    private JLabel dpiLabel;
    private JTextField textField_3;
    private JTextField textField_4;
    private JTextField textField_5;
    private JTextField textField_6;
    private JTextField textField_7;
    private JComboBox<String> textField_8;


    public OutputTab(MainWindow mf) {
        super(new FormLayout("f:p, f:p:g, f:p", "f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p:g" + ""));
        this.mainWindow = mf;
        CellConstraints CC = new CellConstraints();
        warning = new JLabel("");
        this.add(new JLabel("Filename:"), CC.xy(1, 1));
        this.add(outputFile = new JTextField(), CC.xy(2, 1));
        outputFile.setBackground(new Color(255, 255, 255));
        JButton selectFile;
        this.add(selectFile = new JButton("..."), CC.xy(3, 1));
        selectFile.addActionListener(e -> {

            FileChooser fileChooser = new FileChooser();
            JFileChooser pdfChooser = fileChooser.getFileChooser();

            if (pdfChooser.showSaveDialog(mainWindow) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            String filename = pdfChooser.getSelectedFile().getAbsolutePath();
            if (!new File(filename).getName().contains(".")) {
                filename += currentExtension;
            }
            filename = setCorrectExtension(filename);
            if (new File(filename).exists()) {
                if (JOptionPane.showConfirmDialog(mainWindow, "Overwrite existing file?", "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
                    return;
            }
            outputFile.setText(filename);
        });
        this.add(multiPageTiffCheckBox = new JCheckBox("Export as Tiff multipage image"), CC.xyw(1, 2, 3));
        multiPageTiffCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findSharedLibrary();
                fileTypeComboBox.setEnabled(false);
                burst.setSelected(false);
                if (!multiPageTiffCheckBox.isSelected()) {
                    whichToEnable(0);
                } else {
                    whichToEnable(100);
                }
                String filename = outputFile.getText();
                filename = setCorrectExtension(filename);
                outputFile.setText(filename);
            }
        });
        this.add(burst = new JCheckBox("Split pages (use *  in file name as wildcard for page number)"),
            CC.xyw(1, 3, 3));
        burst.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                multiPageTiffCheckBox.setSelected(false);
                fileTypeComboBox.setEnabled(burst.isSelected());

                splitByOddPagesRadioButton.setEnabled(burst.isSelected());
                splitByEvenPagesRadioButton.setEnabled(burst.isSelected());
                splitBySpecificPageRadioButton.setEnabled(burst.isSelected());
                splitByChunkRadioButton.setEnabled(burst.isSelected());
                splitByPageTextRadioButton.setEnabled(burst.isSelected());
                splitByBookmarkLevelRadioButton.setEnabled(burst.isSelected());
                splitByBookmarkTextRadioButton.setEnabled(burst.isSelected());
                splitBySizeRadioButton.setEnabled(burst.isSelected());

                textField_3.setEnabled(burst.isSelected());
                textField_4.setEnabled(burst.isSelected());
                textField_5.setEnabled(burst.isSelected());
                textField_6.setEnabled(burst.isSelected());
                textField_7.setEnabled(burst.isSelected());
                textField_8.setEnabled(burst.isSelected());

                warning.setIcon(null);
                warning.setToolTipText("");
                if (!burst.isSelected()) {

                    whichToEnable(0);
                    setOptionsEnabled(true, false, false, false, true, true, true, true, true);
                } else {
                    whichToEnable(fileTypeComboBox.getSelectedIndex());

                    if (fileTypeComboBox.getSelectedIndex() != 0) {
                        findSharedLibrary();
                    }
                }
                String filename = outputFile.getText();
                filename = setCorrectExtension(filename);
                outputFile.setText(filename);
            }
        });

        this.add(new JLabel("Type:"), "1, 4, left, center");
        this.add(
            fileTypeComboBox = new JComboBox<>(new javax.swing.DefaultComboBoxModel<>(
                new PdfToImage.ImageType[]{
//								Vector formats
                    PdfToImage.ImageType.PDF,
                    PdfToImage.ImageType.PSD,
                    PdfToImage.ImageType.SVG,
                    PdfToImage.ImageType.EMF,
                    PdfToImage.ImageType.WMF,

//								Raster formats
                    PdfToImage.ImageType.JPG,
                    PdfToImage.ImageType.JP2,
                    PdfToImage.ImageType.PNG,
                    PdfToImage.ImageType.PAM,
                    PdfToImage.ImageType.PNM,
                    PdfToImage.ImageType.BMP,
                    PdfToImage.ImageType.GIF,
                    PdfToImage.ImageType.PCX,
                    PdfToImage.ImageType.TGA,
                    PdfToImage.ImageType.TIFF
                })),
            CC.xyw(2, 4, 2));
        fileTypeComboBox.setSelectedIndex(0);
        fileTypeComboBox.setMaximumRowCount(15);
        fileTypeComboBox.addItemListener(arg0 -> {
            whichToEnable(fileTypeComboBox.getSelectedIndex());

            if (fileTypeComboBox.getSelectedIndex() == 0) {
                setOptionsEnabled(true, false, false, false, true, true, true, true, true);

                if (warning.getToolTipText().equals("<html>Images will be exported with selected Resolution")) {
                    warning.setIcon(null);
                    warning.setToolTipText("");
                }
            } else {
                findSharedLibrary();
            }
            String filename = outputFile.getText();
            filename = setCorrectExtension(filename);
            outputFile.setText(filename);

        });
        fileTypeComboBox.setEnabled(false);

        JPanel imagePanel;
        this.add(imagePanel = new JPanel(new FormLayout(
                new ColumnSpec[]{FormSpecs.PREF_COLSPEC, ColumnSpec.decode("pref:grow"),
                    ColumnSpec.decode("pref:grow"), ColumnSpec.decode("20px"), FormSpecs.PREF_COLSPEC,
                    FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("right:pref:grow"),},
                new RowSpec[]{RowSpec.decode("fill:pref"), RowSpec.decode("fill:pref"), RowSpec.decode("fill:pref"),
                    RowSpec.decode("24px"),})),
            CC.xyw(1, 5, 3));
        imagePanel.add(colorLabel = new JLabel("Color Mode:"), CC.xy(1, 1));
        imagePanel.add(colorMode = new JComboBox<>(new javax.swing.DefaultComboBoxModel<>(new PdfToImage.ColorMode[]{})),

            CC.xyw(2, 1, 2));
        colorMode.setMaximumRowCount(24);
        colorMode.addItemListener(arg0 -> {
            if (fileTypeComboBox.getSelectedIndex() != 14 && !multiPageTiffCheckBox.isSelected()) {
                return;
            }

            PdfToImage.ColorMode selectedColorMode = (PdfToImage.ColorMode) colorMode.getSelectedItem();
            if (selectedColorMode != null) {
                switch (selectedColorMode) {
                    case GRAY:
                    case BNW:
                    case BNWI:
                        transparent.setEnabled(false);
                        break;
                    default:
                        transparent.setEnabled(true);
                        break;
                }
            }
        });
        imagePanel.add(compressionLabel = new JLabel("Compression:"), CC.xy(1, 2));
        imagePanel.add(compressionType = new JComboBox<>(new javax.swing.DefaultComboBoxModel<>(new PdfToImage.TiffCompression[]{})),

            CC.xyw(2, 2, 2));
        compressionType.addItemListener(arg0 -> {
            PdfToImage.TiffCompression selectedTiffCompression = (PdfToImage.TiffCompression) compressionType.getSelectedItem();
            if (selectedTiffCompression != null) {
                setOptionsEnabled(colorMode.getSelectedIndex() == 0, true, true, true, true, true, false, false, false);
            }
        });
        imagePanel.add(qualityLabel = new JLabel("Quality:"), CC.xy(1, 3));
        qualityLabel.setToolTipText("JPEG quality (0-100%)");
        imagePanel.add(qualitySlider = new JSlider(), CC.xyw(2, 3, 2));
        qualitySlider.setValue(90);
        imagePanel.add(transparent = new JCheckBox("Save Transparency "), CC.xyw(1, 4, 2));
        transparent.setSelected(true);
        imagePanel.add(warning, CC.xy(7, 4));
        imagePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Burst Options", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        imagePanel.add(getDpiLabel(), "5, 3, fill, default");
        imagePanel.add(getDpiComboBox(), "7, 3, fill, default");
        this.add(new JSeparator(), CC.xyw(1, 6, 3));
        this.add(uncompressedComboBox = new JCheckBox("Save uncompressed"), CC.xyw(1, 7, 3));
        this.add(pageMarksComboBox = new JCheckBox("Remove PdfTk page marks"), CC.xyw(1, 8, 3));
        uncompressedComboBox.addActionListener(e -> pageMarksComboBox.setText((uncompressedComboBox.isSelected() ? "Add" : "Remove") + " PdfTk page marks"));
        tempfilesComboBox = new JCheckBox("Use temporary files for intermediary results (saves RAM)");
        tempfilesComboBox.addItemListener(e -> mainWindow.getInputTab().setUseTempFiles(tempfilesComboBox.isSelected()));
        this.add(tempfilesComboBox, CC.xyw(1, 9, 3));
        this.add(optimizeSizeComboBox = new JCheckBox("Optimize PDF size (will need a lot of RAM)"), CC.xyw(1, 10, 3));
        this.add(fullyCompressedComboBox = new JCheckBox("Use better compression (Acrobat 6.0+)"), CC.xyw(1, 11, 3));
        JLabel label_1 = new JLabel("<html>You can use the following variables in the output filename:<br>"
            + "<tt>&lt;F></tt>: Input filename without extension<br>"
            + "<tt>&lt;FX></tt>: Input filename with extension<br>"
            + "<tt>&lt;P></tt>: Input file path without filename<br>"
            + "<tt>&lt;T></tt>: Create output dir tree<br>"
            + "<tt>&lt;#></tt>: Next free number (where file does not exist)<br>"
            + "<tt>&lt;*></tt>: Page number (for bursting pages)<br>"
            + "<tt>&lt;$></tt>: Test function");
        label_1.setFont(new Font("Tahoma", Font.PLAIN, 11));
        this.add(label_1, "1, 13, 3, 1, left, top");
        add(getPanel_1(), "1, 12, 3, 1");
        setOptionsEnabled(true, false, false, false, true, true, true, true, true);
    }

    private void findSharedLibrary() {
        try {
            PdfToImage.setJavaLibraryPath();
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainWindow, ex.getMessage(), "Error reading file", JOptionPane.ERROR_MESSAGE);
        }
        String sharedLibraryName = PdfToImage.checkForLibraries();
        if (sharedLibraryName != null) {

            if (sharedLibraryName.equals("nojmupdf")) {
                warning.setIcon(new javax.swing.ImageIcon(getClass().getResource("/warning.png")));
                warning.setToolTipText("<html>This feature is not available<br>"
                    + "in compact version. If you are not<br>" + "using compact version verify that<br>"
                    + "lib/JmuPdf.jar is present and your<br>" + "download was not corrupted.");
                multiPageTiffCheckBox.setSelected(false);
                fileTypeComboBox.setSelectedItem(PdfToImage.ImageType.PDF);
                fileTypeComboBox.setSelectedIndex(0);

            } else if (sharedLibraryName.contains(".")) {
                warning.setIcon(new javax.swing.ImageIcon(getClass().getResource("/warning.png")));
                warning.setToolTipText("<html>\"" + sharedLibraryName + "\" needs to be in <br>\""
                    + PdfToImage.getJarFolder() + "\"<br>" + "to export in image file type");
                multiPageTiffCheckBox.setSelected(false);
                fileTypeComboBox.setSelectedItem(PdfToImage.ImageType.PDF);
                fileTypeComboBox.setSelectedIndex(0);
            }

        } else {
            warning.setIcon(new javax.swing.ImageIcon(getClass().getResource("/info.png")));
            warning.setToolTipText("<html>Images will be exported with selected Resolution");
        }
    }

    private String setCorrectExtension(String filename) {
        boolean changeExtension = filename.endsWith(currentExtension);
        if (changeExtension) {
            filename = filename.substring(0, filename.length() - currentExtension.length());
        }
        if (burst.isSelected() || multiPageTiffCheckBox.isSelected()) {
            if (fileTypeComboBox.getSelectedIndex() == 14 || multiPageTiffCheckBox.isSelected()) {

                currentExtension = ".tiff";
            } else {
                currentExtension = "." + fileTypeComboBox.getSelectedItem().toString().toLowerCase();
            }
        } else {
            currentExtension = ".pdf";
        }
        if (changeExtension) {
            filename += currentExtension;
        }
        return filename;
    }

    private void whichToEnable(int option) {
        javax.swing.DefaultComboBoxModel<PdfToImage.ColorMode> emptyModel = new javax.swing.DefaultComboBoxModel<>(new PdfToImage.ColorMode[]{});
        javax.swing.DefaultComboBoxModel<PdfToImage.ColorMode> grayAndRGBModel = new javax.swing.DefaultComboBoxModel<>(new PdfToImage.ColorMode[]
            {PdfToImage.ColorMode.GRAY, PdfToImage.ColorMode.RGB});
        javax.swing.DefaultComboBoxModel<PdfToImage.ColorMode> bmpModel = new javax.swing.DefaultComboBoxModel<>(new PdfToImage.ColorMode[]
            {PdfToImage.ColorMode.BNW, PdfToImage.ColorMode.BNWI, PdfToImage.ColorMode.GRAY, PdfToImage.ColorMode.RGB});

        if (option == 0) { // PDF format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(emptyModel);
            }
        } else if (option == 1) { // PSD format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(emptyModel);
            }
        } else if (option == 2) { // SVG format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(emptyModel);
            }
        } else if (option == 3) { // EMF format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(emptyModel);
            }
        } else if (option == 4) { // WMF format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(emptyModel);
            }
        } else if (option == 5) { // JPG format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(emptyModel);
            }
        } else if (option == 6) { // JP2 format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(emptyModel);
            }
        } else if (option == 7) { // PNG format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(emptyModel);
            }
        } else if (option == 8) { // PAM format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(grayAndRGBModel);
            }
        } else if (option == 9) { // PNM format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(grayAndRGBModel);
            }
        } else if (option == 10) { // BMP format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(bmpModel);
            }
        } else if (option == 11) { // GIF format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(grayAndRGBModel);
            }
        } else if (option == 12) { // PCX format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(emptyModel);
            }
        } else if (option == 13) { // TGA format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(emptyModel);
            }
        } else if (option == 14) { // TIFF format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(emptyModel);
            }
        } else if (option == 100) { // multiTIFF format
            if (colorMode.getModel().getSize() != 64) {
                colorMode.setModel(emptyModel);
            }
        }

        // Selecting Compression Types
        javax.swing.DefaultComboBoxModel<PdfToImage.TiffCompression> emptyCompressionModel = new javax.swing.DefaultComboBoxModel<>(new PdfToImage.TiffCompression[]{});
        javax.swing.DefaultComboBoxModel<PdfToImage.TiffCompression> noneCompressionModel = new javax.swing.DefaultComboBoxModel<>(new PdfToImage.TiffCompression[]
            // TODO: Replace with PNG/TGA Compression
            {PdfToImage.TiffCompression.NONE});
        javax.swing.DefaultComboBoxModel<PdfToImage.TiffCompression> tiffCompressionModel = new javax.swing.DefaultComboBoxModel<>(new PdfToImage.TiffCompression[]
            {PdfToImage.TiffCompression.NONE,
                PdfToImage.TiffCompression.LZW,
                PdfToImage.TiffCompression.JPEG,
                PdfToImage.TiffCompression.ZLIB,
                PdfToImage.TiffCompression.PACKBITS,
                PdfToImage.TiffCompression.DEFLATE,
                PdfToImage.TiffCompression.RLE
            });

        if (option == 0) { // PDF format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(emptyCompressionModel);
            }
        } else if (option == 1) { // PSD format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(emptyCompressionModel);
            }
        } else if (option == 2) { // SVG format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(emptyCompressionModel);
            }
        } else if (option == 3) { // EMF format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(emptyCompressionModel);
            }
        } else if (option == 4) { // WMF format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(emptyCompressionModel);
            }
        } else if (option == 5) { // JPG format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(emptyCompressionModel);
            }
        } else if (option == 6) { // JP2 format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(emptyCompressionModel);
            }
        } else if (option == 7) { // PNG format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(noneCompressionModel);
            }
        } else if (option == 8) { // PAM format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(emptyCompressionModel);
            }
        } else if (option == 9) { // PNM format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(emptyCompressionModel);
            }
        } else if (option == 10) { // BMP format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(emptyCompressionModel);
            }
        } else if (option == 11) { // GIF format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(emptyCompressionModel);
            }
        } else if (option == 12) { // PCX format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(emptyCompressionModel);
            }
        } else if (option == 13) { // TGA format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(noneCompressionModel);
            }
        } else if (option == 14) { // TIFF format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(tiffCompressionModel);
            }
        } else if (option == 100) { // multiTIFF format
            if (compressionType.getModel().getSize() != 64) {
                compressionType.setModel(tiffCompressionModel);
            }
        }

        switch (option) {
            case 1: // PSD format
            case 2: // SVG format
                setOptionsEnabled(true, false, false, false, true, false, false, false, false);
                break;
            case 3: // EMF format
            case 4: // WMF format
                setOptionsEnabled(false, false, false, false, false, false, false, false, false);
                break;
            case 5: // JPG format
            case 6: // JP2 format
                setOptionsEnabled(true, false, true, true, false, false, false, false, false);
                break;
            case 7: // PNG format
            case 13: // TGA format
            case 14: // TIFF format
            case 100: // multiPageTiff enabled
                setOptionsEnabled(true, true, true, true, true, false, false, false, false);
                break;
            case 8: // PAM format
            case 9: // PNM format
            case 12: // PCX format
            case 10: // BMP format
                setOptionsEnabled(true, false, false, true, false, false, false, false, false);
                break;
            case 11: // GIF format
                setOptionsEnabled(true, false, false, true, true, false, false, false, false);
                break;
            default:
                setOptionsEnabled(true, false, false, false, true, true, true, true, true);
                break;
        }
    }

    private void setOptionsEnabled(boolean color, boolean compression, boolean quality, boolean dpi, boolean transparency,
                                   boolean uncompressed, boolean pageMarks, boolean optimizeSize, boolean fullyCompressed) {
        colorLabel.setEnabled(color);
        colorMode.setEnabled(color);

        dpiComboBox.setEnabled(dpi);
        dpiLabel.setEnabled(dpi);

        compressionLabel.setEnabled(compression);
        compressionType.setEnabled(compression);

        qualityLabel.setEnabled(quality);
        qualitySlider.setEnabled(quality);

        transparent.setEnabled(transparency);

        uncompressedComboBox.setEnabled(uncompressed);
        pageMarksComboBox.setEnabled(pageMarks);
        tempfilesComboBox.setEnabled(true);
        optimizeSizeComboBox.setEnabled(optimizeSize);
        fullyCompressedComboBox.setEnabled(fullyCompressed);
    }

    private boolean matchTransparency(boolean transparency) {
        if (transparent.isEnabled()) {
            return transparency;
        } else {
            return false;
        }
    }


    public String getTabName() {
        return "Output";
    }


    public void checkRun() throws IOException {
        if (outputFile.getText().length() == 0)
            throw new IOException("No output file selected");
        String outputFileName = outputFile.getText();
        if (mainWindow.getInputTab().getBatchLength() > 1) {
            if (!outputFileName.contains("<F>") && !outputFileName.contains("<FX>") && !outputFileName.contains("<P>")
                && !outputFileName.contains("<#>")) {
                throw new IOException("Variables in output file name required for batch mode");
            }
        }
        mainWindow.getInputTab().setUseTempFiles(tempfilesComboBox.isSelected());
    }

    public PDFTwist run(PDFTwist pdfTwist, OutputEventListener outputEventListener) throws IOException, DocumentException {
        outputEventListener.updateJPDFTwistProgress(getTabName());
        outputEventListener.setAction("Producing output file(s)");
        if (pageMarksComboBox.isSelected()) {
            if (uncompressedComboBox.isSelected()) {
                pdfTwist.addPageMarks();
            } else {
                pdfTwist.removePageMarks();
            }
        }
        boolean matchedTransparency = matchTransparency(transparent.isSelected());

        boolean burstImages = (fileTypeComboBox.getSelectedIndex() != 0 && !multiPageTiffCheckBox.isSelected());
        PdfToImage.ImageType type = (PdfToImage.ImageType) fileTypeComboBox.getSelectedItem();
        if (multiPageTiffCheckBox.isSelected()) {
            type = PdfToImage.ImageType.TIFF;
        }
        PdfToImage.ColorMode selectedColorMode = colorMode.getModel().getSize() == 0 ? null : (PdfToImage.ColorMode) colorMode.getModel().getSelectedItem();
        PdfToImage.TiffCompression selectedTiffCompression = compressionType.getModel().getSize() == 0 ? null : (PdfToImage.TiffCompression) compressionType.getSelectedItem();
        pdfTwist.setPdfImages(new PdfToImage(burstImages, selectedColorMode, type,
            selectedTiffCompression, qualitySlider.getValue(),
            matchedTransparency));
        pdfTwist.writeOutput(outputFile.getText(), multiPageTiffCheckBox.isSelected(), burst.isSelected(),
            uncompressedComboBox.isSelected(), optimizeSizeComboBox.isSelected(), fullyCompressedComboBox.isSelected());
        return null;
    }

    private JPanel getPanel_1() {
        if (panel == null) {
            panel = new JPanel();
            panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Split Options",
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
            panel.setLayout(new FormLayout(
                new ColumnSpec[]{ColumnSpec.decode("max(160px;pref)"), ColumnSpec.decode("max(200px;pref):grow"),
                    ColumnSpec.decode("15px"), ColumnSpec.decode("max(160px;pref)"),
                    ColumnSpec.decode("max(200px;pref):grow"), ColumnSpec.decode("15px"),},
                new RowSpec[]{RowSpec.decode("23px"), RowSpec.decode("23px"), RowSpec.decode("23px"),
                    RowSpec.decode("23px"), RowSpec.decode("23px"),}));
            panel.add(getSplitByOddPagesRadioButton(), "1, 1, left, center");
            panel.add(getSplitByBookmarkLevelRadioButton(), "4, 1, left, center");
            panel.add(getTextField_6(), "5, 1, fill, default");
            panel.add(getSplitByBookmarkTextRadioButton(), "4, 2, left, center");
            panel.add(getTextField_4_1(), "5, 2, fill, default");
            panel.add(getTextField_3(), "2, 3, fill, default");
            panel.add(getSplitBySizeRadioButton(), "4, 3, left, center");
            panel.add(getSplitByEvenPagesRadioButton(), "1, 2, left, center");
            panel.add(getSplitBySpecificPageRadioButton(), "1, 3, left, center");
            panel.add(getTextField_5_1(), "5, 3, fill, default");
            panel.add(getSplitByChunkRadioButton(), "1, 4, left, center");
            panel.add(getTextField_1(), "2, 4, fill, default");
            panel.add(getSplitByPageTextRadioButton(), "1, 5, left, center");
            panel.add(getTextField_1_1(), "2, 5, fill, default");

        }
        return panel;
    }

    private JRadioButton getSplitByPageTextRadioButton() {
        if (splitByPageTextRadioButton == null) {
            splitByPageTextRadioButton = new JRadioButton("Split by page text");
            splitByPageTextRadioButton.setToolTipText("Split 'after' the page containing a specifc text");
            splitByPageTextRadioButton.setEnabled(false);
        }
        return splitByPageTextRadioButton;
    }

    private JRadioButton getSplitByOddPagesRadioButton() {
        if (splitByOddPagesRadioButton == null) {
            splitByOddPagesRadioButton = new JRadioButton("Split by odd pages");
            splitByOddPagesRadioButton.setToolTipText("Split after each odd page");
            splitByOddPagesRadioButton.setEnabled(false);
        }
        return splitByOddPagesRadioButton;
    }

    private JRadioButton getSplitByEvenPagesRadioButton() {
        if (splitByEvenPagesRadioButton == null) {
            splitByEvenPagesRadioButton = new JRadioButton("Split by even pages");
            splitByEvenPagesRadioButton.setToolTipText("Split after each even page");
            splitByEvenPagesRadioButton.setEnabled(false);
        }
        return splitByEvenPagesRadioButton;
    }

    private JRadioButton getSplitBySpecificPageRadioButton() {
        if (splitBySpecificPageRadioButton == null) {
            splitBySpecificPageRadioButton = new JRadioButton("Split by specific pages");
            splitBySpecificPageRadioButton.setToolTipText("Split after specific pages (ex: 4-6, 9, 14)");
            splitBySpecificPageRadioButton.setEnabled(false);
        }
        return splitBySpecificPageRadioButton;
    }

    private JRadioButton getSplitByChunkRadioButton() {
        if (splitByChunkRadioButton == null) {
            splitByChunkRadioButton = new JRadioButton("Split by chunk of  'n' pages");
            splitByChunkRadioButton.setSelected(true);
            splitByChunkRadioButton.setToolTipText("Split after a chunk of pages (ex: 100)");
            splitByChunkRadioButton.setEnabled(false);
        }
        return splitByChunkRadioButton;
    }

    private JRadioButton getSplitBySizeRadioButton() {
        if (splitBySizeRadioButton == null) {
            splitBySizeRadioButton = new JRadioButton("Split by size");
            splitBySizeRadioButton.setToolTipText("Split 'after' a specific size");
            splitBySizeRadioButton.setEnabled(false);
        }
        return splitBySizeRadioButton;
    }

    private JRadioButton getSplitByBookmarkLevelRadioButton() {
        if (splitByBookmarkLevelRadioButton == null) {
            splitByBookmarkLevelRadioButton = new JRadioButton("Split by bookmark level");
            splitByBookmarkLevelRadioButton.setToolTipText("Split 'before' the page linked to a bookmark level");
            splitByBookmarkLevelRadioButton.setEnabled(false);
        }
        return splitByBookmarkLevelRadioButton;
    }

    private JRadioButton getSplitByBookmarkTextRadioButton() {
        if (splitByBookmarkTextRadioButton == null) {
            splitByBookmarkTextRadioButton = new JRadioButton("Split by bookmark text");
            splitByBookmarkTextRadioButton.setToolTipText("Split 'before' the bookmark containing a specific text");
            splitByBookmarkTextRadioButton.setEnabled(false);
        }
        return splitByBookmarkTextRadioButton;
    }

    private JComboBox<String> getDpiComboBox() {
        if (dpiComboBox == null) {
            dpiComboBox = new JComboBox<>();
            dpiComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"16", "36", "72", "100", "120", "150", "180", "200", "240", "270", "300", "350", "400", "600", "800", "900", "1000", "1200", "1500", "1800", "2000", "2400", "2700", "3000", "Custom"}));
            dpiComboBox.setSelectedIndex(10);
            dpiComboBox.setToolTipText("");
        }
        return dpiComboBox;
    }

    private JLabel getDpiLabel() {
        if (dpiLabel == null) {
            dpiLabel = new JLabel("Resolution:");
            dpiLabel.setToolTipText("Image resolution in DPI");
        }
        return dpiLabel;
    }

    private JTextField getTextField_3() {
        if (textField_3 == null) {
            textField_3 = new JTextField("");
            textField_3.setEnabled(false);
        }
        return textField_3;
    }

    private JTextField getTextField_1() {
        if (textField_4 == null) {
            textField_4 = new JTextField("1");
            textField_4.setEnabled(false);
        }
        return textField_4;
    }

    private JTextField getTextField_1_1() {
        if (textField_5 == null) {
            textField_5 = new JTextField("");
            textField_5.setToolTipText("");
            textField_5.setEnabled(false);
        }
        return textField_5;
    }

    private JTextField getTextField_6() {
        if (textField_6 == null) {
            textField_6 = new JTextField("");
            textField_6.setEnabled(false);
        }
        return textField_6;
    }

    private JTextField getTextField_4_1() {
        if (textField_7 == null) {
            textField_7 = new JTextField("");
            textField_7.setEnabled(false);
        }
        return textField_7;
    }

    private JComboBox<String> getTextField_5_1() {
        if (textField_8 == null) {
            textField_8 = new JComboBox<>();
            textField_8.setModel(new DefaultComboBoxModel<>(new String[]{"(MB) > MegaBytes", "(KB)  > KiloBytes", "(B)    > Bytes"}));
            textField_8.setSelectedIndex(1);
            textField_8.setEnabled(false);
        }
        return textField_8;
    }
}

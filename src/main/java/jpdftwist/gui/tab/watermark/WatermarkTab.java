package jpdftwist.gui.tab.watermark;

import com.itextpdf.text.DocumentException;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jpdftwist.core.PDFTwist;
import jpdftwist.gui.MainWindow;
import jpdftwist.gui.component.FileChooser;
import jpdftwist.gui.component.table.TableComponent;
import jpdftwist.gui.dialog.OutputProgressDialog;
import jpdftwist.gui.tab.PageNumberTab;
import jpdftwist.gui.tab.Tab;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;

public class WatermarkTab extends Tab {

    private final MainWindow mainWindow;
    private final JCheckBox pdfWatermark;
    private final JCheckBox textWatermark;
    private final JCheckBox pageNumbers;
    private final JCheckBox watermarkUseColor;
    private final JCheckBox useMask;
    private final JCheckBox differentPageNumbers;
    private final JTextField filename;
    private final JTextField pgnoSize;
    private final JTextField pgnoHOffset;
    private final JTextField pgnoVOffset;
    private final JTextField maskText;
    private final JTextField watermarkText;
    private final JTextField watermarkSize;
    private final JTextField watermarkOpacity;
    private final JComboBox<String> pgnoHRef;
    private final JComboBox<String> pgnoVRef;
    private final JButton fileButton;
    private final JButton watermarkColor;
    private final JButton load;
    private final TableComponent pageNumberRanges;

    public WatermarkTab(MainWindow mf) {
        super(new FormLayout("f:p, f:p:g, 80dlu, f:p",
            "f:p, f:p, 10dlu, f:p, f:p, f:p, f:p, f:p, 10dlu, f:p, f:p, f:p, f:p, f:p, f:p, f:p, f:p:g"));
        mainWindow = mf;
        CellConstraints CC = new CellConstraints();
        add(pdfWatermark = new JCheckBox("Add first page of PDF as background watermark"), CC.xyw(1, 1, 4));
        pdfWatermark.addActionListener(e -> updatePDFWatermarkEnabled());
        add(new JLabel("Filename: "), CC.xy(1, 2));
        add(filename = new JTextField(""), CC.xyw(2, 2, 2));
        filename.setEditable(false);
        add(fileButton = new JButton("..."), CC.xy(4, 2));
        fileButton.addActionListener(e -> {
            FileChooser fileChooser = new FileChooser();

            JFileChooser pdfChooser = fileChooser.getFileChooser();
            if (pdfChooser.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
                filename.setText(pdfChooser.getSelectedFile().getAbsolutePath());
            }
        });
        add(new JSeparator(), CC.xyw(1, 3, 4));
        add(textWatermark = new JCheckBox("Add transparent text watermark"), CC.xyw(1, 4, 4));
        textWatermark.addActionListener(e -> updateTextWatermarkEnabled());
        add(new JLabel("Text:"), CC.xy(1, 5));
        add(watermarkText = new JTextField("Confidential"), CC.xyw(2, 5, 3));
        add(new JLabel("Font size:"), CC.xy(1, 6));
        add(watermarkSize = new JTextField("100"), CC.xyw(2, 6, 3));
        add(new JLabel("Opacity:"), CC.xy(1, 7));
        add(watermarkOpacity = new JTextField("0.25"), CC.xyw(2, 7, 3));
        add(watermarkUseColor = new JCheckBox("Color:"), CC.xy(1, 8));
        add(watermarkColor = new JButton(""), CC.xyw(2, 8, 3));
        watermarkColor.setBackground(Color.BLACK);
        watermarkColor.addActionListener(e -> {
            Color c = JColorChooser.showDialog(mainWindow, "Select Color", watermarkColor.getBackground());
            if (c != null) {
                watermarkColor.setBackground(c);
            }
        });
        add(new JSeparator(), CC.xyw(1, 9, 4));
        add(pageNumbers = new JCheckBox("Add page numbers"), CC.xyw(1, 10, 4));
        ActionListener pageNumberListener = e -> updatePageNumbersEnabled();
        pageNumbers.addActionListener(pageNumberListener);
        add(new JLabel("Font size:"), CC.xy(1, 11));
        add(pgnoSize = new JTextField("10"), CC.xyw(2, 11, 3));
        add(new JLabel("Horizontal:"), CC.xy(1, 12));
        add(pgnoHOffset = new JTextField("25"), CC.xy(2, 12));
        add(pgnoHRef = new JComboBox<>(new String[]{"PS points from left margin", "PS points from center",
                "PS points from right margin", "PS points from inner margin", "PS points from outer margin"}),
            CC.xyw(3, 12, 2));
        add(new JLabel("Vertical:"), CC.xy(1, 13));
        add(pgnoVOffset = new JTextField("25"), CC.xy(2, 13));
        add(pgnoVRef = new JComboBox<>(new String[]{"PS points from bottom margin", "PS points from center", "PS points from top margin"}),
            CC.xyw(3, 13, 2));
        add(useMask = new JCheckBox("Mask: "), CC.xy(1, 14));
        useMask.addActionListener(pageNumberListener);
        add(maskText = new JTextField("Page %1$d of %2$d"), CC.xyw(2, 14, 3));
        add(new JLabel(
                "<html><tt>%1$d</tt> - page index, <tt>%2$d</tt> - page count, <tt>%3$d</tt> - logical page number, <tt>%4$s</tt> page number text"),
            CC.xyw(2, 15, 3));
        add(differentPageNumbers = new JCheckBox("Use different page numbers"), CC.xyw(1, 16, 2));
        differentPageNumbers.addActionListener(pageNumberListener);
        add(load = new JButton("Load from document"), CC.xyw(3, 16, 2));
        add(pageNumberRanges = PageNumberTab.buildPageNumberRanges(), CC.xyw(1, 17, 4));
        load.addActionListener(new PageNumberTab.PageNumberLoadAction(mf, pageNumberRanges));
        pageNumberRanges.getScrollPane().setPreferredSize(new Dimension(750, 100));
        updatePDFWatermarkEnabled();
        updateTextWatermarkEnabled();
        updatePageNumbersEnabled();
    }

    protected void updatePDFWatermarkEnabled() {
        fileButton.setEnabled(pdfWatermark.isSelected());
        filename.setEnabled(pdfWatermark.isSelected());
    }

    protected void updateTextWatermarkEnabled() {
        watermarkText.setEnabled(textWatermark.isSelected());
        watermarkSize.setEnabled(textWatermark.isSelected());
        watermarkOpacity.setEnabled(textWatermark.isSelected());
        watermarkUseColor.setEnabled(textWatermark.isSelected());
        watermarkColor.setEnabled(textWatermark.isSelected());
    }

    private void updatePageNumbersEnabled() {
        pgnoSize.setEnabled(pageNumbers.isSelected());
        pgnoHOffset.setEnabled(pageNumbers.isSelected());
        pgnoVOffset.setEnabled(pageNumbers.isSelected());
        pgnoHRef.setEnabled(pageNumbers.isSelected());
        pgnoVRef.setEnabled(pageNumbers.isSelected());
        useMask.setEnabled(pageNumbers.isSelected());
        maskText.setEnabled(pageNumbers.isSelected() && useMask.isSelected());
        differentPageNumbers.setEnabled(pageNumbers.isSelected());
        load.setEnabled(pageNumbers.isSelected() && differentPageNumbers.isSelected());
        pageNumberRanges.setEnabled(pageNumbers.isSelected() && differentPageNumbers.isSelected());
    }


    public String getTabName() {
        return "Watermark";
    }


    public void checkRun() throws IOException {
        pageNumberRanges.checkRun("different page number");
    }


    public PDFTwist run(PDFTwist pdfTwist, OutputProgressDialog outDialog) throws IOException, DocumentException {
        outDialog.updateJPDFTwistProgress(getTabName());
        outDialog.setAction("Adding Watermark");
        outDialog.resetProcessedPages();
        boolean run = false;
        String wmText = null;
        int wmSize = 0, pnSize = 0, pnPosition = -1;
        boolean pnFlipEven = false;
        float wmOpacity = 0, pnHOff = 0, pnVOff = 0;
        Color wmColor = null;
        String mask = null;
        try {
            if (pdfWatermark.isSelected()) {
                run = true;
            }
            if (textWatermark.isSelected()) {
                run = true;
                wmText = watermarkText.getText();
                wmSize = Integer.parseInt(watermarkSize.getText());
                wmOpacity = Float.parseFloat(watermarkOpacity.getText());
                if (wmSize == 0) {
                    throw new IOException("Font size may not be zero");
                }
            }
            if (pageNumbers.isSelected()) {
                if (differentPageNumbers.isSelected()) {
                    PageNumberTab.updatePageNumberRanges(pdfTwist, pageNumberRanges, outDialog);
                    mask = "%4$s";
                }
                run = true;
                int hIndex = pgnoHRef.getSelectedIndex();
                if (hIndex > 2) {
                    hIndex = hIndex * 2 - 6;
                    pnFlipEven = true;
                }
                pnPosition = pgnoVRef.getSelectedIndex() * 3 + hIndex;
                pnSize = Integer.parseInt(pgnoSize.getText());
                pnHOff = Float.parseFloat(pgnoHOffset.getText());
                pnVOff = Float.parseFloat(pgnoVOffset.getText());
                if (pnSize == 0) {
                    throw new IOException("Font size may not be zero");
                }
            }
            if (watermarkUseColor.isSelected()) {
                wmColor = watermarkColor.getBackground();
            }
            if (useMask.isSelected()) {
                mask = maskText.getText();
            }
        } catch (NumberFormatException ex) {
            throw new IOException("Cannot parse value: " + ex.getMessage());
        }
        if (run) {
            pdfTwist.addWatermark(filename.getText(), wmText, wmSize, wmOpacity, wmColor, pnPosition, pnFlipEven, pnSize, pnHOff, pnVOff, mask);
        }
        return pdfTwist;
    }
}

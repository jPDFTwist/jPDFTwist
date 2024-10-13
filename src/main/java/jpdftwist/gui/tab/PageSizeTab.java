package jpdftwist.gui.tab;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jpdftwist.core.NumberField;
import jpdftwist.core.OutputEventListener;
import jpdftwist.core.PDFTwist;
import jpdftwist.core.PageBox;
import jpdftwist.core.PageDimension;
import jpdftwist.core.UnitTranslator;
import jpdftwist.core.tabparams.RotateParameters;
import jpdftwist.core.tabparams.ScaleParameters;
import jpdftwist.gui.MainWindow;
import jpdftwist.gui.dialog.ScaleCustomSizeDialog;

import javax.swing.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PageSizeTab extends Tab {
    private JTextField scaleWidth;
    private JTextField scaleHeight;
    private NumberField scalePortraitUpperLimit;
    private NumberField scalePortraitLowerLimit;
    private NumberField scaleLandscapeUpperLimit;
    private NumberField scaleLandscapeLowerLimit;
    private NumberField rotatePortraitUpperLimit;
    private NumberField rotatePortraitLowerLimit;
    private NumberField rotateLandscapeUpperLimit;
    private NumberField rotateLandscapeLowerLimit;
    private JCheckBox rotatePages;
    private JCheckBox fixRotation;
    private JCheckBox cropPages;
    private JCheckBox scalePages;
    private JCheckBox scaleNoPreserve;
    private JCheckBox scaleCenter;
    private JCheckBox scaleConditionPortrait;
    private JCheckBox scaleConditionLandscape;
    private JCheckBox rotateConditionPortrait;
    private JCheckBox rotateConditionLandscape;
    private JCheckBox preserveHyperlinks;
    private JComboBox<String> rotatePortrait;
    private JComboBox<String> rotateLandscape;
    private JComboBox<String> rotatePortraitUnits;
    private JComboBox<String> rotateLandscapeUnits;
    private JComboBox<PageDimension> scaleSize;
    private JComboBox<PageDimension> scaleSizePortrait;
    private JComboBox<PageDimension> scaleSizeLandscape;
    private JComboBox<PageBox> cropTo;
    private JComboBox<String> scaleJustifyPortrait;
    private JComboBox<String> scaleJustifyLandscape;
    private JComboBox<String> scaleJustify;
    private JComboBox<String> scalePortraitUnits;
    private JComboBox<String> scaleLandscapeUnits;
    private ScaleCustomSizeDialog scaleSizeDialog;

    public PageSizeTab(MainWindow mf) {
        super(new FormLayout("f:p, f:p:g, 30dlu, f:p, 30dlu, f:p, f:p, f:p",
            "f:p, 10dlu,f:p,f:p,f:p, f:p, f:p, 10dlu, f:p, 10dlu, f:p,f:p, f:p,f:p, f:p,f:p,f:p,f:p, 10dlu, f:p, f:p:g"));

        initComponents();

        CellConstraints CC = new CellConstraints();

        // Crop
        this.add(cropPages, CC.xy(1, 1));
        this.add(cropTo, CC.xyw(2, 1, 7));

        // Rotate
        this.add(new JSeparator(), CC.xyw(1, 2, 8));
        this.add(rotatePages, CC.xyw(1, 3, 8));
        this.add(rotateConditionPortrait, CC.xyw(1, 4, 2));
        this.add(rotatePortraitLowerLimit, CC.xy(3, 4));
        this.add(new JLabel(" and "), CC.xy(4, 4));
        this.add(rotatePortraitUpperLimit, CC.xy(5, 4));
        this.add(rotatePortraitUnits, CC.xy(6, 4));
        this.add(new JLabel("Portrait pages:"), CC.xy(1, 5));
        this.add(rotatePortrait, CC.xyw(2, 5, 7));
        this.add(rotateConditionLandscape, CC.xyw(1, 6, 2));
        this.add(rotateLandscapeLowerLimit, CC.xy(3, 6));
        this.add(new JLabel(" and "), CC.xy(4, 6));
        this.add(rotateLandscapeUpperLimit, CC.xy(5, 6));
        this.add(rotateLandscapeUnits, CC.xy(6, 6));
        this.add(new JLabel("Landscape pages:"), CC.xy(1, 7));
        this.add(rotateLandscape, CC.xyw(2, 7, 7));

        // Remove Rotation
        this.add(new JSeparator(), CC.xyw(1, 8, 8));
        this.add(fixRotation, CC.xyw(1, 9, 8));

        // Scale
        this.add(new JSeparator(), CC.xyw(1, 10, 8));
        this.add(scalePages, CC.xyw(1, 11, 8));
        this.add(new JLabel("Page Size:"), CC.xy(1, 12));
        this.add(scaleSize, CC.xyw(2, 12, 6));
        this.add(scaleJustify, CC.xy(8, 12));
        this.add(new JLabel("Page Width:"), CC.xy(1, 13));
        this.add(scaleWidth, CC.xyw(2, 13, 7));
        this.add(new JLabel("Page Height:"), CC.xy(1, 14));
        this.add(scaleHeight, CC.xyw(2, 14, 7));
        this.add(scaleConditionPortrait, CC.xyw(1, 15, 2));
        this.add(scalePortraitLowerLimit, CC.xy(3, 15));
        this.add(new JLabel(" and "), CC.xy(4, 15));
        this.add(scalePortraitUpperLimit, CC.xy(5, 15));
        this.add(scalePortraitUnits, CC.xy(6, 15));
        this.add(scaleSizePortrait, CC.xy(7, 15));
        this.add(scaleJustifyPortrait, CC.xy(8, 15));
        this.add(scaleConditionLandscape, CC.xyw(1, 16, 2));
        this.add(scaleLandscapeLowerLimit, CC.xy(3, 16));
        this.add(new JLabel(" and "), CC.xy(4, 16));
        this.add(scaleLandscapeUpperLimit, CC.xy(5, 16));
        this.add(scaleLandscapeUnits, CC.xy(6, 16));
        this.add(scaleSizeLandscape, CC.xy(7, 16));
        this.add(scaleJustifyLandscape, CC.xy(8, 16));
        this.add(scaleCenter, CC.xyw(1, 17, 8));
        this.add(scaleNoPreserve, CC.xyw(1, 18, 8));

        // Preserve Hyperlinks
        this.add(new JSeparator(), CC.xyw(1, 19, 8));
        this.add(preserveHyperlinks, CC.xyw(1, 20, 8));
    }

    private void initComponents() {
        cropPages = new JCheckBox("Crop to:");
        cropTo = new JComboBox<>(new PageBox[]{
            PageBox.MediaBox, PageBox.CropBox, PageBox.BleedBox, PageBox.TrimBox, PageBox.ArtBox});
        cropTo.setSelectedItem(PageBox.CropBox);
        cropTo.setEnabled(false);
        cropPages.addActionListener(e -> cropTo.setEnabled(cropPages.isSelected()));
        rotatePages = new JCheckBox("Rotate pages");
        rotateConditionPortrait = new JCheckBox("Page is Portrait and height is between");
        rotatePortraitLowerLimit = new NumberField("0");
        rotatePortraitUpperLimit = new NumberField("200");
        rotatePortrait = new JComboBox<>(new String[]{"Keep", "Right", "Upside-Down", "Left"});
        rotateConditionLandscape = new JCheckBox("Page is Landscape and width is between");
        rotateLandscapeLowerLimit = new NumberField("0");
        rotateLandscapeUpperLimit = new NumberField("200");
        rotateLandscape = new JComboBox<>(new String[]{"Keep", "Right", "Upside-Down", "Left"});
        rotatePortraitUnits = new JComboBox<>(new String[]{"inches", "mm", "points"});
        rotateLandscapeUnits = new JComboBox<>(new String[]{"inches", "mm", "points"});
        rotatePages.addActionListener(e -> toggleRotateEnabled());
        rotateConditionPortrait.addActionListener(e -> toggleRotateEnabled());
        rotateConditionLandscape.addActionListener(e -> toggleRotateEnabled());

        toggleRotateEnabled();

        fixRotation = new JCheckBox("Remove implicit page rotation");

        scalePages = new JCheckBox("Scale pages");
        scaleSize = new JComboBox<>();
        scaleSizePortrait = new JComboBox<>();
        scaleSizeLandscape = new JComboBox<>();
        scaleWidth = new JTextField();
        scaleHeight = new JTextField();
        scaleConditionPortrait = new JCheckBox("Page is Portrait and height is between");
        scaleConditionPortrait.setToolTipText(scaleConditionPortrait.getText());
        scalePortraitLowerLimit = new NumberField("0");
        scalePortraitUpperLimit = new NumberField("200");

        scaleJustifyPortrait = new JComboBox<>(new String[]{
            "Top Left", "Top Center", "Top Right", "Left", "Center",
            "Right", "Bottom Left", "Bottom Center", "Bottom Right"});
        scaleJustifyPortrait.setSelectedIndex(4);

        scaleJustifyLandscape = new JComboBox<>(new String[]{
            "Top Left", "Top Center", "Top Right", "Left", "Center",
            "Right", "Bottom Left", "Bottom Center", "Bottom Right"});
        scaleJustifyLandscape.setSelectedIndex(4);

        scaleJustify = new JComboBox<>(new String[]{
            "Top Left", "Top Center", "Top Right", "Left", "Center",
            "Right", "Bottom Left", "Bottom Center", "Bottom Right"});
        scaleJustify.setSelectedIndex(4);

        scalePortraitUnits = new JComboBox<>(new String[]{"inches", "mm", "points"});
        scaleLandscapeUnits = new JComboBox<>(new String[]{"inches", "mm", "points"});

        scaleConditionLandscape = new JCheckBox("Page is Landscape and width is between");
        scaleConditionLandscape.setToolTipText(scaleConditionLandscape.getText());
        scaleLandscapeLowerLimit = new NumberField("0");
        scaleLandscapeUpperLimit = new NumberField("200");
        scaleCenter = new JCheckBox("Do not scale contents");
        scaleNoPreserve = new JCheckBox("Do not preserve aspect ratio");

        scalePages.addActionListener(e -> toggleScaleEnabled());
        scaleConditionPortrait.addActionListener(e -> toggleScaleEnabled());
        scaleConditionLandscape.addActionListener(e -> toggleScaleEnabled());

        populateScaleSizeValues(scaleSize);
        populateScaleSizeValues(scaleSizePortrait);
        populateScaleSizeValues(scaleSizeLandscape);

        rotatePortraitLowerLimit.setHorizontalAlignment(JTextField.CENTER);
        rotatePortraitUpperLimit.setHorizontalAlignment(JTextField.CENTER);
        rotateLandscapeLowerLimit.setHorizontalAlignment(JTextField.CENTER);
        rotateLandscapeUpperLimit.setHorizontalAlignment(JTextField.CENTER);

        scalePortraitLowerLimit.setHorizontalAlignment(JTextField.CENTER);
        scalePortraitUpperLimit.setHorizontalAlignment(JTextField.CENTER);
        scaleLandscapeLowerLimit.setHorizontalAlignment(JTextField.CENTER);
        scaleLandscapeUpperLimit.setHorizontalAlignment(JTextField.CENTER);

        toggleScaleEnabled();
        updateScaleSize();

        preserveHyperlinks = new JCheckBox("Preserve annotations (EXPERIMENTAL)");
    }

    private void toggleRotateEnabled() {
        rotatePortrait.setEnabled(rotatePages.isSelected());
        rotateLandscape.setEnabled(rotatePages.isSelected());
        rotateConditionPortrait.setEnabled(rotatePages.isSelected());
        rotatePortraitLowerLimit.setEnabled(rotatePages.isSelected() && rotateConditionPortrait.isSelected());
        rotatePortraitUpperLimit.setEnabled(rotatePages.isSelected() && rotateConditionPortrait.isSelected());
        rotateConditionLandscape.setEnabled(rotatePages.isSelected());
        rotateLandscapeLowerLimit.setEnabled(rotatePages.isSelected() && rotateConditionLandscape.isSelected());
        rotateLandscapeUpperLimit.setEnabled(rotatePages.isSelected() && rotateConditionLandscape.isSelected());
        rotateLandscapeUnits.setEnabled(rotatePages.isSelected() && rotateConditionLandscape.isSelected());
        rotatePortraitUnits.setEnabled(rotatePages.isSelected() && rotateConditionPortrait.isSelected());
        if (!rotatePages.isSelected()) {
            rotatePortrait.setSelectedIndex(0);
            rotateLandscape.setSelectedIndex(0);
        }
    }

    private void toggleScaleEnabled() {
        scaleSize.setEnabled(scalePages.isSelected() && !scaleConditionPortrait.isSelected() && !scaleConditionLandscape.isSelected());
        scaleWidth.setEnabled(scalePages.isSelected() && !scaleConditionPortrait.isSelected() && !scaleConditionLandscape.isSelected());
        scaleHeight.setEnabled(scalePages.isSelected() && !scaleConditionPortrait.isSelected() && !scaleConditionLandscape.isSelected());
        scaleJustify.setEnabled(scalePages.isSelected() && !scaleConditionPortrait.isSelected() && !scaleConditionLandscape.isSelected());
        scaleCenter.setEnabled(scalePages.isSelected());
        scaleNoPreserve.setEnabled(scalePages.isSelected());

        // Portrait Condition
        scaleConditionPortrait.setEnabled(scalePages.isSelected());
        scalePortraitLowerLimit.setEnabled(scalePages.isSelected() && scaleConditionPortrait.isSelected());
        scalePortraitUpperLimit.setEnabled(scalePages.isSelected() && scaleConditionPortrait.isSelected());
        scaleSizePortrait.setEnabled(scalePages.isSelected() && scaleConditionPortrait.isSelected());
        scaleJustifyPortrait.setEnabled(scalePages.isSelected() && scaleConditionPortrait.isSelected());
        scalePortraitUnits.setEnabled(scalePages.isSelected() && scaleConditionPortrait.isSelected());

        // Landscape Condition
        scaleConditionLandscape.setEnabled(scalePages.isSelected());
        scaleLandscapeLowerLimit.setEnabled(scalePages.isSelected() && scaleConditionLandscape.isSelected());
        scaleLandscapeUpperLimit.setEnabled(scalePages.isSelected() && scaleConditionLandscape.isSelected());
        scaleSizeLandscape.setEnabled(scalePages.isSelected() && scaleConditionLandscape.isSelected());
        scaleJustifyLandscape.setEnabled(scalePages.isSelected() && scaleConditionLandscape.isSelected());
        scaleLandscapeUnits.setEnabled(scalePages.isSelected() && scaleConditionLandscape.isSelected());
    }

    private void populateScaleSizeValues(final JComboBox<PageDimension> box) {
        box.setEnabled(false);
        box.addItem(new PageDimension("* Custom Size", -1, -1, false));
        for (PageDimension ps : PageDimension.getCommonSizes()) {
            box.addItem(ps);
        }

        scaleSizeDialog = new ScaleCustomSizeDialog();
        box.addActionListener(e -> {
            if (box.getSelectedIndex() == 0) {
                int customSizePrompt = JOptionPane.showConfirmDialog(null, scaleSizeDialog, " Custom Page Size",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (customSizePrompt == JOptionPane.OK_OPTION) {
                    String newPageSizeName;

                    if (scaleSizeDialog.isPercentage()) {
                        newPageSizeName = String.format("Custom %s%% x %s%%", scaleSizeDialog.getPageWidth(), scaleSizeDialog.getPageHeight());
                    } else {
                        if (scaleSizeDialog.getPagePostscriptWidth() != 0 && scaleSizeDialog.getPagePostscriptHeight() != 0) {
                            newPageSizeName = String.format("Custom %s x %s %s", scaleSizeDialog.getPageWidth(), scaleSizeDialog.getPageHeight(), scaleSizeDialog.getUnitsName());
                        } else {
                            box.setSelectedIndex(1);
                            return;
                        }
                    }

                    PageDimension customDimension = new PageDimension(newPageSizeName,
                        scaleSizeDialog.getPagePostscriptWidth(), scaleSizeDialog.getPagePostscriptHeight(),
                        scaleSizeDialog.isPercentage());

                    scaleSize.addItem(customDimension);
                    scaleSizePortrait.addItem(customDimension);
                    scaleSizeLandscape.addItem(customDimension);
                    box.setSelectedItem(customDimension);
                } else if (customSizePrompt == JOptionPane.CANCEL_OPTION || scaleSizeDialog.getPagePostscriptWidth() == 0 || scaleSizeDialog.getPagePostscriptHeight() == 0) {
                    box.setSelectedIndex(1);
                }
            }
            updateScaleSize();
        });

        box.setSelectedIndex(1);
    }

    protected void updateScaleSize() {
        if (scaleSize.getSelectedIndex() == 0) {
            return;
        }

        double width = scaleSize.getModel().getElementAt(scaleSize.getSelectedIndex()).getWidth();
        double height = scaleSize.getModel().getElementAt(scaleSize.getSelectedIndex()).getHeight();

        scaleWidth.setText(String.valueOf(width));
        scaleHeight.setText(String.valueOf(height));
    }

    private double toPoints(double value, int index) {
        switch (index) {
            case 0: // inches
                return round(value * UnitTranslator.POINT_POSTSCRIPT);
            case 1: // mm
                return round(UnitTranslator.millisToPoints(value));
            default:
                return value;
        }
    }

    private double round(double value) {
        return new BigDecimal(value)
            .setScale(3, RoundingMode.HALF_UP)
            .doubleValue();
    }


    public String getTabName() {
        return "Page Size";
    }


    public PDFTwist run(PDFTwist pdfTwist, OutputEventListener outputEventListener) throws IOException, DocumentException {
        outputEventListener.updateJPDFTwistProgress(getTabName());
        if (preserveHyperlinks.isSelected()) {
            pdfTwist.preserveHyperlinks();
        }
        if (cropPages.isSelected()) {
            pdfTwist.cropPages((PageBox) cropTo.getSelectedItem());
        }
        if (rotatePages.isSelected()) {
            RotateParameters rotateParams = new RotateParameters();
            rotateParams.setLandscapeCount(rotateLandscape.getSelectedIndex());
            rotateParams.setPortraitCount(rotatePortrait.getSelectedIndex());
            rotateParams.setIsLandscape(rotateConditionLandscape.isSelected());
            rotateParams.setIsPortrait(rotateConditionPortrait.isSelected());

            double[] landscapeLimits = new double[2];
            landscapeLimits[0] = toPoints(Double.parseDouble(rotateLandscapeLowerLimit.getText()), rotateLandscapeUnits.getSelectedIndex());
            landscapeLimits[1] = toPoints(Double.parseDouble(rotateLandscapeUpperLimit.getText()), rotateLandscapeUnits.getSelectedIndex());
            rotateParams.setLandscapeLimits(landscapeLimits);

            double[] portraitLimits = new double[2];
            portraitLimits[0] = toPoints(Double.parseDouble(rotatePortraitLowerLimit.getText()), rotatePortraitUnits.getSelectedIndex());
            portraitLimits[1] = toPoints(Double.parseDouble(rotatePortraitUpperLimit.getText()), rotatePortraitUnits.getSelectedIndex());
            rotateParams.setPortraitLimits(portraitLimits);

            pdfTwist.rotatePages(rotateParams);
        }
        if (fixRotation.isSelected()) {
            pdfTwist.removeRotation();
        }
        if (scalePages.isSelected()) {
            try {
                Float.parseFloat(scaleWidth.getText());
                Float.parseFloat(scaleHeight.getText());
            } catch (NumberFormatException ex) {
                Logger.getLogger(PageSizeTab.class.getName()).log(Level.SEVERE, "Ex067", ex);
                throw new IOException("Invalid scale size");
            }

            ScaleParameters scaleParams = new ScaleParameters();
            scaleParams.setNoEnlarge(scaleCenter.isSelected());
            scaleParams.setPreserveAspectRatio(!scaleNoPreserve.isSelected());
            scaleParams.setIsPortrait(scaleConditionPortrait.isSelected());
            scaleParams.setIsLandscape(scaleConditionLandscape.isSelected());

            scaleParams.setLandscapeLimits(new double[] {
                toPoints(Double.parseDouble(scaleLandscapeLowerLimit.getText()), scaleLandscapeUnits.getSelectedIndex()),
                toPoints(Double.parseDouble(scaleLandscapeUpperLimit.getText()), scaleLandscapeUnits.getSelectedIndex())
            });

            scaleParams.setPortraitLimits(new double[] {
                toPoints(Double.parseDouble(scalePortraitLowerLimit.getText()), scaleLandscapeUnits.getSelectedIndex()),
                toPoints(Double.parseDouble(scalePortraitUpperLimit.getText()), scaleLandscapeUnits.getSelectedIndex())
            });

            float width = Float.parseFloat(scaleWidth.getText());
            float height = Float.parseFloat(scaleHeight.getText());
            PageDimension pageDim = new PageDimension("Final", new Rectangle(width, height), false,
                scaleSize.getModel().getElementAt(scaleSize.getSelectedIndex()).isPercentange());
            scaleParams.setPageDim(pageDim);
            scaleParams.setPortraitPageDim((PageDimension) scaleSizePortrait.getSelectedItem());
            scaleParams.setLandscapePageDim((PageDimension) scaleSizeLandscape.getSelectedItem());

            scaleParams.setJustify(scaleJustify.getSelectedIndex());
            scaleParams.setJustifyPortrait(scaleJustifyPortrait.getSelectedIndex());
            scaleParams.setJustifyLandscape(scaleJustifyLandscape.getSelectedIndex());

            pdfTwist.scalePages(scaleParams);
        }
        return pdfTwist;
    }
}

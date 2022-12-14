package jpdftwist.gui.tab;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jpdftwist.core.NumberField;
import jpdftwist.core.PDFTwist;
import jpdftwist.core.PDFTwist.PageBox;
import jpdftwist.core.PageDimension;
import jpdftwist.core.UnitTranslator;
import jpdftwist.core.tabparams.RotateParameters;
import jpdftwist.core.tabparams.ScaleParameters;
import jpdftwist.gui.MainWindow;
import jpdftwist.gui.dialog.OutputProgressDialog;
import jpdftwist.gui.dialog.ScaleCustomSizeDialog;
import jpdftwist.tabs.Tab;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;

public class PageSizeTab extends Tab {
    private JTextField scaleWidth, scaleHeight;
    private NumberField scalePortraitUpperLimit, scalePortraitLowerLimit, scaleLandscapeUpperLimit,
        scaleLandscapeLowerLimit, rotatePortraitUpperLimit, rotatePortraitLowerLimit, rotateLandscapeUpperLimit,
        rotateLandscapeLowerLimit;
    private JCheckBox rotatePages, fixRotation, cropPages, scalePages, scaleNoPreserve, scaleCenter,
        scaleConditionPortait, scaleConditionLandscape, rotateConditionPortait, rotateConditionLandscape,
        preserveHyperlinks;
    private JComboBox rotatePortrait, rotateLandscape, rotatePortraitUnits, rotateLandscapeUnits, scaleSize, scaleSize2,
        scaleSize3, cropTo, scaleJustifyPortrait, scaleJustifyLandscape, scaleJustify, scalePortraitUnits,
        scaleLandscapeUnits;
    private ScaleCustomSizeDialog scaleSizeDialog;

    private void initComponents() {
        cropPages = new JCheckBox("Crop to:");
        cropTo = new JComboBox(
            new PageBox[]{PageBox.MediaBox, PageBox.CropBox, PageBox.BleedBox, PageBox.TrimBox, PageBox.ArtBox});
        cropTo.setSelectedItem(PageBox.CropBox);
        cropTo.setEnabled(false);

        cropPages.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cropTo.setEnabled(cropPages.isSelected());
            }
        });

        rotatePages = new JCheckBox("Rotate pages");
        rotateConditionPortait = new JCheckBox("Page is Portrait and height is between");
        rotatePortraitLowerLimit = new NumberField("0");
        rotatePortraitUpperLimit = new NumberField("200");
        rotatePortrait = new JComboBox(new String[]{"Keep", "Right", "Upside-Down", "Left"});
        rotateConditionLandscape = new JCheckBox("Page is Landscape and width is between");
        rotateLandscapeLowerLimit = new NumberField("0");
        rotateLandscapeUpperLimit = new NumberField("200");
        rotateLandscape = new JComboBox(new String[]{"Keep", "Right", "Upside-Down", "Left"});

        rotatePortraitUnits = new JComboBox(new Object[]{"inches", "mm", "points"});

        rotateLandscapeUnits = new JComboBox(new Object[]{"inches", "mm", "points"});

        rotatePages.addActionListener(new ActionListener() {


            public void actionPerformed(ActionEvent e) {
                rotatePortrait.setEnabled(rotatePages.isSelected());
                rotateLandscape.setEnabled(rotatePages.isSelected());
                rotateConditionPortait.setEnabled(rotatePages.isSelected());
                rotatePortraitLowerLimit.setEnabled(rotatePages.isSelected() && rotateConditionPortait.isSelected());
                rotatePortraitUpperLimit.setEnabled(rotatePages.isSelected() && rotateConditionPortait.isSelected());
                rotateConditionLandscape.setEnabled(rotatePages.isSelected());
                rotateLandscapeLowerLimit.setEnabled(rotatePages.isSelected() && rotateConditionLandscape.isSelected());
                rotateLandscapeUpperLimit.setEnabled(rotatePages.isSelected() && rotateConditionLandscape.isSelected());
                rotateLandscapeUnits.setEnabled(rotatePages.isSelected() && rotateConditionLandscape.isSelected());
                rotatePortraitUnits.setEnabled(rotatePages.isSelected() && rotateConditionPortait.isSelected());
                if (!rotatePages.isSelected()) {
                    rotatePortrait.setSelectedIndex(0);
                    rotateLandscape.setSelectedIndex(0);
                }
            }
        });

        rotateConditionPortait.addActionListener(e -> {
            rotatePortraitLowerLimit.setEnabled(rotatePages.isSelected() && rotateConditionPortait.isSelected());
            rotatePortraitUpperLimit.setEnabled(rotatePages.isSelected() && rotateConditionPortait.isSelected());
            rotatePortraitUnits.setEnabled(rotatePages.isSelected() && rotateConditionPortait.isSelected());
        });

        rotateConditionLandscape.addActionListener(e -> {
            rotateLandscapeLowerLimit.setEnabled(rotatePages.isSelected() && rotateConditionLandscape.isSelected());
            rotateLandscapeUpperLimit.setEnabled(rotatePages.isSelected() && rotateConditionLandscape.isSelected());
            rotateLandscapeUnits.setEnabled(rotatePages.isSelected() && rotateConditionLandscape.isSelected());
        });

        rotatePortrait.setEnabled(false);
        rotateLandscape.setEnabled(false);
        rotateConditionPortait.setEnabled(false);
        rotatePortraitLowerLimit.setEnabled(false);
        rotatePortraitUpperLimit.setEnabled(false);
        rotateConditionLandscape.setEnabled(false);
        rotateLandscapeLowerLimit.setEnabled(false);
        rotateLandscapeUpperLimit.setEnabled(false);
        rotatePortraitUnits.setEnabled(false);
        rotateLandscapeUnits.setEnabled(false);

        fixRotation = new JCheckBox("Remove implicit page rotation");

        scalePages = new JCheckBox("Scale pages");
        scaleSize = new JComboBox();
        scaleSize2 = new JComboBox();
        scaleSize3 = new JComboBox();
        scaleWidth = new JTextField();
        scaleHeight = new JTextField();
        scaleConditionPortait = new JCheckBox("Page is Portrait and height is between");
        scaleConditionPortait.setToolTipText(scaleConditionPortait.getText());
        scalePortraitLowerLimit = new NumberField("0");
        scalePortraitUpperLimit = new NumberField("200");

        scaleJustifyPortrait = new JComboBox(new Object[]{"Top Left", "Top Center", "Top Right", "Left", "Center",
            "Right", "Bottom Left", "Bottom Center", "Bottom Right"});
        scaleJustifyPortrait.setSelectedIndex(4);

        scaleJustifyLandscape = new JComboBox(new Object[]{"Top Left", "Top Center", "Top Right", "Left", "Center",
            "Right", "Bottom Left", "Bottom Center", "Bottom Right"});
        scaleJustifyLandscape.setSelectedIndex(4);

        scaleJustify = new JComboBox(new Object[]{"Top Left", "Top Center", "Top Right", "Left", "Center", "Right",
            "Bottom Left", "Bottom Center", "Bottom Right"});
        scaleJustify.setSelectedIndex(4);

        scalePortraitUnits = new JComboBox(new Object[]{"inches", "mm", "points"});

        scaleLandscapeUnits = new JComboBox(new Object[]{"inches", "mm", "points"});

        scaleConditionLandscape = new JCheckBox("Page is Landscape and width is between");
        scaleConditionLandscape.setToolTipText(scaleConditionLandscape.getText());
        scaleLandscapeLowerLimit = new NumberField("0");
        scaleLandscapeUpperLimit = new NumberField("200");
        scaleCenter = new JCheckBox("Do not scale contents");
        scaleNoPreserve = new JCheckBox("Do not preserve aspect ratio");

        scalePages.addActionListener(e -> {
            scaleSize.setEnabled(scalePages.isSelected() && !scaleConditionPortait.isSelected()
                && !scaleConditionLandscape.isSelected());
            scaleWidth.setEnabled(scalePages.isSelected() && !scaleConditionPortait.isSelected()
                && !scaleConditionLandscape.isSelected());
            scaleHeight.setEnabled(scalePages.isSelected() && !scaleConditionPortait.isSelected()
                && !scaleConditionLandscape.isSelected());
            scaleConditionPortait.setEnabled(scalePages.isSelected());
            scalePortraitLowerLimit.setEnabled(scalePages.isSelected() && scaleConditionPortait.isSelected());
            scalePortraitUpperLimit.setEnabled(scalePages.isSelected() && scaleConditionPortait.isSelected());
            scaleSize2.setEnabled(scalePages.isSelected() && scaleConditionPortait.isSelected());
            scaleConditionLandscape.setEnabled(scalePages.isSelected());
            scaleLandscapeLowerLimit.setEnabled(scalePages.isSelected() && scaleConditionLandscape.isSelected());
            scaleLandscapeUpperLimit.setEnabled(scalePages.isSelected() && scaleConditionLandscape.isSelected());
            scaleSize3.setEnabled(scalePages.isSelected() && scaleConditionLandscape.isSelected());
            scaleCenter.setEnabled(scalePages.isSelected());
            scaleNoPreserve.setEnabled(scalePages.isSelected());
            scaleJustifyPortrait.setEnabled(scalePages.isSelected() && scaleConditionPortait.isSelected());
            scaleJustifyLandscape.setEnabled(scalePages.isSelected() && scaleConditionLandscape.isSelected());
            scaleJustify.setEnabled(scalePages.isSelected());
            scaleLandscapeUnits.setEnabled(scalePages.isSelected() && scaleConditionLandscape.isSelected());
            scalePortraitUnits.setEnabled(scalePages.isSelected() && scaleConditionPortait.isSelected());
        });

        scaleConditionPortait.addActionListener(e -> {
            scaleSize.setEnabled(scalePages.isSelected() && !scaleConditionPortait.isSelected()
                && !scaleConditionLandscape.isSelected());
            scaleWidth.setEnabled(scalePages.isSelected() && !scaleConditionPortait.isSelected()
                && !scaleConditionLandscape.isSelected());
            scaleHeight.setEnabled(scalePages.isSelected() && !scaleConditionPortait.isSelected()
                && !scaleConditionLandscape.isSelected());
            scaleJustify.setEnabled(scalePages.isSelected() && !scaleConditionPortait.isSelected()
                && !scaleConditionLandscape.isSelected());
            scalePortraitLowerLimit.setEnabled(scalePages.isSelected() && scaleConditionPortait.isSelected());
            scalePortraitUpperLimit.setEnabled(scalePages.isSelected() && scaleConditionPortait.isSelected());
            scaleSize2.setEnabled(scalePages.isSelected() && scaleConditionPortait.isSelected());
            scaleJustifyPortrait.setEnabled(scalePages.isSelected() && scaleConditionPortait.isSelected());
            scalePortraitUnits.setEnabled(scalePages.isSelected() && scaleConditionPortait.isSelected());
        });

        scaleConditionLandscape.addActionListener(e -> {
            scaleSize.setEnabled(scalePages.isSelected() && !scaleConditionPortait.isSelected()
                && !scaleConditionLandscape.isSelected());
            scaleWidth.setEnabled(scalePages.isSelected() && !scaleConditionPortait.isSelected()
                && !scaleConditionLandscape.isSelected());
            scaleHeight.setEnabled(scalePages.isSelected() && !scaleConditionPortait.isSelected()
                && !scaleConditionLandscape.isSelected());
            scaleJustify.setEnabled(scalePages.isSelected() && !scaleConditionPortait.isSelected()
                && !scaleConditionLandscape.isSelected());
            scaleLandscapeLowerLimit.setEnabled(scalePages.isSelected() && scaleConditionLandscape.isSelected());
            scaleLandscapeUpperLimit.setEnabled(scalePages.isSelected() && scaleConditionLandscape.isSelected());
            scaleSize3.setEnabled(scalePages.isSelected() && scaleConditionLandscape.isSelected());
            scaleJustifyLandscape.setEnabled(scalePages.isSelected() && scaleConditionLandscape.isSelected());
            scaleLandscapeUnits.setEnabled(scalePages.isSelected() && scaleConditionLandscape.isSelected());
        });

        JComboBox[] sizes = new JComboBox[3];
        sizes[0] = scaleSize;
        sizes[1] = scaleSize2;
        sizes[2] = scaleSize3;

        for (final JComboBox box : sizes) {
            box.setEnabled(false);
            box.addItem("* Custom Size");
            for (PageDimension ps : PageDimension.getCommonSizes()) {
                box.addItem(ps);
            }
            scaleSizeDialog = new ScaleCustomSizeDialog();
            box.addActionListener(e -> {
                if (box.getSelectedIndex() == 0) {
                    int customSize = JOptionPane.showConfirmDialog(null, scaleSizeDialog, " Custom Page Size",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if (customSize == JOptionPane.OK_OPTION) {

                        String newPageSizeName = "";

                        if (scaleSizeDialog.isPercentage()) {
                            newPageSizeName = "custom ".concat(scaleSizeDialog.getPageWidth()).concat("% x ")
                                .concat(scaleSizeDialog.getPageHeight()).concat("%");
                        } else {
                            if (scaleSizeDialog.getPagePostscriptWidth() != 0
                                && scaleSizeDialog.getPagePostscriptHeight() != 0) {

                                newPageSizeName = "custom ".concat(scaleSizeDialog.getPageWidth()).concat(" x ")
                                    .concat(scaleSizeDialog.getPageHeight()).concat(" ")
                                    .concat(scaleSizeDialog.getUnitsName());
                            } else {
                                box.setSelectedIndex(1);
                                return;
                            }
                        }

                        PageDimension customDimension = new PageDimension(newPageSizeName,
                            scaleSizeDialog.getPagePostscriptWidth(), scaleSizeDialog.getPagePostscriptHeight(),
                            scaleSizeDialog.isPercentage());

                        scaleSize.addItem(customDimension);
                        scaleSize2.addItem(customDimension);
                        scaleSize3.addItem(customDimension);
                        box.setSelectedItem(customDimension);
                    } else if (customSize == JOptionPane.CANCEL_OPTION
                        || scaleSizeDialog.getPagePostscriptWidth() == 0
                        || scaleSizeDialog.getPagePostscriptHeight() == 0) {
                        box.setSelectedIndex(1);
                    }
                }
                updateScaleSize();
            });
            box.setSelectedIndex(1);
        }
        scaleSize.setSelectedIndex(1);

        rotatePortraitLowerLimit.setHorizontalAlignment(JTextField.CENTER);
        rotatePortraitUpperLimit.setHorizontalAlignment(JTextField.CENTER);
        rotateLandscapeLowerLimit.setHorizontalAlignment(JTextField.CENTER);
        rotateLandscapeUpperLimit.setHorizontalAlignment(JTextField.CENTER);

        scalePortraitLowerLimit.setHorizontalAlignment(JTextField.CENTER);
        scalePortraitUpperLimit.setHorizontalAlignment(JTextField.CENTER);
        scaleLandscapeLowerLimit.setHorizontalAlignment(JTextField.CENTER);
        scaleLandscapeUpperLimit.setHorizontalAlignment(JTextField.CENTER);

        scaleWidth.setEnabled(false);
        scaleHeight.setEnabled(false);
        scaleConditionPortait.setEnabled(false);
        scalePortraitUpperLimit.setEnabled(false);
        scalePortraitLowerLimit.setEnabled(false);
        scaleConditionLandscape.setEnabled(false);
        scaleLandscapeUpperLimit.setEnabled(false);
        scaleLandscapeLowerLimit.setEnabled(false);
        scaleCenter.setEnabled(false);
        scaleNoPreserve.setEnabled(false);
        scaleJustifyPortrait.setEnabled(false);
        scaleJustifyLandscape.setEnabled(false);
        scaleJustify.setEnabled(false);
        scalePortraitUnits.setEnabled(false);
        scaleLandscapeUnits.setEnabled(false);

        updateScaleSize();

        preserveHyperlinks = new JCheckBox("Preserve annotations (EXPERIMENTAL)");
    }

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
        this.add(rotateConditionPortait, CC.xyw(1, 4, 2));
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
        this.add(scaleConditionPortait, CC.xyw(1, 15, 2));
        this.add(scalePortraitLowerLimit, CC.xy(3, 15));
        this.add(new JLabel(" and "), CC.xy(4, 15));
        this.add(scalePortraitUpperLimit, CC.xy(5, 15));
        this.add(scalePortraitUnits, CC.xy(6, 15));
        this.add(scaleSize2, CC.xy(7, 15));
        this.add(scaleJustifyPortrait, CC.xy(8, 15));
        this.add(scaleConditionLandscape, CC.xyw(1, 16, 2));
        this.add(scaleLandscapeLowerLimit, CC.xy(3, 16));
        this.add(new JLabel(" and "), CC.xy(4, 16));
        this.add(scaleLandscapeUpperLimit, CC.xy(5, 16));
        this.add(scaleLandscapeUnits, CC.xy(6, 16));
        this.add(scaleSize3, CC.xy(7, 16));
        this.add(scaleJustifyLandscape, CC.xy(8, 16));
        this.add(scaleCenter, CC.xyw(1, 17, 8));
        this.add(scaleNoPreserve, CC.xyw(1, 18, 8));

        // Preserve Hyperlinks
        this.add(new JSeparator(), CC.xyw(1, 19, 8));
        this.add(preserveHyperlinks, CC.xyw(1, 20, 8));
    }

    protected void updateScaleSize() {
        if (scaleSize.getSelectedIndex() == 0) {
            return;
        }

        double width = ((PageDimension) scaleSize.getSelectedItem()).getWidth();
        double height = ((PageDimension) scaleSize.getSelectedItem()).getHeight();

        scaleWidth.setText("" + width);
        scaleHeight.setText("" + height);
    }

    private double toPoints(double value, int index) {
        double valueToPoints = value;

        switch (index) {
            case 0: // inches
                valueToPoints = round(value * UnitTranslator.POINT_POSTSCRIPT, 3);
                break;
            case 1: // mm
                valueToPoints = round(UnitTranslator.millisToPoints(value), 3);
                break;
        }

        return valueToPoints;
    }

    private double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }


    public String getTabName() {
        return "Page Size";
    }


    public PDFTwist run(PDFTwist pdfTwist, OutputProgressDialog outDialog) throws IOException, DocumentException {
        outDialog.updateJPDFTwistProgress(getTabName());
        if (preserveHyperlinks.isSelected()) {
            pdfTwist.preserveHyperlinks();
        }
        if (cropPages.isSelected()) {
            pdfTwist.cropPages((PageBox) cropTo.getSelectedItem(), outDialog);
        }
        if (rotatePages.isSelected()) {
            RotateParameters rotateParams = new RotateParameters();
            rotateParams.setLandscapeCount(rotateLandscape.getSelectedIndex());
            rotateParams.setPortraitCount(rotatePortrait.getSelectedIndex());
            rotateParams.setIsLandscape(rotateConditionLandscape.isSelected());
            rotateParams.setIsPortrait(rotateConditionPortait.isSelected());

            double[] landscapeLimits = new double[2];
            landscapeLimits[0] = toPoints(Double.parseDouble(rotateLandscapeLowerLimit.getText()),
                rotateLandscapeUnits.getSelectedIndex());
            landscapeLimits[1] = toPoints(Double.parseDouble(rotateLandscapeUpperLimit.getText()),
                rotateLandscapeUnits.getSelectedIndex());
            rotateParams.setLandscapeLimits(landscapeLimits);

            double[] portraitLimits = new double[2];
            portraitLimits[0] = toPoints(Double.parseDouble(rotatePortraitLowerLimit.getText()),
                rotatePortraitUnits.getSelectedIndex());
            portraitLimits[1] = toPoints(Double.parseDouble(rotatePortraitUpperLimit.getText()),
                rotatePortraitUnits.getSelectedIndex());
            rotateParams.setPortraitLimits(portraitLimits);

            pdfTwist.rotatePages(rotateParams, outDialog);
        }
        if (fixRotation.isSelected()) {
            pdfTwist.removeRotation(outDialog);
        }
        if (scalePages.isSelected()) {
            float ww, hh;
            try {
                ww = Float.parseFloat(scaleWidth.getText());
                hh = Float.parseFloat(scaleHeight.getText());
            } catch (NumberFormatException ex) {
                throw new IOException("Invalid scale size");
            }

            ScaleParameters scaleParams = new ScaleParameters();
            scaleParams.setNoEnlarge(scaleCenter.isSelected());
            scaleParams.setPreserveAspectRatio(!scaleNoPreserve.isSelected());
            scaleParams.setIsPortrait(scaleConditionPortait.isSelected());
            scaleParams.setIsLandscape(scaleConditionLandscape.isSelected());

            double[] landscapeLimits = new double[2];
            landscapeLimits[0] = toPoints(Double.parseDouble(scaleLandscapeLowerLimit.getText()),
                scaleLandscapeUnits.getSelectedIndex());
            landscapeLimits[1] = toPoints(Double.parseDouble(scaleLandscapeUpperLimit.getText()),
                scaleLandscapeUnits.getSelectedIndex());
            scaleParams.setLandscapeLimits(landscapeLimits);

            double[] portraitLimits = new double[2];
            portraitLimits[0] = toPoints(Double.parseDouble(scalePortraitLowerLimit.getText()),
                scalePortraitUnits.getSelectedIndex());
            portraitLimits[1] = toPoints(Double.parseDouble(scalePortraitUpperLimit.getText()),
                scalePortraitUnits.getSelectedIndex());
            scaleParams.setPortraitLimits(portraitLimits);

            float width = Float.parseFloat(scaleWidth.getText());
            float height = Float.parseFloat(scaleHeight.getText());
            PageDimension pageDim = new PageDimension("Final", new Rectangle(width, height), false,
                ((PageDimension) scaleSize.getSelectedItem()).isPercentange());
            scaleParams.setPageDim(pageDim);
            scaleParams.setPortraitPageDim((PageDimension) scaleSize2.getSelectedItem());
            scaleParams.setLandscapePageDim((PageDimension) scaleSize3.getSelectedItem());

            scaleParams.setJustify(scaleJustify.getSelectedIndex());
            scaleParams.setJustifyPortrait(scaleJustifyPortrait.getSelectedIndex());
            scaleParams.setJustifyLandscape(scaleJustifyLandscape.getSelectedIndex());

            pdfTwist.scalePages(scaleParams, outDialog);
        }
        return pdfTwist;
    }
}

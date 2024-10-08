/**
 * Original Functions		@author Michael Schierl					Affero GNU Public License
 * Additional Functions		@author & @sponsor: E.Victor			Proprietary for in-house use only / Not released to the Public
 */
package jpdftwist.gui.tab.watermark;

import jpdftwist.core.watermark.WatermarkStyle;
import jpdftwist.core.watermark.WatermarkStyle.HorizontalAlign;
import jpdftwist.gui.component.ColorChooserButton;
import jpdftwist.gui.tab.watermark.WatermarkPreviewComponent.PreviewModel;
import say.swing.JFontChooser;

import java.awt.*;
import java.awt.event.ItemEvent;

public class WatermarkOptionsPanel extends javax.swing.JPanel {

    private javax.swing.JSpinner angleSpinner;
    private ColorChooserButton backgroundButton;
    private javax.swing.JCheckBox backgroundCheckBox;
    private javax.swing.JToggleButton bottomAlignButton;
    private javax.swing.JToggleButton centerAlignButton;
    private javax.swing.JButton fontButton;
    private ColorChooserButton fontColorButton;
    private javax.swing.JSpinner heightSpinner;
    private javax.swing.JComboBox<String> horizontalReference;
    private javax.swing.JSpinner horizontalSpinner;
    private say.swing.JFontChooser jFontChooser1;
    private javax.swing.JToggleButton leftAlignButton;
    private javax.swing.JToggleButton middleAlignButton;
    private javax.swing.JSpinner opacitySpinner;
    private javax.swing.JToggleButton rightAlignButton;
    private javax.swing.JCheckBox strikethroughCheckBox;
    private javax.swing.JToggleButton topAlignButton;
    private javax.swing.JComboBox<String> typeComboBox;
    private javax.swing.JCheckBox underlineCheckBox;
    private javax.swing.JComboBox<String> unitsComboBox;
    private javax.swing.JComboBox<String> verticalReference;
    private javax.swing.JSpinner verticalSpinner;
    private javax.swing.JSpinner widthSpinner;
    private PreviewModel previewModel;
    private WatermarkStyle style;

    private WatermarkPlusTabPanel.StyleChangeListener styleChangeListener;

    /**
     * Creates new form watermarkOptionsPanel
     */
    public WatermarkOptionsPanel() {
        initComponents();

        fontColorButton.addColorChangedListener(newColor -> {
            previewModel.setColor(newColor);
            if (style != null)
                style.setFontColor(newColor);
        });

        backgroundButton.addColorChangedListener(newColor -> {
            if (backgroundCheckBox.isSelected()) {
                previewModel.setBackgroundColor(newColor);
            } else {
                previewModel.setBackgroundColor(null);
            }
            if (style != null)
                style.setBackgroundColor(newColor);
        });
    }

    private void initComponents() {

        javax.swing.ButtonGroup buttonGroup1 = new javax.swing.ButtonGroup();
        jFontChooser1 = new say.swing.JFontChooser();
        javax.swing.ButtonGroup buttonGroup2 = new javax.swing.ButtonGroup();
        fontButton = new javax.swing.JButton();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        angleSpinner = new javax.swing.JSpinner();
        underlineCheckBox = new javax.swing.JCheckBox();
        strikethroughCheckBox = new javax.swing.JCheckBox();
        javax.swing.JSeparator jSeparator1 = new javax.swing.JSeparator();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        widthSpinner = new javax.swing.JSpinner();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        heightSpinner = new javax.swing.JSpinner();
        javax.swing.JSeparator jSeparator2 = new javax.swing.JSeparator();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        horizontalReference = new javax.swing.JComboBox<>();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        verticalReference = new javax.swing.JComboBox<>();
        javax.swing.JSeparator jSeparator3 = new javax.swing.JSeparator();
        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        opacitySpinner = new javax.swing.JSpinner();
        backgroundCheckBox = new javax.swing.JCheckBox();
        fontColorButton = new ColorChooserButton();
        backgroundButton = new ColorChooserButton();
        horizontalSpinner = new javax.swing.JSpinner();
        verticalSpinner = new javax.swing.JSpinner();
        leftAlignButton = new javax.swing.JToggleButton();
        centerAlignButton = new javax.swing.JToggleButton();
        rightAlignButton = new javax.swing.JToggleButton();
        topAlignButton = new javax.swing.JToggleButton();
        middleAlignButton = new javax.swing.JToggleButton();
        bottomAlignButton = new javax.swing.JToggleButton();
        typeComboBox = new javax.swing.JComboBox<>();
        javax.swing.JLabel jLabel10 = new javax.swing.JLabel();
        javax.swing.JSeparator jSeparator4 = new javax.swing.JSeparator();
        javax.swing.JToggleButton justifyButton = new javax.swing.JToggleButton();
        javax.swing.JLabel jLabel11 = new javax.swing.JLabel();
        unitsComboBox = new javax.swing.JComboBox<>();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        fontButton.setFont(new java.awt.Font("Ubuntu", Font.PLAIN, 12));
        fontButton.setText("Arial 12px");
        fontButton.addActionListener(this::fontButtonActionPerformed);

        jLabel1.setText("Font:");

        jLabel2.setText("Font color:");

        jLabel3.setText("Background:");

        jLabel4.setText("Angle:");

        angleSpinner.addChangeListener(this::angleSpinnerStateChanged);

        underlineCheckBox.setText("Underline");
        underlineCheckBox.addItemListener(this::underlineCheckBoxItemStateChanged);

        strikethroughCheckBox.setText("Strikethrough");
        strikethroughCheckBox.addItemListener(this::strikethroughCheckBoxItemStateChanged);

        jLabel5.setText("Width:");

        widthSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1.0f));
        widthSpinner.addChangeListener(this::widthSpinnerStateChanged);

        jLabel6.setText("Height:");

        heightSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0f, 0.0f, null, 1.0f));
        heightSpinner.addChangeListener(this::heightSpinnerStateChanged);

        jLabel7.setText("Horizontal:");

        horizontalReference.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"From Left", "Center", "From Right"}));
        horizontalReference.addItemListener(this::horizontalReferenceItemStateChanged);

        jLabel8.setText("Vertical:");

        verticalReference.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"From Top", "Center", "From Bottom"}));
        verticalReference.addItemListener(this::verticalReferenceItemStateChanged);

        jLabel9.setText("Transparency %");

        opacitySpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        opacitySpinner.addChangeListener(this::opacitySpinnerStateChanged);

        backgroundCheckBox.setText("Background");
        backgroundCheckBox.addItemListener(this::backgroundCheckBoxItemStateChanged);

        fontColorButton.setSelectedColor(new java.awt.Color(255, 0, 0));

        backgroundButton.setSelectedColor(new java.awt.Color(4, 255, 0));

        horizontalSpinner
            .setModel(new javax.swing.SpinnerNumberModel(0.0f, null, null, 1.0f));
        horizontalSpinner.addChangeListener(this::horizontalSpinnerStateChanged);

        verticalSpinner
            .setModel(new javax.swing.SpinnerNumberModel(0.0f, null, null, 1.0f));
        verticalSpinner.addChangeListener(this::verticalSpinnerStateChanged);

        buttonGroup1.add(leftAlignButton);
        leftAlignButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-left-icon.png")));
        leftAlignButton.setSelected(true);
        leftAlignButton.setToolTipText("left align");
        leftAlignButton.setBorderPainted(false);
        leftAlignButton.setContentAreaFilled(false);
        leftAlignButton
            .setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-left-icon2.png")));
        leftAlignButton
            .setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-left-icon3.png")));
        leftAlignButton.addItemListener(this::leftAlignButtonItemStateChanged);

        buttonGroup1.add(centerAlignButton);
        centerAlignButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-center-icon.png")));
        centerAlignButton.setToolTipText("center align");
        centerAlignButton.setBorderPainted(false);
        centerAlignButton.setContentAreaFilled(false);
        centerAlignButton
            .setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-center-icon2.png")));
        centerAlignButton
            .setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-center-icon3.png")));
        centerAlignButton.addItemListener(this::centerAlignButtonItemStateChanged);

        buttonGroup1.add(rightAlignButton);
        rightAlignButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-right-icon.png")));
        rightAlignButton.setToolTipText("right align");
        rightAlignButton.setBorderPainted(false);
        rightAlignButton.setContentAreaFilled(false);
        rightAlignButton
            .setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-right-icon2.png")));
        rightAlignButton
            .setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-right-icon3.png")));
        rightAlignButton.addItemListener(this::rightAlignButtonItemStateChanged);

        buttonGroup2.add(topAlignButton);
        topAlignButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-top.png")));
        topAlignButton.setSelected(true);
        topAlignButton.setToolTipText("top align");
        topAlignButton.setBorderPainted(false);
        topAlignButton.setContentAreaFilled(false);
        topAlignButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-top-rollover.png")));
        topAlignButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-top-pressed.png")));
        topAlignButton.addItemListener(this::topAlignButtonItemStateChanged);

        buttonGroup2.add(middleAlignButton);
        middleAlignButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-middle.png")));
        middleAlignButton.setToolTipText("middle align");
        middleAlignButton.setBorderPainted(false);
        middleAlignButton.setContentAreaFilled(false);
        middleAlignButton
            .setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-middle-rollover.png")));
        middleAlignButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-middle-pressed.png")));
        middleAlignButton.addItemListener(this::middleAlignButtonItemStateChanged);

        buttonGroup2.add(bottomAlignButton);
        bottomAlignButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-bottom.png")));
        bottomAlignButton.setToolTipText("bottom align");
        bottomAlignButton.setBorderPainted(false);
        bottomAlignButton.setContentAreaFilled(false);
        bottomAlignButton
            .setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-bottom-rollover.png")));
        bottomAlignButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-bottom-pressed.png")));
        bottomAlignButton.addItemListener(this::bottomAlignButtonItemStateChanged);

        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"1, 2, 3", "I, II, III", "i, ii, iii",
            "A, B, C", "a, b, c", "Bates Numbering", "Repeated Text", "Image", "Variable Text", "Empty"}));
        typeComboBox.addItemListener(this::typeComboBoxItemStateChanged);

        jLabel10.setText("Style:");

        buttonGroup1.add(justifyButton);
        justifyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-justity-icon.png")));
        justifyButton.setToolTipText("justify");
        justifyButton.setBorderPainted(false);
        justifyButton.setContentAreaFilled(false);
        justifyButton
            .setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-justity-icon2.png")));
        justifyButton
            .setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-justity-icon3.png")));
        justifyButton.addItemListener(this::justifyButtonItemStateChanged);

        jLabel11.setText("units:");

        unitsComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"points", "inches", "mm"}));
        unitsComboBox.addItemListener(this::unitsComboBoxItemStateChanged);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addGap(12, 12, 12).addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 134,
                    javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel11).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(
                    unitsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 99,
                    javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jSeparator1)
            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup().addComponent(jLabel9)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                        opacitySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 60,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createSequentialGroup().addGroup(layout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup().addGroup(layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(
                                    layout.createSequentialGroup().addComponent(jLabel1).addGap(20, 20, 20)
                                        .addComponent(fontButton, javax.swing.GroupLayout.DEFAULT_SIZE,
                                            javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup().addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(angleSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 68,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(layout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(topAlignButton,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(middleAlignButton,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(bottomAlignButton,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(leftAlignButton,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(centerAlignButton,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(rightAlignButton,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(justifyButton,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(0, 0, Short.MAX_VALUE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup().addGroup(layout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(strikethroughCheckBox)
                                            .addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel3))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(underlineCheckBox).addGap(41, 41, 41)
                                            .addComponent(jLabel2)))
                                    .addGroup(layout
                                        .createParallelGroup(
                                            javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGap(14, 14, 14)
                                            .addComponent(fontColorButton,
                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                            layout.createSequentialGroup().addPreferredGap(
                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(backgroundButton,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addComponent(backgroundCheckBox)))
                        .addGroup(layout.createSequentialGroup().addGroup(layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup().addComponent(jLabel5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(widthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 73,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18).addComponent(jLabel6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(heightSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 73,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup().addComponent(jLabel7)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(horizontalSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(horizontalReference,
                                        javax.swing.GroupLayout.PREFERRED_SIZE, 120,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel8)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(verticalSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(verticalReference, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(0, 0, Short.MAX_VALUE)))
                    .addContainerGap()))));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10).addComponent(jLabel11).addComponent(unitsComboBox,
                        javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 11,
                    javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
                        .createSequentialGroup().addGap(1, 1, 1)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(underlineCheckBox).addComponent(jLabel2))
                            .addComponent(fontColorButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(strikethroughCheckBox).addComponent(jLabel3))
                            .addComponent(backgroundButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(backgroundCheckBox))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1).addComponent(fontButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup().addGroup(layout
                                    .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(leftAlignButton,
                                        javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(centerAlignButton,
                                        javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(rightAlignButton,
                                        javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(justifyButton,
                                        javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(
                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout
                                    .createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(topAlignButton,
                                        javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(middleAlignButton,
                                        javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(bottomAlignButton,
                                        javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout
                                .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(angleSpinner,
                                    javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                    javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11,
                    javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(widthSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6).addComponent(heightSpinner,
                        javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 11,
                    javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(horizontalReference, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(verticalReference, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(horizontalSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(verticalSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 11,
                    javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9).addComponent(opacitySpinner,
                        javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                        javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE)));
    }

    private void backgroundCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (backgroundCheckBox.isSelected()) {
            previewModel.setBackgroundColor(backgroundButton.getSelectedColor());
        } else {
            previewModel.setBackgroundColor(null);
        }

        if (style != null)
            style.setBackground(backgroundCheckBox.isSelected());
    }

    private void angleSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        int angle = Integer.parseInt(angleSpinner.getValue().toString());
        previewModel.setAngle(angle);
        if (style != null)
            style.setAngle(angle);
    }

    private void opacitySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        int opacity = Integer.parseInt(opacitySpinner.getValue().toString());
        previewModel.setOpacity(opacity);
        if (style != null)
            style.setOpacity(opacity);
    }

    private void horizontalSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        float hpos = Float.parseFloat(horizontalSpinner.getValue().toString());
        if (style != null)
            style.setHorizontalPosition(hpos);
    }

    private void verticalSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        float vpos = Float.parseFloat(verticalSpinner.getValue().toString());
        if (style != null)
            style.setVerticalPosition(vpos);
    }

    private void fontButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (jFontChooser1.showDialog(this) != JFontChooser.OK_OPTION) {
            return;
        }

        Font font = jFontChooser1.getSelectedFont();
        int fontSize = jFontChooser1.getSelectedFontSize();
        int fontStyle = jFontChooser1.getSelectedFontStyle();

        if (style != null) {
            style.setFont(font);
            style.setFontSize(fontSize);
            style.setFontStyle(fontStyle);
        }

        fontButton.setText(font.getFontName() + " " + fontSize + "px");
        previewModel.setFont(font);
    }

    private void strikethroughCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        previewModel.setStrikethrough(strikethroughCheckBox.isSelected());
        if (style != null)
            style.setStrikethrough(strikethroughCheckBox.isSelected());
    }

    private void underlineCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        previewModel.setUnderline(underlineCheckBox.isSelected());
        if (style != null)
            style.setUnderline(underlineCheckBox.isSelected());
    }

    private void leftAlignButtonItemStateChanged(java.awt.event.ItemEvent evt) {
        horizontalAlignListener();
    }

    private void centerAlignButtonItemStateChanged(java.awt.event.ItemEvent evt) {
        horizontalAlignListener();
    }

    private void rightAlignButtonItemStateChanged(java.awt.event.ItemEvent evt) {
        horizontalAlignListener();
    }

    private void horizontalReferenceItemStateChanged(java.awt.event.ItemEvent evt) {
        int href = horizontalReference.getSelectedIndex();
        if (style != null)
            style.setHorizontalReference(href);
    }

    private void verticalReferenceItemStateChanged(java.awt.event.ItemEvent evt) {
        int vref = verticalReference.getSelectedIndex();
        if (style != null)
            style.setVerticalReference(vref);
    }

    private void widthSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        float width = Float.parseFloat(widthSpinner.getValue().toString());
        if (style != null)
            style.setWidth(width);
    }

    private void heightSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        float height = Float.parseFloat(heightSpinner.getValue().toString());
        if (style != null)
            style.setHeight(height);
    }

    private void typeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (style == null)
            return;

        if (evt.getStateChange() == ItemEvent.SELECTED) {
            style.setType(typeComboBox.getSelectedIndex());
            styleChangeListener.styleChanged(typeComboBox.getSelectedIndex());
        }
    }

    private void topAlignButtonItemStateChanged(java.awt.event.ItemEvent evt) {
        verticalAlignListener();
    }

    private void middleAlignButtonItemStateChanged(java.awt.event.ItemEvent evt) {
        verticalAlignListener();
    }

    private void bottomAlignButtonItemStateChanged(java.awt.event.ItemEvent evt) {
        verticalAlignListener();
    }

    private void justifyButtonItemStateChanged(java.awt.event.ItemEvent evt) {
        horizontalAlignListener();
    }

    private void unitsComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (style != null) {
            switch (unitsComboBox.getSelectedIndex()) {
                case 0:
                    style.setUnits(WatermarkStyle.Units.POINTS);
                    break;
                case 1:
                    style.setUnits(WatermarkStyle.Units.INCHES);
                    break;
                case 2:
                    style.setUnits(WatermarkStyle.Units.MM);
                    break;
            }
        }
    }

    public void setPreviewModel(PreviewModel model) {
        this.previewModel = model;
    }

    public void setStyleChangeListener(WatermarkPlusTabPanel.StyleChangeListener styleChangeListener) {
        this.styleChangeListener = styleChangeListener;
    }

    private void horizontalAlignListener() {
        HorizontalAlign align;

        if (leftAlignButton.isSelected()) {
            align = HorizontalAlign.LEFT;
        } else if (centerAlignButton.isSelected()) {
            align = HorizontalAlign.CENTER;
        } else if (rightAlignButton.isSelected()) {
            align = HorizontalAlign.RIGHT;
        } else {
            align = HorizontalAlign.JUSTIFY;
        }

        if (style != null)
            style.setHorizontalAlign(align);
    }

    private void verticalAlignListener() {
        WatermarkStyle.VerticalAlign align = WatermarkStyle.VerticalAlign.TOP;

        if (middleAlignButton.isSelected()) {
            align = WatermarkStyle.VerticalAlign.MIDDLE;
        } else if (bottomAlignButton.isSelected()) {
            align = WatermarkStyle.VerticalAlign.BOTTOM;
        }

        if (style != null)
            style.setVerticalAlign(align);
    }

    public void setStyle(WatermarkStyle style) {
        this.style = style;

        fontColorButton.setSelectedColor(style.getFontColor());

        backgroundCheckBox.setSelected(style.isBackground());
        backgroundButton.setSelectedColor(style.getBackgroundColor());

        fontButton.setText(style.getFont().getFontName() + " " + style.getFontSize() + "px");
        previewModel.setFont(style.getFont());

        angleSpinner.setValue(style.getAngle());
        previewModel.setAngle(style.getAngle());

        opacitySpinner.setValue(style.getOpacity());
        previewModel.setOpacity(style.getOpacity());

        strikethroughCheckBox.setSelected(style.isStrikethrough());
        underlineCheckBox.setSelected(style.isUnderline());

        switch (style.getHorizontalAlign()) {
            case LEFT:
                leftAlignButton.setSelected(true);
                break;
            case CENTER:
                centerAlignButton.setSelected(true);
                break;
            case RIGHT:
                rightAlignButton.setSelected(true);
                break;
        }

        switch (style.getVerticalAlign()) {
            case TOP:
                topAlignButton.setSelected(true);
                break;
            case MIDDLE:
                middleAlignButton.setSelected(true);
                break;
            case BOTTOM:
                bottomAlignButton.setSelected(true);
                break;
        }

        widthSpinner.setValue(style.getWidth());
        heightSpinner.setValue(style.getHeight());

        horizontalSpinner.setValue(style.getHorizontalPosition());
        horizontalReference.setSelectedIndex(style.getHorizontalReference());
        verticalSpinner.setValue(style.getVerticalPosition());
        verticalReference.setSelectedIndex(style.getVerticalReference());

        typeComboBox.setSelectedIndex(style.getType().getMask());

        switch (style.getUnits()) {
            case POINTS:
                unitsComboBox.setSelectedIndex(0);
                break;
            case INCHES:
                unitsComboBox.setSelectedIndex(1);
                break;
            case MM:
                unitsComboBox.setSelectedIndex(2);
                break;
        }
    }
}

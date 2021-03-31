package jpdftweak.tabs.watermark.optionpanels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;

import jpdftweak.tabs.watermark.ColorChooserButton.ColorChangedListener;
import jpdftweak.tabs.watermark.WatermarkPreviewBox.PreviewModel;
import jpdftweak.tabs.watermark.WatermarkStyle;
import jpdftweak.tabs.watermark.WatermarkStyle.HorizontalAlign;
import jpdftweak.tabs.watermark.WatermarkTabPanel;
import say.swing.JFontChooser;

/**
 *
 * @author Vasilis Naskos
 */
public class WatermarkOptionsPanel extends javax.swing.JPanel {

	private PreviewModel previewModel;
	private WatermarkStyle style;

	private WatermarkTabPanel.StyleChangeListener styleChangeListener;

	/**
	 * Creates new form watermarkOptionsPanel
	 */
	public WatermarkOptionsPanel() {
		initComponents();

		fontColorButton.addColorChangedListener(new ColorChangedListener() {
			
			public void colorChanged(Color newColor) {
				previewModel.setColor(newColor);
				if (style != null)
					style.setFontColor(newColor);
			}
		});

		backgroundButton.addColorChangedListener(new ColorChangedListener() {
			
			public void colorChanged(Color newColor) {
				if (backgroundCheckBox.isSelected()) {
					previewModel.setBackgroundColor(newColor);
				} else {
					previewModel.setBackgroundColor(null);
				}
				if (style != null)
					style.setBackgroundColor(newColor);
			}
		});
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		buttonGroup1 = new javax.swing.ButtonGroup();
		jFontChooser1 = new say.swing.JFontChooser();
		buttonGroup2 = new javax.swing.ButtonGroup();
		fontButton = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		angleSpinner = new javax.swing.JSpinner();
		underlineCheckBox = new javax.swing.JCheckBox();
		strikethroughCheckBox = new javax.swing.JCheckBox();
		jSeparator1 = new javax.swing.JSeparator();
		jLabel5 = new javax.swing.JLabel();
		widthSpinner = new javax.swing.JSpinner();
		jLabel6 = new javax.swing.JLabel();
		heightSpinner = new javax.swing.JSpinner();
		jSeparator2 = new javax.swing.JSeparator();
		jLabel7 = new javax.swing.JLabel();
		horizontalReference = new javax.swing.JComboBox();
		jLabel8 = new javax.swing.JLabel();
		verticalReference = new javax.swing.JComboBox();
		jSeparator3 = new javax.swing.JSeparator();
		jLabel9 = new javax.swing.JLabel();
		opacitySpinner = new javax.swing.JSpinner();
		backgroundCheckBox = new javax.swing.JCheckBox();
		fontColorButton = new jpdftweak.tabs.watermark.ColorChooserButton();
		backgroundButton = new jpdftweak.tabs.watermark.ColorChooserButton();
		horizontalSpinner = new javax.swing.JSpinner();
		verticalSpinner = new javax.swing.JSpinner();
		leftAlignButton = new javax.swing.JToggleButton();
		centerAlignButton = new javax.swing.JToggleButton();
		rightAlignButton = new javax.swing.JToggleButton();
		topAlignButton = new javax.swing.JToggleButton();
		middleAlignButton = new javax.swing.JToggleButton();
		bottomAlignButton = new javax.swing.JToggleButton();
		typeComboBox = new javax.swing.JComboBox();
		jLabel10 = new javax.swing.JLabel();
		jSeparator4 = new javax.swing.JSeparator();
		justifyButton = new javax.swing.JToggleButton();
		jLabel11 = new javax.swing.JLabel();
		unitsComboBox = new javax.swing.JComboBox();

		setBorder(javax.swing.BorderFactory.createEtchedBorder());

		fontButton.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
		fontButton.setText("Arial 12px");
		fontButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				fontButtonActionPerformed(evt);
			}
		});

		jLabel1.setText("Font:");

		jLabel2.setText("Font color:");

		jLabel3.setText("Background:");

		jLabel4.setText("Angle:");

		angleSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				angleSpinnerStateChanged(evt);
			}
		});

		underlineCheckBox.setText("Underline");
		underlineCheckBox.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				underlineCheckBoxItemStateChanged(evt);
			}
		});

		strikethroughCheckBox.setText("Strikethrough");
		strikethroughCheckBox.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				strikethroughCheckBoxItemStateChanged(evt);
			}
		});

		jLabel5.setText("Width:");

		widthSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), null,
				Float.valueOf(1.0f)));
		widthSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				widthSpinnerStateChanged(evt);
			}
		});

		jLabel6.setText("Height:");

		heightSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), Float.valueOf(0.0f), null,
				Float.valueOf(1.0f)));
		heightSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				heightSpinnerStateChanged(evt);
			}
		});

		jLabel7.setText("Horizontal:");

		horizontalReference
		.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "From Left", "Center", "From Right" }));
		horizontalReference.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				horizontalReferenceItemStateChanged(evt);
			}
		});

		jLabel8.setText("Vertical:");

		verticalReference
		.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "From Top", "Center", "From Bottom" }));
		verticalReference.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				verticalReferenceItemStateChanged(evt);
			}
		});

		jLabel9.setText("Opactity:");

		opacitySpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
		opacitySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				opacitySpinnerStateChanged(evt);
			}
		});

		backgroundCheckBox.setText("Background");
		backgroundCheckBox.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				backgroundCheckBoxItemStateChanged(evt);
			}
		});

		fontColorButton.setSelectedColor(new java.awt.Color(255, 0, 0));

		backgroundButton.setSelectedColor(new java.awt.Color(4, 255, 0));

		horizontalSpinner
		.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));
		horizontalSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				horizontalSpinnerStateChanged(evt);
			}
		});

		verticalSpinner
		.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.0f), null, null, Float.valueOf(1.0f)));
		verticalSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				verticalSpinnerStateChanged(evt);
			}
		});

		buttonGroup1.add(leftAlignButton);
		leftAlignButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-left-icon.png"))); // NOI18N
		leftAlignButton.setSelected(true);
		leftAlignButton.setToolTipText("left align");
		leftAlignButton.setBorderPainted(false);
		leftAlignButton.setContentAreaFilled(false);
		leftAlignButton
		.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-left-icon2.png"))); // NOI18N
		leftAlignButton
		.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-left-icon3.png"))); // NOI18N
		leftAlignButton.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				leftAlignButtonItemStateChanged(evt);
			}
		});

		buttonGroup1.add(centerAlignButton);
		centerAlignButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-center-icon.png"))); // NOI18N
		centerAlignButton.setToolTipText("center align");
		centerAlignButton.setBorderPainted(false);
		centerAlignButton.setContentAreaFilled(false);
		centerAlignButton
		.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-center-icon2.png"))); // NOI18N
		centerAlignButton
		.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-center-icon3.png"))); // NOI18N
		centerAlignButton.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				centerAlignButtonItemStateChanged(evt);
			}
		});

		buttonGroup1.add(rightAlignButton);
		rightAlignButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-right-icon.png"))); // NOI18N
		rightAlignButton.setToolTipText("right align");
		rightAlignButton.setBorderPainted(false);
		rightAlignButton.setContentAreaFilled(false);
		rightAlignButton
		.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-right-icon2.png"))); // NOI18N
		rightAlignButton
		.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-right-icon3.png"))); // NOI18N
		rightAlignButton.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				rightAlignButtonItemStateChanged(evt);
			}
		});

		buttonGroup2.add(topAlignButton);
		topAlignButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-top.png"))); // NOI18N
		topAlignButton.setSelected(true);
		topAlignButton.setToolTipText("top align");
		topAlignButton.setBorderPainted(false);
		topAlignButton.setContentAreaFilled(false);
		topAlignButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-top-rollover.png"))); // NOI18N
		topAlignButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-top-pressed.png"))); // NOI18N
		topAlignButton.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				topAlignButtonItemStateChanged(evt);
			}
		});

		buttonGroup2.add(middleAlignButton);
		middleAlignButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-middle.png"))); // NOI18N
		middleAlignButton.setToolTipText("middle align");
		middleAlignButton.setBorderPainted(false);
		middleAlignButton.setContentAreaFilled(false);
		middleAlignButton
		.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-middle-rollover.png"))); // NOI18N
		middleAlignButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-middle-pressed.png"))); // NOI18N
		middleAlignButton.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				middleAlignButtonItemStateChanged(evt);
			}
		});

		buttonGroup2.add(bottomAlignButton);
		bottomAlignButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-bottom.png"))); // NOI18N
		bottomAlignButton.setToolTipText("bottom align");
		bottomAlignButton.setBorderPainted(false);
		bottomAlignButton.setContentAreaFilled(false);
		bottomAlignButton
		.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-bottom-rollover.png"))); // NOI18N
		bottomAlignButton.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/btn-bottom-pressed.png"))); // NOI18N
		bottomAlignButton.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				bottomAlignButtonItemStateChanged(evt);
			}
		});

		typeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1, 2, 3", "I, II, III", "i, ii, iii",
				"A, B, C", "a, b, c", "Bates Numbering", "Repeated Text", "Image", "Variable Text", "Empty" }));
		typeComboBox.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				typeComboBoxItemStateChanged(evt);
			}
		});

		jLabel10.setText("Style:");

		buttonGroup1.add(justifyButton);
		justifyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-justity-icon.png"))); // NOI18N
		justifyButton.setToolTipText("justify");
		justifyButton.setBorderPainted(false);
		justifyButton.setContentAreaFilled(false);
		justifyButton
		.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-justity-icon2.png"))); // NOI18N
		justifyButton
		.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/text-align-justity-icon3.png"))); // NOI18N
		justifyButton.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				justifyButtonItemStateChanged(evt);
			}
		});

		jLabel11.setText("units:");

		unitsComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "points", "inches", "mm" }));
		unitsComboBox.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent evt) {
				unitsComboBoxItemStateChanged(evt);
			}
		});

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
	}// </editor-fold>//GEN-END:initComponents

	private void backgroundCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_backgroundCheckBoxItemStateChanged
		if (backgroundCheckBox.isSelected()) {
			previewModel.setBackgroundColor(backgroundButton.getSelectedColor());
		} else {
			previewModel.setBackgroundColor(null);
		}

		if (style != null)
			style.setBackground(backgroundCheckBox.isSelected());
	}// GEN-LAST:event_backgroundCheckBoxItemStateChanged

	private void angleSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_angleSpinnerStateChanged
		int angle = Integer.parseInt(angleSpinner.getValue().toString());
		previewModel.setAngle(angle);
		if (style != null)
			style.setAngle(angle);
	}// GEN-LAST:event_angleSpinnerStateChanged

	private void opacitySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_opacitySpinnerStateChanged
		int opacity = Integer.parseInt(opacitySpinner.getValue().toString());
		previewModel.setOpacity(opacity);
		if (style != null)
			style.setOpacity(opacity);
	}// GEN-LAST:event_opacitySpinnerStateChanged

	private void horizontalSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_horizontalSpinnerStateChanged
		float hpos = Float.parseFloat(horizontalSpinner.getValue().toString());
		if (style != null)
			style.setHorizontalPosition(hpos);
	}// GEN-LAST:event_horizontalSpinnerStateChanged

	private void verticalSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_verticalSpinnerStateChanged
		float vpos = Float.parseFloat(verticalSpinner.getValue().toString());
		if (style != null)
			style.setVerticalPosition(vpos);
	}// GEN-LAST:event_verticalSpinnerStateChanged

	private void fontButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_fontButtonActionPerformed
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
	}// GEN-LAST:event_fontButtonActionPerformed

	private void strikethroughCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_strikethroughCheckBoxItemStateChanged
		previewModel.setStrikethrough(strikethroughCheckBox.isSelected());
		if (style != null)
			style.setStrikethrough(strikethroughCheckBox.isSelected());
	}// GEN-LAST:event_strikethroughCheckBoxItemStateChanged

	private void underlineCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_underlineCheckBoxItemStateChanged
		previewModel.setUnderline(underlineCheckBox.isSelected());
		if (style != null)
			style.setUnderline(underlineCheckBox.isSelected());
	}// GEN-LAST:event_underlineCheckBoxItemStateChanged

	private void leftAlignButtonItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_leftAlignButtonItemStateChanged
		horizontalAlignListener();
	}// GEN-LAST:event_leftAlignButtonItemStateChanged

	private void centerAlignButtonItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_centerAlignButtonItemStateChanged
		horizontalAlignListener();
	}// GEN-LAST:event_centerAlignButtonItemStateChanged

	private void rightAlignButtonItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_rightAlignButtonItemStateChanged
		horizontalAlignListener();
	}// GEN-LAST:event_rightAlignButtonItemStateChanged

	private void horizontalReferenceItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_horizontalReferenceItemStateChanged
		int href = horizontalReference.getSelectedIndex();
		if (style != null)
			style.setHorizontalReference(href);
	}// GEN-LAST:event_horizontalReferenceItemStateChanged

	private void verticalReferenceItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_verticalReferenceItemStateChanged
		int vref = verticalReference.getSelectedIndex();
		if (style != null)
			style.setVerticalReference(vref);
	}// GEN-LAST:event_verticalReferenceItemStateChanged

	private void widthSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_widthSpinnerStateChanged
		float width = Float.parseFloat(widthSpinner.getValue().toString());
		if (style != null)
			style.setWidth(width);
	}// GEN-LAST:event_widthSpinnerStateChanged

	private void heightSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_heightSpinnerStateChanged
		float height = Float.parseFloat(heightSpinner.getValue().toString());
		if (style != null)
			style.setHeight(height);
	}// GEN-LAST:event_heightSpinnerStateChanged

	private void typeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_typeComboBoxItemStateChanged
		if (style == null)
			return;

		if (evt.getStateChange() == ItemEvent.SELECTED) {
			style.setType(typeComboBox.getSelectedIndex());
			styleChangeListener.styleChanged(typeComboBox.getSelectedIndex());
		}
	}// GEN-LAST:event_typeComboBoxItemStateChanged

	private void topAlignButtonItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_topAlignButtonItemStateChanged
		verticalAlignListener();
	}// GEN-LAST:event_topAlignButtonItemStateChanged

	private void middleAlignButtonItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_middleAlignButtonItemStateChanged
		verticalAlignListener();
	}// GEN-LAST:event_middleAlignButtonItemStateChanged

	private void bottomAlignButtonItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_bottomAlignButtonItemStateChanged
		verticalAlignListener();
	}// GEN-LAST:event_bottomAlignButtonItemStateChanged

	private void justifyButtonItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_justifyButtonItemStateChanged
		horizontalAlignListener();
	}// GEN-LAST:event_justifyButtonItemStateChanged

	private void unitsComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_unitsComboBoxItemStateChanged
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
	}// GEN-LAST:event_unitsComboBoxItemStateChanged

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JSpinner angleSpinner;
	private jpdftweak.tabs.watermark.ColorChooserButton backgroundButton;
	private javax.swing.JCheckBox backgroundCheckBox;
	private javax.swing.JToggleButton bottomAlignButton;
	private javax.swing.ButtonGroup buttonGroup1;
	private javax.swing.ButtonGroup buttonGroup2;
	private javax.swing.JToggleButton centerAlignButton;
	private javax.swing.JButton fontButton;
	private jpdftweak.tabs.watermark.ColorChooserButton fontColorButton;
	private javax.swing.JSpinner heightSpinner;
	private javax.swing.JComboBox horizontalReference;
	private javax.swing.JSpinner horizontalSpinner;
	private say.swing.JFontChooser jFontChooser1;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	private javax.swing.JSeparator jSeparator3;
	private javax.swing.JSeparator jSeparator4;
	private javax.swing.JToggleButton justifyButton;
	private javax.swing.JToggleButton leftAlignButton;
	private javax.swing.JToggleButton middleAlignButton;
	private javax.swing.JSpinner opacitySpinner;
	private javax.swing.JToggleButton rightAlignButton;
	private javax.swing.JCheckBox strikethroughCheckBox;
	private javax.swing.JToggleButton topAlignButton;
	private javax.swing.JComboBox typeComboBox;
	private javax.swing.JCheckBox underlineCheckBox;
	private javax.swing.JComboBox unitsComboBox;
	private javax.swing.JComboBox verticalReference;
	private javax.swing.JSpinner verticalSpinner;
	private javax.swing.JSpinner widthSpinner;
	// End of variables declaration//GEN-END:variables

	public void setPreviewModel(PreviewModel model) {
		this.previewModel = model;
	}

	public void setStyleChangeListener(WatermarkTabPanel.StyleChangeListener styleChangeListener) {
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

		if (topAlignButton.isSelected()) {
			align = WatermarkStyle.VerticalAlign.TOP;
		} else if (middleAlignButton.isSelected()) {
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

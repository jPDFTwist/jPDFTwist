package jpdftwist.gui.tab.watermark;

import jpdftwist.core.watermark.WatermarkStyle;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class WatermarkBatesPanel extends javax.swing.JPanel {

    private javax.swing.JComboBox<String> applyToComboBox;
    private javax.swing.JTextField pagesField;
    private javax.swing.JTextField prefixField;
    private javax.swing.JSpinner repeatForSpinner;
    private javax.swing.JSpinner startWithSpinner;
    private javax.swing.JSpinner stepSpinner;
    private javax.swing.JTextField suffixField;
    private javax.swing.JSpinner zeroPaddingSpinner;
    private WatermarkStyle style;

    public WatermarkBatesPanel() {
        initComponents();

        prefixField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (style != null)
                    style.setBatesPrefix(prefixField.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                if (style != null)
                    style.setBatesPrefix(prefixField.getText());
            }

            public void changedUpdate(DocumentEvent e) {
                if (style != null)
                    style.setBatesPrefix(prefixField.getText());
            }
        });

        suffixField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (style != null)
                    style.setBatesSuffix(suffixField.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                if (style != null)
                    style.setBatesSuffix(suffixField.getText());
            }

            public void changedUpdate(DocumentEvent e) {
                if (style != null)
                    style.setBatesSuffix(suffixField.getText());
            }
        });

        pagesField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                changed();
            }

            public void removeUpdate(DocumentEvent e) {
                changed();
            }

            public void changedUpdate(DocumentEvent e) {
                changed();
            }

            private void changed() {
                if (style == null) {
                    return;
                }

                style.setBatesPages(pagesField.getText());
            }
        });
    }

    private void initComponents() {
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        startWithSpinner = new javax.swing.JSpinner();
        stepSpinner = new javax.swing.JSpinner();
        repeatForSpinner = new javax.swing.JSpinner();
        zeroPaddingSpinner = new javax.swing.JSpinner();
        prefixField = new javax.swing.JTextField();
        suffixField = new javax.swing.JTextField();
        applyToComboBox = new javax.swing.JComboBox<>();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        pagesField = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createTitledBorder(" Bates Numbering "));

        jLabel1.setText("Start with:");

        jLabel2.setText("Step:");

        jLabel3.setText("Repeat For:");

        jLabel4.setText("Zero padding:");

        jLabel5.setText("Prefix:");

        jLabel6.setText("Suffix:");

        jLabel7.setText("Apply to:");

        startWithSpinner
            .setModel(new javax.swing.SpinnerNumberModel(1, null, null, 1));
        startWithSpinner.addChangeListener(this::startWithSpinnerStateChanged);

        stepSpinner.setModel(new javax.swing.SpinnerNumberModel(1, null, null, 1));
        stepSpinner.addChangeListener(this::stepSpinnerStateChanged);

        repeatForSpinner.setModel(
            new javax.swing.SpinnerNumberModel(1, 0, null, 1));
        repeatForSpinner.addChangeListener(this::repeatForSpinnerStateChanged);

        zeroPaddingSpinner.setModel(
            new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        zeroPaddingSpinner.addChangeListener(this::zeroPaddingSpinnerStateChanged);

        applyToComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[]{"All", "Odd", "Even", "Image Files", "PDF Files", "Only:", "Exlude:"}));
        applyToComboBox.addItemListener(this::applyToComboBoxItemStateChanged);

        jLabel8.setText("Pages:");

        pagesField.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup().addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(startWithSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 63,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup().addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stepSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 63,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup().addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(repeatForSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 63,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup().addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(zeroPaddingSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 63,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup().addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(prefixField))
                    .addGroup(layout.createSequentialGroup().addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(suffixField))
                    .addGroup(layout.createSequentialGroup().addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(applyToComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 127,
                            javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup().addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pagesField)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7);

        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
            .createSequentialGroup().addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel1)
                .addComponent(startWithSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel2)
                .addComponent(stepSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel3)
                .addComponent(repeatForSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel4)
                .addComponent(zeroPaddingSpinner, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel5)
                .addComponent(prefixField, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel6)
                .addComponent(suffixField, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel7)
                .addComponent(applyToComboBox, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel8)
                .addComponent(pagesField, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }

    private void startWithSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        int value = Integer.parseInt(startWithSpinner.getValue().toString());
        style.setBatesStartWith(value);
    }

    private void stepSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        int value = Integer.parseInt(stepSpinner.getValue().toString());
        style.setBatesStep(value);
    }

    private void repeatForSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        int value = Integer.parseInt(repeatForSpinner.getValue().toString());
        style.setBatesRepeatFor(value);
    }

    private void zeroPaddingSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {
        int value = Integer.parseInt(zeroPaddingSpinner.getValue().toString());
        style.setBatesZeroPadding(value);
    }

    private void applyToComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        int value = applyToComboBox.getSelectedIndex();
        style.setBatesApplyTo(value);
        pagesField.setEnabled(value == 5 || value == 6);
    }

    public void setStyle(WatermarkStyle style) {
        this.style = style;

        startWithSpinner.setValue(style.getBatesStartWith());
        stepSpinner.setValue(style.getBatesStep());
        repeatForSpinner.setValue(style.getBatesRepeatFor());
        zeroPaddingSpinner.setValue(style.getBatesZeroPadding());
        prefixField.setText(style.getBatesPrefix());
        suffixField.setText(style.getBatesSuffix());
        applyToComboBox.setSelectedIndex(style.getBatesApplyTo());
        pagesField.setText(style.getBatesPages());
    }

}

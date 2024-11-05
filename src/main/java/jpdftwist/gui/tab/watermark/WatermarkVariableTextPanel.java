package jpdftwist.gui.tab.watermark;

import jpdftwist.core.watermark.WatermarkStyle;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class WatermarkVariableTextPanel extends javax.swing.JPanel {

    private javax.swing.JTextField jTextField1;
    private WatermarkStyle style;
    private final JFileChooser fileChooser;
    private javax.swing.JSpinner pdfPageSpinner;

    public WatermarkVariableTextPanel() {
        initComponents();

        jTextField1.getDocument().addDocumentListener(new DocumentListener() {
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
                style.setVariableTextFile(jTextField1.getText());
            }
        });

        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
    }

    private void initComponents() {
        JLabel jLabel1 = new JLabel();
        JButton jButton1 = new JButton();
        jTextField1 = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createTitledBorder(" Variable Text from file "));
        jLabel1.setText("File: *.txt");

        jButton1.setText("Browse");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        pdfPageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        pdfPageSpinner.addChangeListener(e -> {
            try {
                int page = Integer.parseInt(pdfPageSpinner.getValue().toString());
                style.setPdfPage(page);
            } catch (Exception ex) {
                Logger.getLogger(WatermarkImagePanel.class.getName()).log(Level.SEVERE, "Ex157", ex);
            }
        });
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jTextField1)
						.addGroup(layout.createSequentialGroup().addComponent(jLabel1)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 147, Short.MAX_VALUE)
								.addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 99,
										javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel1).addComponent(jButton1))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(67, Short.MAX_VALUE)));
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        int response = fileChooser.showOpenDialog(this);
        if (response != JFileChooser.APPROVE_OPTION) {
            return;
        }

        jTextField1.setText(fileChooser.getSelectedFile().getAbsolutePath());
    }

    public void setStyle(WatermarkStyle style) {
        this.style = style;

        jTextField1.setText(style.getVariableTextFile());
    }
}

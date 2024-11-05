package jpdftwist.gui.tab.watermark;

import jpdftwist.core.watermark.WatermarkStyle;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

public class WatermarkRepeatedTextPanel extends javax.swing.JPanel {

    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JTextArea jTextArea1;
    private WatermarkStyle style;

    public WatermarkRepeatedTextPanel() {
        initComponents();

        jTextArea1.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                if (style != null)
                    style.setRepeatedText(jTextArea1.getText());
            }

            public void removeUpdate(DocumentEvent e) {
                if (style != null)
                    style.setRepeatedText(jTextArea1.getText());
            }

            public void changedUpdate(DocumentEvent e) {
                if (style != null)
                    style.setRepeatedText(jTextArea1.getText());
            }
        });
    }

    private void initComponents() {

        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();

        setBorder(javax.swing.BorderFactory.createTitledBorder(" Repeated Text "));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(10);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel1.setText("Text:");

        jLabel2.setText("Data:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(
            new String[]{"--Data--", "System Time", "Hour", "Minutes", "Seconds", "System Date", "Day", "Month", "Year", 
            		
            	"Page Count", 
                "Page Width (pts)", 	"Page Height (pts)", 
                "Page Width (inch)", 	"Page Height (inch)",
                "Page Width (mm)", 		"Page Height (mm)",
                
                "Page Number", "Logical Page Number", "File Name", "File Name with extension", "File Path",
                "File Size", "File Last Modified Date", 
                
                "Image Depth", 
                "Image Width (pts)", 	"Image Height (pts)",
                "Image Width (inch)", 	"Image Height (inch)", 
                "Image Width (mm)", 	"Image Height (mm)",
                "Image Width (dpi)", 	"Image Height (dpi)"}));
        
        jComboBox1.addItemListener(this::jComboBox1ItemStateChanged);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
            .createSequentialGroup().addContainerGap()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup().addComponent(jLabel1).addGap(0, 0, Short.MAX_VALUE))
                .addGroup(layout.createSequentialGroup().addComponent(jLabel2)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2).addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() != ItemEvent.SELECTED) {
            return;
        }

        int index = jComboBox1.getSelectedIndex();
        String data;

        List<String> dataCodes = new ArrayList<>();
        dataCodes.add("");
        dataCodes.add("%h:%m:%s");
        dataCodes.add("%h");
        dataCodes.add("%m");
        dataCodes.add("%s");
        dataCodes.add("%D/%M/%Y");
        dataCodes.add("%D");
        dataCodes.add("%M");
        dataCodes.add("%Y");
        dataCodes.add("%c");
        dataCodes.add("{page_width_points}");
        dataCodes.add("{page_height_points}");
        dataCodes.add("{page_width_inch}");
        dataCodes.add("{page_height_inch}");
        dataCodes.add("{page_width_mm}");
        dataCodes.add("{page_height_mm}");
        dataCodes.add("%n");
        dataCodes.add("%N");
        dataCodes.add("%f");
        dataCodes.add("%F");
        dataCodes.add("%p");
        dataCodes.add("{file_size}");
        dataCodes.add("{last_modified}");
        dataCodes.add("{img_depth}");
        dataCodes.add("{img_width_points}");
        dataCodes.add("{img_height_points}");
        dataCodes.add("{img_width_inch}");
        dataCodes.add("{img_height_inch}");
        dataCodes.add("{img_width_mm}");
        dataCodes.add("{img_height_mm}");
        dataCodes.add("{img_width_dpi}");
        dataCodes.add("{img_height_dpi}");

        data = dataCodes.get(index);
        int caretPos = jTextArea1.getCaretPosition();
        String text = jTextArea1.getText();

        String finalText;
        finalText = text.substring(0, caretPos);
        finalText += data;
        finalText += text.substring(caretPos);

        jTextArea1.setText(finalText);

        jComboBox1.setSelectedIndex(0);
    }

    public void setStyle(WatermarkStyle style) {
        this.style = style;

        jTextArea1.setText(style.getRepeatedText());
    }
}

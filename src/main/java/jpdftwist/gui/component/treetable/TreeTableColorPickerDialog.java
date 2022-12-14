package jpdftwist.gui.component.treetable;

import jpdftwist.gui.component.ColorChooserButton;
import jpdftwist.utils.PreferencesUtil;

import java.awt.*;

/**
 * @author Vasilis Naskos
 */
public class TreeTableColorPickerDialog extends javax.swing.JFrame {

    private static final int LIGHT_GRAY = 0xFFF2F1F0;
    private static final int YELLOW = 0xFFFFFF00;
    private static final int CYAN = 0xFF00FFFF;
    private static final int LIGHT_GREEN = 0xFF00FF00;

    private ColorChooserButton blankColorButton;
    private ColorChooserButton existingColorButton;
    private ColorChooserButton fileColorButton;
    private ColorChooserButton folderColorButton;
    private ColorChooserButton pageColorButton;

    private Color folderColor;
    private Color fileColor;
    private Color blankColor;
    private Color existingColor;
    private Color pageColor;

    public TreeTableColorPickerDialog() {
        initComponents();

        setLocationRelativeTo(null);

        folderColor = new Color(PreferencesUtil.PREFS.getInt("folderColor", LIGHT_GRAY));
        fileColor = new Color(PreferencesUtil.PREFS.getInt("fileColor", YELLOW));
        blankColor = new Color(PreferencesUtil.PREFS.getInt("blankColor", CYAN));
        existingColor = new Color(PreferencesUtil.PREFS.getInt("existingColor", LIGHT_GREEN));
        pageColor = new Color(PreferencesUtil.PREFS.getInt("pageColor", LIGHT_GRAY));

        setSelectedColors();

        folderColorButton.addColorChangedListener(newColor -> folderColor = newColor);
        fileColorButton.addColorChangedListener(newColor -> fileColor = newColor);
        blankColorButton.addColorChangedListener(newColor -> blankColor = newColor);
        existingColorButton.addColorChangedListener(newColor -> existingColor = newColor);
        pageColorButton.addColorChangedListener(newColor -> pageColor = newColor);
    }

    private void initComponents() {
        folderColorButton = new ColorChooserButton();
        fileColorButton = new ColorChooserButton();
        blankColorButton = new ColorChooserButton();
        existingColorButton = new ColorChooserButton();
        pageColorButton = new ColorChooserButton();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton defaultsButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Colors");

        folderColorButton.setText("Folder");
        folderColorButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        fileColorButton.setText("File");
        fileColorButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        blankColorButton.setText("Generated Blank");
        blankColorButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        existingColorButton.setText("Generated From Existing");
        existingColorButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        pageColorButton.setText("Page");
        pageColorButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        okButton.setText("Ok");
        okButton.addActionListener(this::savePreferences);

        defaultsButton.setText("Default");
        defaultsButton.addActionListener(this::resetToDefaults);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(folderColorButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileColorButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(blankColorButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(existingColorButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pageColorButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 82,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(defaultsButton).addContainerGap()));

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, blankColorButton, existingColorButton, fileColorButton, folderColorButton, pageColorButton);

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, okButton, defaultsButton);

        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addContainerGap()
                .addComponent(folderColorButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fileColorButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(blankColorButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(existingColorButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pageColorButton, javax.swing.GroupLayout.PREFERRED_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton).addComponent(defaultsButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        pack();
    }

    private void savePreferences(java.awt.event.ActionEvent evt) {
        PreferencesUtil.PREFS.putInt("folderColor", folderColor.getRGB());
        PreferencesUtil.PREFS.putInt("fileColor", fileColor.getRGB());
        PreferencesUtil.PREFS.putInt("blankColor", blankColor.getRGB());
        PreferencesUtil.PREFS.putInt("existingColor", existingColor.getRGB());
        PreferencesUtil.PREFS.putInt("pageColor", pageColor.getRGB());

        this.dispose();
    }

    private void resetToDefaults(java.awt.event.ActionEvent evt) {
        folderColor = new Color(LIGHT_GRAY);
        fileColor = new Color(YELLOW);
        blankColor = new Color(CYAN);
        existingColor = new Color(LIGHT_GREEN);
        pageColor = new Color(LIGHT_GRAY);

        setSelectedColors();
    }

    private void setSelectedColors() {
        folderColorButton.setSelectedColor(folderColor);
        fileColorButton.setSelectedColor(fileColor);
        blankColorButton.setSelectedColor(blankColor);
        existingColorButton.setSelectedColor(existingColor);
        pageColorButton.setSelectedColor(pageColor);
    }
}

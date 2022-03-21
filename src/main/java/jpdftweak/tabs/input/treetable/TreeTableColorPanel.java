package jpdftweak.tabs.input.treetable;

import java.awt.Color;

import jpdftweak.tabs.watermark.ColorChooserButton.ColorChangedListener;
import jpdftweak.utils.PreferencesUtil;

/**
 *
 * @author Vasilis Naskos
 */
public class TreeTableColorPanel extends javax.swing.JFrame {

	private Color folder, file, blank, existing, page;

	/**
	 * Creates new form TreeTableColorPanel
	 */
	public TreeTableColorPanel() {
		initComponents();

		setLocationRelativeTo(null);

		folder = new Color(PreferencesUtil.PREFS.getInt("folderColor", -855568));
		file = new Color(PreferencesUtil.PREFS.getInt("fileColor", -256));
		blank = new Color(PreferencesUtil.PREFS.getInt("blankColor", -16711681));
		existing = new Color(PreferencesUtil.PREFS.getInt("existingColor", -16711936));
		page = new Color(PreferencesUtil.PREFS.getInt("pageColor", -855568));

		changeButtonSelectedColors();

		folderColorButton.addColorChangedListener(new ColorChangedListener() {
			
			public void colorChanged(Color newColor) {
				folder = newColor;
			}
		});

		fileColorButton.addColorChangedListener(new ColorChangedListener() {
			
			public void colorChanged(Color newColor) {
				file = newColor;
			}
		});

		blankColorButton.addColorChangedListener(new ColorChangedListener() {
			
			public void colorChanged(Color newColor) {
				blank = newColor;
			}
		});

		existingColorButton.addColorChangedListener(new ColorChangedListener() {
			
			public void colorChanged(Color newColor) {
				existing = newColor;
			}
		});

		pageColorButton.addColorChangedListener(new ColorChangedListener() {
			
			public void colorChanged(Color newColor) {
				page = newColor;
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

		folderColorButton = new jpdftweak.tabs.watermark.ColorChooserButton();
		fileColorButton = new jpdftweak.tabs.watermark.ColorChooserButton();
		blankColorButton = new jpdftweak.tabs.watermark.ColorChooserButton();
		existingColorButton = new jpdftweak.tabs.watermark.ColorChooserButton();
		pageColorButton = new jpdftweak.tabs.watermark.ColorChooserButton();
		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();

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

		jButton1.setText("Ok");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		jButton2.setText("Default");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});

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
						.addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 82,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jButton2).addContainerGap()));

		layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] { blankColorButton,
				existingColorButton, fileColorButton, folderColorButton, pageColorButton });

		layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] { jButton1, jButton2 });

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
								.addComponent(jButton1).addComponent(jButton2))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
		PreferencesUtil.PREFS.putInt("folderColor", folder.getRGB());
		PreferencesUtil.PREFS.putInt("fileColor", file.getRGB());
		PreferencesUtil.PREFS.putInt("blankColor", blank.getRGB());
		PreferencesUtil.PREFS.putInt("existingColor", existing.getRGB());
		PreferencesUtil.PREFS.putInt("pageColor", page.getRGB());

		this.dispose();
	}// GEN-LAST:event_jButton1ActionPerformed

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
		folder = new Color(-855568);
		file = new Color(-256);
		blank = new Color(-16711681);
		existing = new Color(-16711936);
		page = new Color(-855568);

		changeButtonSelectedColors();
	}// GEN-LAST:event_jButton2ActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private jpdftweak.tabs.watermark.ColorChooserButton blankColorButton;
	private jpdftweak.tabs.watermark.ColorChooserButton existingColorButton;
	private jpdftweak.tabs.watermark.ColorChooserButton fileColorButton;
	private jpdftweak.tabs.watermark.ColorChooserButton folderColorButton;
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private jpdftweak.tabs.watermark.ColorChooserButton pageColorButton;
	// End of variables declaration//GEN-END:variables

	private void changeButtonSelectedColors() {
		folderColorButton.setSelectedColor(folder);
		fileColorButton.setSelectedColor(file);
		blankColorButton.setSelectedColor(blank);
		existingColorButton.setSelectedColor(existing);
		pageColorButton.setSelectedColor(page);
	}
}
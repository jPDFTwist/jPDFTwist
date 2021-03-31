package jpdftweak.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;

import com.itextpdf.text.DocumentException;

import jpdftweak.core.PdfTweak;
import jpdftweak.gui.MainForm;
import jpdftweak.gui.dialogs.OutputProgressDialog;

public class AttachmentTab extends Tab {

	private JButton add, remove;
	private JList list;
	private MainForm mainForm;
	private DefaultListModel lm;

	public AttachmentTab(MainForm mf) {
		super(new BorderLayout());
		mainForm = mf;
		add(add = new JButton("Add attachment..."), BorderLayout.NORTH);
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser f = new JFileChooser();
				if (f.showOpenDialog(mainForm) == JFileChooser.APPROVE_OPTION) {
					lm.addElement(f.getSelectedFile());
				}
			}
		});
		add(list = new JList(lm = new DefaultListModel()), BorderLayout.CENTER);
		list.setBackground(new Color(230, 230, 250));
		add(remove = new JButton("Remove"), BorderLayout.SOUTH);
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[] idxs = list.getSelectedIndices();
				for (int i = idxs.length - 1; i >= 0; i--) {
					lm.remove(idxs[i]);
				}
			}
		});
	}

	
	public String getTabName() {
		return "Attachments";
	}

	
	public PdfTweak run(PdfTweak tweak, OutputProgressDialog outDialog) throws IOException, DocumentException {
		outDialog.updateTweaksProgress(getTabName());
		outDialog.setAction("Adding attachments");
		outDialog.resetProcessedPages();
		for (int i = 0; i < lm.getSize(); i++) {
			File f = (File) lm.get(i);
			tweak.addFile(f);
		}
		return tweak;
	}

}

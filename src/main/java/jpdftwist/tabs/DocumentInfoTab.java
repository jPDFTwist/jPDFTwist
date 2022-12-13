package jpdftwist.tabs;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jpdftwist.core.PDFTwist;
import jpdftwist.gui.MainWindow;
import jpdftwist.gui.component.table.TableComponent;
import jpdftwist.gui.dialog.OutputProgressDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class DocumentInfoTab extends Tab {

	private final MainWindow mainWindow;

	private JCheckBox infoChange;
	private TableComponent infoEntries;
	private JButton infoLoad, infoAdd;

	public DocumentInfoTab(MainWindow mf) {
		super(new FormLayout("f:p:g, f:p", "f:p, f:p, f:p:g"));
		this.mainWindow = mf;
		CellConstraints CC = new CellConstraints();
		this.add(infoChange = new JCheckBox("Change Document Info"), CC.xy(1, 1));
		infoChange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean b = infoChange.isSelected();
				infoLoad.setEnabled(b);
				infoAdd.setEnabled(b);
				infoEntries.setEnabled(b);
			}
		});

		this.add(infoLoad = new JButton("Load from document"), CC.xy(2, 1));
		infoLoad.setEnabled(false); // TODO
		infoLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO
				//                infoEntries.clear();
				//                if (mainForm.getInputFile() == null) {
				//                    return;
				//                }
				//                Map<String, String> infoDictionary = mainForm.getInputFile().getInfoDictionary();
				//                for (Map.Entry<String, String> entry : infoDictionary.entrySet()) {
				//                    infoEntries.addRow(entry.getKey(), entry.getValue());
				//                }
			}
		});
		this.add(infoAdd = new JButton("Add predefined..."), CC.xyw(1, 2, 2));
		infoAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPopupMenu pm = new JPopupMenu();
				JMenuItem jmi;
				for (String name : PDFTwist.getKnownInfoNames()) {
					pm.add(jmi = new JMenuItem(name));
					jmi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String text = ((JMenuItem) e.getSource()).getText();
							infoEntries.addRow(text, "");
						}
					});
				}
				pm.show(infoAdd, 0, infoAdd.getHeight());
			}
		});
		this.add(infoEntries = new TableComponent(new String[] { "Name", "Value" },
				new Class[] { String.class, String.class }, new Object[] { "", "" }), CC.xyw(1, 3, 2));
		infoLoad.setEnabled(false);
		infoAdd.setEnabled(false);
		infoEntries.setEnabled(false);
	}

	
	public String getTabName() {
		return "Document Info";
	}

	
	public PDFTwist run(PDFTwist pdfTwist, OutputProgressDialog outDialog) {
		outDialog.updateJPDFTwistProgress(getTabName());
		outDialog.setAction("Updating info");
		outDialog.resetProcessedPages();
		if (infoChange.isSelected()) {
			Map<String, String> newInfo = new HashMap<String, String>();
			for (int i = 0; i < infoEntries.getRowCount(); i++) {
				Object[] row = infoEntries.getRow(i);
				String key = (String) row[0], value = (String) row[1];
				newInfo.put(key, value);
			}
			pdfTwist.updateInfoDictionary(newInfo);
		}
		return pdfTwist;
	}

}

package jpdftwist.tabs;

import com.itextpdf.text.DocumentException;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import jpdftwist.core.PDFTwist;
import jpdftwist.gui.MainWindow;
import jpdftwist.gui.dialog.OutputProgressDialog;
import jpdftwist.gui.tab.Tab;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Forms extends Tab {

	private MainWindow mainWindow;

	private JCheckBox changeforms;
	private JButton formLoad;
	private JPanel panel;
	private JCheckBox flatten_fields;
	private JButton import_2;
	private JButton import_1;
	private JButton export_1;
	private JButton export_2;

	public Forms(MainWindow mf) {
		super(new BorderLayout());
		mainWindow = mf;
		initGUI();
	}

	private void initGUI() {
		setLayout(new FormLayout(
				new ColumnSpec[] { ColumnSpec.decode("150px:grow"), ColumnSpec.decode("150px:grow"),
						ColumnSpec.decode("150px:grow"), ColumnSpec.decode("150px:grow"), },
				new RowSpec[] { RowSpec.decode("23px"), FormSpecs.PREF_ROWSPEC, RowSpec.decode("149px"), }));
		add(getExport_1(), "1, 2");
		add(getImport_1(), "2, 2");
		add(getExport_2(), "3, 2");
		add(getImport_2(), "4, 2, fill, top");
		add(getChangeforms(), "1, 1, left, top");
		add(getFormLoad(), "4, 1, right, top");
		add(getPanel(), "1, 3, 4, 1, fill, fill");
	}

	
	public String getTabName() {
		return "Forms / Fields";
	}

	
	public PDFTwist run(PDFTwist pdfTwist, OutputProgressDialog outDialog) throws IOException, DocumentException {
		return pdfTwist;
	}

	private JCheckBox getChangeforms() {
		if (changeforms == null) {
			changeforms = new JCheckBox("Change forms");
			changeforms.addChangeListener(new ChangeListener() {
				public void stateChanged(final ChangeEvent arg0) {
					updateEnabledState();
				}
			});
		}
		return changeforms;
	}

	protected void updateEnabledState() {
		formLoad.setEnabled(changeforms.isSelected());
		export_1.setEnabled(changeforms.isSelected());
		import_1.setEnabled(changeforms.isSelected());
		export_2.setEnabled(changeforms.isSelected());
		import_2.setEnabled(changeforms.isSelected());

	}

	private JButton getFormLoad() {
		if (formLoad == null) {
			formLoad = new JButton("Load from document");
			formLoad.setEnabled(false);
		}
		return formLoad;
	}

	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Fields Options",
					TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			GroupLayout gl_panel = new GroupLayout(panel);
			gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel.createSequentialGroup().addContainerGap()
							.addComponent(getFlatten_fields(), GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
							.addGap(617)));
			gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel
					.createSequentialGroup().addComponent(getFlatten_fields()).addContainerGap(104, Short.MAX_VALUE)));
			panel.setLayout(gl_panel);
		}
		return panel;
	}

	private JCheckBox getFlatten_fields() {
		if (flatten_fields == null) {
			flatten_fields = new JCheckBox("Flatten fields");
			flatten_fields.setEnabled(true);
		}
		return flatten_fields;
	}

	private JButton getImport_2() {
		if (import_2 == null) {
			import_2 = new JButton("Import Properties of fields from TXT");
			import_2.setEnabled(false);
		}
		return import_2;
	}

	private JButton getImport_1() {
		if (import_1 == null) {
			import_1 = new JButton("Import Data of fields from TXT");
			import_1.setEnabled(false);
		}
		return import_1;
	}

	private JButton getExport_1() {
		if (export_1 == null) {
			export_1 = new JButton("Export Data of fields to TXT");
			export_1.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent arg0) {
				}
			});
			export_1.setEnabled(false);
		}
		return export_1;
	}

	private JButton getExport_2() {
		if (export_2 == null) {
			export_2 = new JButton("Export Properties of fields to TXT");
			export_2.setEnabled(false);
		}
		return export_2;
	}
}
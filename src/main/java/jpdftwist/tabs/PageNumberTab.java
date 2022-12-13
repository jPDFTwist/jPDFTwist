package jpdftwist.tabs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPageLabels.PdfPageLabelFormat;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import jpdftwist.core.PDFTwist;
import jpdftwist.gui.MainForm;
import jpdftwist.gui.TableComponent;
import jpdftwist.gui.dialogs.OutputProgressDialog;

public class PageNumberTab extends Tab {

	private JButton load;
	private TableComponent pageNumberRanges;
	private JCheckBox changePageNumbers;

	private static final String[] NUMBER_STYLES = new String[] { "1, 2, 3", "I, II, III", "i, ii, iii", "A, B, C",
			"a, b, c", "Empty" };

	public PageNumberTab(MainForm mf) {
		super(new FormLayout("f:p:g, f:p", "f:p, f:p, f:p:g"));
		CellConstraints CC = new CellConstraints();
		add(changePageNumbers = new JCheckBox("Change page numbers"), CC.xy(1, 1));
		changePageNumbers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateEnabledState();
			}
		});
		add(load = new JButton("Load from document"), CC.xy(2, 1));
		add(pageNumberRanges = buildPageNumberRanges(), CC.xyw(1, 3, 2));
		load.addActionListener(new PageNumberLoadAction(mf, pageNumberRanges));
		updateEnabledState();
	}

	public static TableComponent buildPageNumberRanges() {
		TableComponent result = new TableComponent(new String[] { "Start Page", "Style", "Prefix", "Logical Page" },
				new Class[] { Integer.class, String.class, String.class, Integer.class },
				new Object[] { 1, NUMBER_STYLES[0], "", 1 });
		JComboBox styleValues = new JComboBox(NUMBER_STYLES);
		result.getTable().getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(styleValues));
		return result;
	}

	protected void updateEnabledState() {
		load.setEnabled(changePageNumbers.isSelected());
		pageNumberRanges.setEnabled(changePageNumbers.isSelected());
	}

	
	public String getTabName() {
		return "Page Numbers";
	}

	
	public void checkRun() throws IOException {
		pageNumberRanges.checkRun("page number");
	}

	
	public PDFTwist run(PDFTwist pdfTwist, OutputProgressDialog outDialog) throws IOException, DocumentException {
		outDialog.updateJPDFTwistProgress(getTabName());
		if (changePageNumbers.isSelected()) {
			updatePageNumberRanges(pdfTwist, pageNumberRanges, outDialog);
		}
		return pdfTwist;
	}

	public static void updatePageNumberRanges(PDFTwist pdfTwist, TableComponent pageNumberRanges,
											  OutputProgressDialog outDialog) throws DocumentException, IOException {
		PdfPageLabelFormat[] fmts = new PdfPageLabelFormat[pageNumberRanges.getRowCount()];
		for (int i = 0; i < fmts.length; i++) {
			Object[] row = pageNumberRanges.getRow(i);
			int nstyle = Arrays.asList(NUMBER_STYLES).indexOf(row[1]);
			if (nstyle == -1)
				nstyle = 0;
			fmts[i] = new PdfPageLabelFormat((Integer) row[0], nstyle, (String) row[2], (Integer) row[3]);
		}
		pdfTwist.setPageNumbers(fmts, outDialog);
	}

	public static class PageNumberLoadAction implements ActionListener {

		private final TableComponent pageNumberRanges;
		private final MainForm mainForm;

		public PageNumberLoadAction(MainForm mainForm, TableComponent pageNumberRanges) {
			this.mainForm = mainForm;
			this.pageNumberRanges = pageNumberRanges;
		}

		public void actionPerformed(ActionEvent e) {
			//			pageNumberRanges.clear();
			//			if (mainForm.getInputFile() == null)
			//				return;
			//			PdfPageLabelFormat[] lbls = mainForm.getInputFile().getPageLabels();
			//			if (lbls != null) {
			//				for (PdfPageLabelFormat lbl : lbls) {
			//					pageNumberRanges.addRow(lbl.physicalPage,
			//							NUMBER_STYLES[lbl.numberStyle], lbl.prefix,
			//							lbl.logicalPage);
			//				}
			//			}
		}

	}
}

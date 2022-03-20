package jpdftweak.tabs;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import jpdftweak.core.PdfBookmark;
import jpdftweak.core.PdfTweak;
import jpdftweak.gui.MainForm;
import jpdftweak.gui.TableComponent;
import jpdftweak.gui.dialogs.OutputProgressDialog;
import jpdftweak.tabs.input.FileChooser;
import jpdftweak.utils.PdfParser;

public class BookmarkTab extends Tab {

	private JButton load;
	private JButton importPDF, importCSV, exportCSV;
	private TableComponent bookmarks;
	private JCheckBox changeBookmarks;
	private final MainForm mainForm;

	public BookmarkTab(MainForm mf) {
		super(new FormLayout("f:p:g, f:p", "f:p, f:p, f:p:g"));
		this.mainForm = mf;
		CellConstraints CC = new CellConstraints();
		add(changeBookmarks = new JCheckBox("Change chapter bookmarks"), CC.xy(1, 1));
		changeBookmarks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateEnabledState();
			}
		});
		add(load = new JButton("Load from document"), CC.xy(2, 1));
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<PdfBookmark> bm = mainForm.getInputTab().loadBookmarks();
				bookmarks.clear();
				appendBookmarks(bm);
			}
		});
		JPanel panel = new JPanel(new GridLayout(1, 3));
		panel.add(importPDF = new JButton("Import from PDF"));
		importPDF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileChooser fileChooser = new FileChooser();

				JFileChooser chooser = fileChooser.getFileChooser();
				if (chooser.showOpenDialog(mainForm) == JFileChooser.APPROVE_OPTION) {
					try {
						bookmarks.clear();
						PdfReader reader = PdfParser.open(chooser.getSelectedFile().getAbsolutePath(), false);
						List bmk = SimpleBookmark.getBookmark(reader);
						appendBookmarks(PdfBookmark.parseBookmarks(bmk, 1));
						reader.close();
					} catch (Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(mainForm, ex.getMessage(), "Error reading file",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		panel.add(importCSV = new JButton("Import from CSV"));
		importCSV.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				if (jfc.showOpenDialog(mainForm) == JFileChooser.APPROVE_OPTION) {
					importCSV(jfc.getSelectedFile());
				}
			}

		});
		panel.add(exportCSV = new JButton("Export to CSV"));
		exportCSV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				if (jfc.showSaveDialog(mainForm) == JFileChooser.APPROVE_OPTION) {
					File f = jfc.getSelectedFile();
					if (f.exists()) {
						if (JOptionPane.showConfirmDialog(mainForm, "Overwrite existing file?", "Confirm Overwrite",
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
							return;
						}
					}
					exportCSV(f);
				}
			}
		});
		add(panel, CC.xyw(1, 2, 2));
		add(bookmarks = new TableComponent(
				new String[] { "Depth", "Open", "Title", "Page", "Position", "Bold", "Italic", "Options" },
				new Class[] { Integer.class, Boolean.class, String.class, Integer.class, String.class, Boolean.class,
						Boolean.class, String.class },
				new Object[] { 1, false, "", 1, "", false, false, "" }), CC.xyw(1, 3, 2));
		updateEnabledState();
	}

	protected void importCSV(File selectedFile) {
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(selectedFile), "UTF-8"));
			String line;
			List<PdfBookmark> bmks = new ArrayList<PdfBookmark>();
			while ((line = r.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				bmks.add(PdfBookmark.parseBookmark(line));
			}
			r.close();
			bookmarks.clear();
			appendBookmarks(bmks);
		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(mainForm, ex.getMessage(), "Error reading file", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void exportCSV(File selectedFile) {
		try {
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(selectedFile), "UTF-8"));
			for (int i = 0; i < bookmarks.getRowCount(); i++) {
				PdfBookmark b = getBookmark(bookmarks.getRow(i));
				w.write(b.toString());
				w.newLine();
			}
			w.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(mainForm, ex.getMessage(), "Error reading file", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void appendBookmarks(List<PdfBookmark> bm) {
		for (PdfBookmark b : bm) {
			bookmarks.addRow(b.getDepth(), b.isOpen(), b.getTitle(), b.getPage(), b.getPagePosition(), b.isBold(),
					b.isItalic(), b.getMoreOptions());
		}
	}

	protected PdfBookmark getBookmark(Object[] row) {
		int depth = row[0] == null ? 1 : (Integer) row[0];
		boolean open = (Boolean) row[1];
		String title = (String) row[2];
		int page = row[3] == null ? 1 : (Integer) row[3];
		String pagePosition = (String) row[4];
		boolean bold = (Boolean) row[5];
		boolean italic = (Boolean) row[6];
		String moreOptions = (String) row[7];
		return new PdfBookmark(depth, title, open, page, pagePosition, bold, italic, moreOptions);
	}

	protected void updateEnabledState() {
		load.setEnabled(changeBookmarks.isSelected());
		importPDF.setEnabled(changeBookmarks.isSelected());
		importCSV.setEnabled(changeBookmarks.isSelected());
		exportCSV.setEnabled(changeBookmarks.isSelected());
		bookmarks.setEnabled(changeBookmarks.isSelected());
	}

	
	public String getTabName() {
		return "Bookmarks";
	}

	
	public void checkRun() throws IOException {
		bookmarks.checkRun("chapter bookmarks");
	}

	
	public PdfTweak run(PdfTweak tweak, OutputProgressDialog outDialog) throws IOException, DocumentException {
		outDialog.updateTweaksProgress(getTabName());
		outDialog.setAction("Updating bookmarks");
		outDialog.resetProcessedPages();
		if (changeBookmarks.isSelected()) {
			PdfBookmark[] bm = new PdfBookmark[bookmarks.getRowCount()];
			for (int i = 0; i < bm.length; i++) {
				bm[i] = getBookmark(bookmarks.getRow(i));
			}
			tweak.updateBookmarks(bm);
		}
		return tweak;
	}

}

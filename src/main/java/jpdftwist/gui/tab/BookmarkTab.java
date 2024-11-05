package jpdftwist.gui.tab;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jpdftwist.core.OutputEventListener;
import jpdftwist.core.PDFTwist;
import jpdftwist.core.PdfBookmark;
import jpdftwist.gui.MainWindow;
import jpdftwist.gui.component.ColorChooserButton;
import jpdftwist.gui.component.FileChooser;
import jpdftwist.gui.component.table.TableComponent;
import jpdftwist.utils.PdfParser;
import jpdftwist.utils.PreferencesUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookmarkTab extends Tab {

    private final MainWindow mainWindow;
    private final JButton load;
    private final JButton colorBM;
    private final JButton extraBM;
    private final JButton importPDF;
    private final JButton importCSV;
    private final JButton exportCSV;
    private final TableComponent bookmarks;
    private final JCheckBox changeBookmarks;

    
    public BookmarkTab(MainWindow mf) {
        super(new FormLayout("f:p:g, f:p", "f:p, f:p, f:p:g"));
        this.mainWindow = mf;
        CellConstraints CC = new CellConstraints();
        add(changeBookmarks = new JCheckBox("Change chapter bookmarks"), CC.xy(1, 1));
        changeBookmarks.addActionListener(e -> updateEnabledState());
        
        add(load = new JButton("Load from document"), CC.xy(2, 1));
        load.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<PdfBookmark> bm = mainWindow.getInputTab().loadBookmarks();
                bookmarks.clear();
                appendBookmarks(bm);
            }
        });
        JPanel panel = new JPanel(new GridLayout(1, 5));
        panel.add(importPDF = new JButton("Import from PDF"));
        importPDF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileChooser fileChooser = new FileChooser();

                JFileChooser chooser = fileChooser.getFileChooser();
                if (chooser.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
                    try {
                        bookmarks.clear();
                        PdfReader reader = PdfParser.open(chooser.getSelectedFile().getAbsolutePath(), false);
                        List bmk = SimpleBookmark.getBookmark(reader);
                        appendBookmarks(PdfBookmark.parseBookmarks(bmk, 1));
                        reader.close();
                    } catch (Exception ex) {
                        Logger.getLogger(BookmarkTab.class.getName()).log(Level.SEVERE, "Ex064", ex);
                        JOptionPane.showMessageDialog(mainWindow, ex.getMessage(), "Error reading file", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        panel.add(importCSV = new JButton("Import from CSV"));
        importCSV.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            if (jfc.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
                importCSV(jfc.getSelectedFile());
            }
        });
        panel.add(exportCSV = new JButton("Export to CSV"));
        exportCSV.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            if (jfc.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
                File f = jfc.getSelectedFile();
                if (f.exists()) {
                    if (JOptionPane.showConfirmDialog(mainWindow, "Overwrite existing file?", "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
                exportCSV(f);
            }
        });
        panel.add(colorBM = new JButton("Color"));
        colorBM.addActionListener(e -> {
			try {
				Color current = Color.BLACK;
				Color defaultColor = JColorChooser.showDialog(null, "Choose a Color", current);

				double dR = (float) defaultColor.getRed() / 255.0;
				double dG = (float) defaultColor.getGreen() / 255.0;
				double dB = (float) defaultColor.getBlue() / 255.0;

				String RD = String.format("%.1f", dR);
				String GR = String.format("%.1f", dG);
				String BL = String.format("%.1f", dB);

//				String RD = String.valueOf(dR);
//				String GR = String.valueOf(dG);
//				String BL = String.valueOf(dB);

				
			    //set text on clipboard
			    StringSelection selection = new StringSelection("Color=\"" + RD + " " + GR + " " + BL + "\"");
				 Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				    clipboard.setContents(selection, selection);
				
				    
				//selectable text from JOptionPane
//				Color SelectedColor = new Color((int)dR, (int)dG, (int)dB);
//                JTextArea TA = new JTextArea(1, 16);
//                TA.setText("Color=\"" + RD + " " + GR + " " + BL + "\"");
//                TA.setWrapStyleWord(false);
//                TA.setLineWrap(false);
//                TA.setCaretPosition(TA.getText().length());
//                TA.setEditable(false);
//				JOptionPane.showMessageDialog(null, new JScrollPane(TA), "Paste into Table", JOptionPane.INFORMATION_MESSAGE);
				
				    
				JOptionPane.showMessageDialog(null, "Color=\"" + RD + " " + GR + " " + BL + "\"", 
						"--->  Paste into Color column", JOptionPane.INFORMATION_MESSAGE);

			} catch (Exception ex) { }
        });
        panel.add(extraBM = new JButton("Options"));
    	extraBM.setEnabled(false);
        extraBM.addActionListener(e -> {
        	
        });
        
        add(panel, CC.xyw(1, 2, 2));
        add(bookmarks = new TableComponent(
//          new String[]{"Depth", "Open", "Title", "Page", "Position", "Bold", "Italic", "Ex:    Color=\"0.5 0 1\"  "},
            new String[]{"Depth", "Open", "Title", "Page", "Position", "Bold", "Italic", "Color"},
            
            new Class[]{Integer.class, Boolean.class, String.class, Integer.class, String.class, Boolean.class,
                Boolean.class, String.class},
            new Object[]{1, false, "", 1, "", false, false, ""}), CC.xyw(1, 3, 2));
        updateEnabledState();
    }

    protected void importCSV(File selectedFile) {
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(Files.newInputStream(selectedFile.toPath()), StandardCharsets.UTF_8));
            String line;
            List<PdfBookmark> bmks = new ArrayList<>();
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
            Logger.getLogger(BookmarkTab.class.getName()).log(Level.SEVERE, "Ex065", ex);
            JOptionPane.showMessageDialog(mainWindow, ex.getMessage(), "Error reading file", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void exportCSV(File selectedFile) {
        try {
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(selectedFile.toPath()), StandardCharsets.UTF_8));
            for (int i = 0; i < bookmarks.getRowCount(); i++) {
                PdfBookmark b = getBookmark(bookmarks.getRow(i));
                w.write(b.toString());
                w.newLine();
            }
            w.close();
        } catch (IOException ex) {
            Logger.getLogger(BookmarkTab.class.getName()).log(Level.SEVERE, "Ex066", ex);
            JOptionPane.showMessageDialog(mainWindow, ex.getMessage(), "Error reading file", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void appendBookmarks(List<PdfBookmark> pdfBookmarks) {
        for (PdfBookmark b : pdfBookmarks) {
            bookmarks.addRow(b.getDepth(), b.isOpen(), b.getTitle(), b.getPage(), b.getPagePosition(), b.isBold(), b.isItalic(), b.getMoreOptions());
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
        colorBM.setEnabled(changeBookmarks.isSelected());
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

    public PDFTwist run(PDFTwist pdfTwist, OutputEventListener outputEventListener) throws IOException, DocumentException {
        outputEventListener.updateJPDFTwistProgress(getTabName());
        outputEventListener.setAction("Updating bookmarks");
        outputEventListener.resetProcessedPages();
        if (changeBookmarks.isSelected()) {
            PdfBookmark[] bm = new PdfBookmark[bookmarks.getRowCount()];
            for (int i = 0; i < bm.length; i++) {
                bm[i] = getBookmark(bookmarks.getRow(i));
            }
            pdfTwist.updateBookmarks(bm);
        }
        return pdfTwist;
    }    
}

package jpdftwist.gui.tab;

import com.itextpdf.text.pdf.PdfName;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jpdftwist.core.OutputEventListener;
import jpdftwist.core.PDFTwist;
import jpdftwist.gui.MainWindow;
import jpdftwist.gui.component.table.TableComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class DocumentInfoTab extends Tab {

    private final JCheckBox infoChange;
    private final TableComponent infoEntries;
    private final JButton infoLoad;
    private final JButton infoAdd;
    private static final PdfName[] INFO_NAMES = {PdfName.TITLE, PdfName.SUBJECT, PdfName.KEYWORDS, PdfName.AUTHOR,
        PdfName.CREATOR, PdfName.PRODUCER, PdfName.CREATIONDATE, PdfName.MODDATE};

    public DocumentInfoTab(MainWindow mf) {
        super(new FormLayout("f:p:g, f:p", "f:p, f:p, f:p:g"));
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
        infoLoad.addActionListener(e -> {
        });
        this.add(infoAdd = new JButton("Add predefined..."), CC.xyw(1, 2, 2));
        infoAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPopupMenu pm = new JPopupMenu();
                JMenuItem jmi;
                for (String name : getKnownInfoNames()) {
                    pm.add(jmi = new JMenuItem(name));
                    jmi.addActionListener(e1 -> {
                        String text = ((JMenuItem) e1.getSource()).getText();
                        infoEntries.addRow(text, "");
                    });
                }
                pm.show(infoAdd, 0, infoAdd.getHeight());
            }
        });
        this.add(infoEntries = new TableComponent(new String[]{"Name", "Value"},
            new Class[]{String.class, String.class}, new Object[]{"", ""}), CC.xyw(1, 3, 2));
        infoLoad.setEnabled(false);
        infoAdd.setEnabled(false);
        infoEntries.setEnabled(false);
    }

    public String getTabName() {
        return "Document Info";
    }

    public PDFTwist run(PDFTwist pdfTwist, OutputEventListener outputEventListener) {
        outputEventListener.updateJPDFTwistProgress(getTabName());
        outputEventListener.setAction("Updating info");
        outputEventListener.resetProcessedPages();
        if (infoChange.isSelected()) {
            Map<String, String> newInfo = new HashMap<>();
            for (int i = 0; i < infoEntries.getRowCount(); i++) {
                Object[] row = infoEntries.getRow(i);
                String key = (String) row[0], value = (String) row[1];
                newInfo.put(key, value);
            }
            pdfTwist.updateInfoDictionary(newInfo);
        }
        return pdfTwist;
    }

    private static String[] getKnownInfoNames() {
        String[] result = new String[INFO_NAMES.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = INFO_NAMES[i].toString().substring(1);
        }
        return result;
    }
}

package jpdftwist.gui.tab;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfWriter;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import jpdftwist.core.OutputEventListener;
import jpdftwist.core.PDFTwist;
import jpdftwist.core.ViewerPreference;
import jpdftwist.gui.MainWindow;
import jpdftwist.gui.component.table.TableComponent;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InteractionTab extends Tab {
    private final JCheckBox[] optionalPrefCheck = new JCheckBox[ViewerPreference.SUPPORTED_VIEWER_PREFERENCES.length];
    private final JComboBox[] optionalPrefValue = new JComboBox[ViewerPreference.SUPPORTED_VIEWER_PREFERENCES.length];
    private final TableComponent transitions;
    private final JCheckBox addTransitions;
    private final JCheckBox addPrefs;
    private final JComboBox<Object> pageMode;
    private final JComboBox<Object> pageLayout;

    public InteractionTab(MainWindow mf) {
        super(new BorderLayout());
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.add(addTransitions = new JCheckBox("Add page transitions"), BorderLayout.NORTH);
        ActionListener updateListener = e -> updateEnabledState();
        addTransitions.addActionListener(updateListener);
        panel1.add(transitions = new TableComponent(
            new String[]{"First Page", "Last Page", "Transition", "TransDur.", "PageDur."},
            new Class[]{Integer.class, Integer.class, String.class, Integer.class, Integer.class},
            new Object[]{1, -1, "None", 0, -1}), BorderLayout.CENTER);
        transitions.getScrollPane().setPreferredSize(new Dimension(200, 300));
        TableColumn c = transitions.getTable().getColumnModel().getColumn(2);
        c.setPreferredWidth(200);
        c.setCellEditor(new DefaultCellEditor(new JComboBox<>(PDFTwist.TRANSITION_NAMES)));
        FormLayout fl;
        JPanel panel2 = new JPanel(fl = new FormLayout("f:p, f:p:g", "f:p, f:p, f:p, 10dlu"));
        CellConstraints CC = new CellConstraints();
        panel2.add(addPrefs = new JCheckBox("Set Viewer Preferences"), CC.xyw(1, 1, 2));
        addPrefs.addActionListener(updateListener);
        panel2.add(new JLabel("Page Mode: "), CC.xy(1, 2));
        panel2.add(pageMode = new JComboBox<>(
                new Object[]{"None", "Outline", "Thumbnails", "Full Screen", "Optional Content", "Attachments"}),
            CC.xy(2, 2));
        panel2.add(new JLabel("Page Layout: "), CC.xy(1, 3));
        panel2.add(pageLayout = new JComboBox<>(new Object[]{"Single Page", "One Column", "Two Columns Left",
            "Two Columns Right", "Two Pages Left", "Two Pages Right"}), CC.xy(2, 3));
        panel2.add(new JSeparator(), CC.xyw(1, 4, 2));
        for (int i = 0; i < ViewerPreference.SUPPORTED_VIEWER_PREFERENCES.length; i++) {
            ViewerPreference vp = ViewerPreference.SUPPORTED_VIEWER_PREFERENCES[i];
            fl.appendRow(RowSpec.decode("f:p"));
            panel2.add(optionalPrefCheck[i] = new JCheckBox(vp.getName() + ": "), CC.xy(1, i + 5));
            panel2.add(optionalPrefValue[i] = new JComboBox<>(vp.getPossibleValues()), CC.xy(2, i + 5));
            optionalPrefCheck[i].addActionListener(updateListener);
        }
        JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel1, panel2);
        jsp.setResizeWeight(0.5f);
        add(jsp, BorderLayout.CENTER);
        updateEnabledState();
    }

    protected void updateEnabledState() {
        transitions.setEnabled(addTransitions.isSelected());
        boolean b = addPrefs.isSelected();
        pageMode.setEnabled(b);
        pageLayout.setEnabled(b);
        for (int i = 0; i < optionalPrefCheck.length; i++) {
            optionalPrefCheck[i].setEnabled(b);
            optionalPrefValue[i].setEnabled(b && optionalPrefCheck[i].isSelected());
        }
    }

    public String getTabName() {
        return "Interaction";
    }

    public void checkRun() throws IOException {
        transitions.checkRun("page transition");
    }

    public PDFTwist run(PDFTwist pdfTwist, OutputEventListener outputEventListener) throws IOException, DocumentException {
        outputEventListener.updateJPDFTwistProgress(getTabName());
        outputEventListener.setAction("Interaction");
        outputEventListener.resetProcessedPages();
        if (addTransitions.isSelected()) {
            for (int i = 0; i < transitions.getRowCount(); i++) {
                Object[] row = transitions.getRow(i);
                int from = (Integer) row[0];
                int to = (Integer) row[1];
                String transition = (String) row[2];
                int trans = Arrays.asList(PDFTwist.TRANSITION_NAMES).indexOf(transition);
                if (trans == -1)
                    throw new RuntimeException();
                int duration = (Integer) row[3];
                int pdur = (Integer) row[4];
                if (from < 0)
                    from += pdfTwist.getPageCount() + 1;
                if (to < 0)
                    to += pdfTwist.getPageCount() + 1;
                for (int j = from; j <= to; j++) {
                    pdfTwist.setTransition(j, trans, duration, pdur);
                }
            }
        }
        if (addPrefs.isSelected()) {
            int simplePrefs = (PdfWriter.PageLayoutSinglePage << pageLayout.getSelectedIndex())
                + (PdfWriter.PageModeUseNone << pageMode.getSelectedIndex());
            Map<PdfName, PdfObject> optionalPrefs = new HashMap<>();
            for (int i = 0; i < optionalPrefCheck.length; i++) {
                if (optionalPrefCheck[i].isSelected()) {
                    optionalPrefs.put(ViewerPreference.SUPPORTED_VIEWER_PREFERENCES[i].getInternalName(),
                        (PdfObject) optionalPrefValue[i].getSelectedItem());
                }
            }
            pdfTwist.setViewerPreferences(simplePrefs, optionalPrefs);
        }
        return pdfTwist;
    }
}

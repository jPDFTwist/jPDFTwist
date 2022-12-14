package jpdftwist.gui.dialog;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jpdftwist.tabs.input.error.ErrorReportExporter;
import jpdftwist.tabs.input.error.XmlExporter;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.HashMap;

/**
 * @author Vasilis Naskos
 */
public class ErrorDialog extends JFrame {

    private JSplitPane splitPane;
    private JButton saveButton, okButton;
    private JTextArea exceptionArea;
    private DefaultListModel model;
    private HashMap<String, String> exceptionsMap;

    public ErrorDialog() {
        initComponents();
        buildGui();
        setupFrame();
    }

    private void initComponents() {
        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveToFile());

        okButton = new JButton("OK");
        okButton.addActionListener(e -> dispose());

        exceptionArea = new JTextArea();
        exceptionArea.setEditable(false);

        JScrollPane exceptionPane = new JScrollPane(exceptionArea);
        exceptionArea.setRows(4);
        exceptionPane.setBorder(new TitledBorder(new EtchedBorder(), "Exception Details"));

        exceptionsMap = new HashMap<>();

        model = new DefaultListModel();
        JList errorFileList = new JList(model);
        errorFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        errorFileList.addListSelectionListener(e -> {
            int indexSelected = e.getFirstIndex();
            String selectedValue = model.get(indexSelected).toString();
            String exception = exceptionsMap.get(selectedValue);
            exceptionArea.setText(exception);
            exceptionArea.setCaretPosition(0);
        });

        JScrollPane errorListPane = new JScrollPane(errorFileList);
        exceptionPane.setBorder(new EtchedBorder());
        exceptionPane.setMaximumSize(new Dimension(400, 200));

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, errorListPane, exceptionPane);
    }

    private void buildGui() {
        FormLayout layout = new FormLayout("$lcgap, f:p:g, $lcgap, 40dlu, $lcgap, 40dlu, $lcgap",
            "$lgap, f:p:g, $lgap, f:p, $lgap");

        PanelBuilder builder = new PanelBuilder(layout);

        CellConstraints CC = new CellConstraints();

        builder.add(splitPane, CC.xyw(2, 2, 5));
        builder.add(saveButton, CC.xy(4, 4));
        builder.add(okButton, CC.xy(6, 4));

        this.add(builder.getPanel());
    }

    private void setupFrame() {
        this.setMinimumSize(new Dimension(450, 500));
        this.setResizable(false);
        this.setTitle("Input Progress");
        this.setUndecorated(false);
        this.setResizable(true);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    public void reportError(String filepath, String ex) {
        int position = model.getSize();
        model.add(position, filepath);
        exceptionsMap.put(filepath, ex);
    }

    public void showErrors() {
        if (!model.isEmpty())
            this.setVisible(true);
    }

    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File exportFile = fileChooser.getSelectedFile();
        String exportPath = exportFile.getPath();

        ErrorReportExporter exporter = getAppropriateExporter(exportPath);
        exporter.setData(exceptionsMap);
        exporter.export(exportPath);
    }

    // Currently only XML Exporter is implemented
    // TODO: Extract logic to actions
    private ErrorReportExporter getAppropriateExporter(String exportPath) {
        ErrorReportExporter exporter;
        //        exportPath = exportPath.toLowerCase();

        //        if(exportPath.endsWith(".xml")) {
        exporter = new XmlExporter();
        //        }

        return exporter;
    }

}

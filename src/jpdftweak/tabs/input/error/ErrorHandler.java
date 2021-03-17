package jpdftweak.tabs.input.error;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Vasilis Naskos
 */
public class ErrorHandler extends JFrame {

    private JSplitPane splitPane;
    private JButton saveButton, okButton;
    private JScrollPane exceptionPane, errorListPane;
    private JTextArea exceptionArea;
    private DefaultListModel model;
    private JList errorFileList;
    private HashMap<String, String> exceptionsMap;
    
    public ErrorHandler() {
        initComponents();
        buildGui();
        setupFrame();
    }
    
    private void initComponents() {
        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveToFile();
            }
        });
        
        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        exceptionArea = new JTextArea();
        exceptionArea.setEditable(false);
        
        exceptionPane = new JScrollPane(exceptionArea);
        exceptionArea.setRows(4);
        exceptionPane.setBorder(
                new TitledBorder(new EtchedBorder(), "Exception Details"));
        
        exceptionsMap = new HashMap<String, String>();
        
        model = new DefaultListModel();
        errorFileList = new JList(model);
        errorFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        errorFileList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int indexSelected = e.getFirstIndex();
                String selectedValue = model.get(indexSelected).toString();
                String exception = exceptionsMap.get(selectedValue);
                exceptionArea.setText(exception);
                exceptionArea.setCaretPosition(0);
            }
        });

        errorListPane = new JScrollPane(errorFileList);
        exceptionPane.setBorder(new EtchedBorder());
        exceptionPane.setMaximumSize(new Dimension(400,200));
        
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, errorListPane, exceptionPane);
    }
    
    private void buildGui() {
        FormLayout layout = new FormLayout(
                "$lcgap, f:p:g, $lcgap, 40dlu, $lcgap, 40dlu, $lcgap",
                "$lgap, f:p:g, $lgap, f:p, $lgap");
        
        PanelBuilder builder = new PanelBuilder(layout);
        
        CellConstraints cc = new CellConstraints();
        
        builder.add(splitPane, cc.xyw(2, 2, 5));
        builder.add(saveButton, cc.xy(4, 4));
        builder.add(okButton, cc.xy(6, 4));
        
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
        if(!model.isEmpty())
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
    
    //Currently only XML Exporter is implemented
    private ErrorReportExporter getAppropriateExporter(String exportPath) {
        ErrorReportExporter exporter;
//        exportPath = exportPath.toLowerCase();
        
//        if(exportPath.endsWith(".xml")) {
        exporter = new XmlExporter();
//        }
        
        return exporter;
    }
    
}

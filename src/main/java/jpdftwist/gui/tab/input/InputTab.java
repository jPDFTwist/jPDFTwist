package jpdftwist.gui.tab.input;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jpdftwist.core.IntegerList;
import jpdftwist.gui.component.FileChooser;
import jpdftwist.gui.component.ImagePreviewPanel;
import jpdftwist.gui.component.treetable.Node;
import jpdftwist.gui.component.treetable.TreeTableComponent;
import jpdftwist.gui.component.treetable.TreeTableModel;
import jpdftwist.tabs.input.FileImporter;
import jpdftwist.tabs.input.InputTabFileImporter;
import jpdftwist.tabs.input.ModelHandler;
import jpdftwist.tabs.input.treetable.InputTabControlListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author Vasilis Naskos
 * TODO: Move logic to actions
 */
public class InputTab extends JPanel {

    private final CellConstraints CC;
    private JTextField fileCountField;
    private final ImagePreviewPanel previewPanel;
    private final TreeTableComponent inputFilesTable;
    private JButton selectFile;
    private JButton clear;
    private JButton generate;
    private InputOptionsPanel optionsPanel;
    private final TreeTableModel model;
    private final GenerateInputItemsDialog generateFrame;

    private final InputTabFileImporter inputTabFileImporter;

    public static InputTab getInputPanel() {
        return new InputTab();
    }

    public InputTab() {
        super(new FormLayout("f:p, f:p:g, f:p:g, f:p, f:p, f:p, f:p, f:p, f:p, f:p", "f:p, f:p, f:p:g"));

        this.CC = new CellConstraints();

        String[] columnHeaders = new String[]{"File", "ID", "PaperSize", "Orientation", "Color Depth", "Size", "Pages",
            "From Page", "To Page", "Include Odd", "Include Even", "Empty Before", "Bookmark Level"};

        Class[] itemClassType = new Class[]{String.class, String.class, String.class, String.class, String.class,
            Integer.class, Integer.class, Integer.class, Integer.class, Boolean.class, Boolean.class,
            IntegerList.class, Integer.class};

        inputTabFileImporter = new InputTabFileImporter() {
            final ModelHandler modelHandler = new ModelHandler() {

                public void insertFileNode(Node node) {
                    Node parent = model.createParents(node.getUserObject().getKey());
                    model.insertNodeInto(node, parent, parent.getChildCount());

                    updateFileCount();
                }

                public void updateTableUI() {
                    inputFilesTable.updateTreeTableUI();
                }
            };

            @Override
            public void importFilesToInputTab(File[] files) {
                final ImportItemsListener importItemsListener = new DialogsImportItemsListener();
                final FileImporter importer = new FileImporter(modelHandler, importItemsListener, files);
                importer.setOptimizePDF(optionsPanel.isOptimizePDFSelected());
                importer.setAutoRestrictionsOverwrite(optionsPanel.isAutoRestrictionsOverwriteSelected());
                importer.setAutoRestrictionsNew(optionsPanel.isAutoRestrictionsNewSelected());
                new Thread(importer).start();
            }
        };

        this.previewPanel = new ImagePreviewPanel();

        InputTabControlListener inputTabControlListener = new InputTabControlListener(previewPanel, inputTabFileImporter);
        inputFilesTable = new TreeTableComponent(columnHeaders, itemClassType, inputTabControlListener);
        inputFilesTable.getTreeTable().setBackground(new Color(230, 230, 250));
        model = inputFilesTable.getModel();

        generateUserInterface();
        updateFileCount();

        GenerateInputItemsDialog.Listener l = new GenerateInputItemsDialog.Listener() {
            public void importNode(Node node, int index) {
                Node parent = model.createParents(node.getUserObject().getKey());
                model.insertNodeInto(node, parent, index);
            }

            public void insertNodeInto(Node node, Node parent, int index) {
                model.insertNodeInto(node, parent, index);
            }
        };
        generateFrame = new GenerateInputItemsDialog(l);
    }

    private void generateUserInterface() {
        initializeFileRowComponents();
        positionFileRowComponents();

        generateOptionsPanel();

        final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputFilesTable, previewPanel);
        splitPane.setResizeWeight(0.9);
        this.add(splitPane, this.CC.xyw(1, 3, 9));
    }

    private void initializeFileRowComponents() {
        fileCountField = new JTextField();
        fileCountField.setEditable(false);

        selectFile = new JButton("Select...");
        selectFile.addActionListener(e -> importFilesActionPerformed());

        clear = new JButton("Clear");
        clear.addActionListener(clearButtonListener());

        generate = new JButton("Generate");
        generate.addActionListener(generateButtonListener());
    }

    private void importFilesActionPerformed() {
        FileChooser fileChooser = new FileChooser();
        File[] selectedFiles = fileChooser.getSelectedFiles();
        if (selectedFiles == null) {
            return;
        }
        inputTabFileImporter.importFilesToInputTab(selectedFiles);
    }

    private ActionListener clearButtonListener() {
        return e -> clearActionPerformed();
    }

    private ActionListener generateButtonListener() {
        return e -> {
            generateFrame.setSelectedNode(inputFilesTable.getSelected());
            generateFrame.setVisible(true);
        };
    }

    private void clearActionPerformed() {
        this.previewPanel.clearPreview();
        this.model.clear();
        this.updateFileCount();
    }

    private void updateFileCount() {
        String fn;

        if (model.isEmpty())
            fn = "(No file selected)";
        else
            fn = "(" + model.getFileCount(model.getRoot()) + " files selected)";

        fileCountField.setText(fn);
    }

    private void positionFileRowComponents() {
        this.add(new JLabel("Filename"), CC.xy(1, 1));
        this.add(fileCountField, CC.xyw(2, 1, 2));
        this.add(selectFile, CC.xy(4, 1));
        this.add(clear, CC.xy(5, 1));
        this.add(generate, CC.xy(6, 1));
    }

    private void generateOptionsPanel() {
        this.add(this.optionsPanel = new InputOptionsPanel(), this.CC.xyw(1, 2, 7));
    }

    public void setUseTempFiles(boolean useTempFiles) {
        System.out.println("Use temp files is " + useTempFiles);
        //TODO: not supported
    }

    public boolean isModelEmpty() {
        return model.isEmpty();
    }

    public Node getRootNode() {
        return model.getRoot();
    }

    public boolean isMergeByDirSelected() {
        return optionsPanel.isMergeByDirSelected();
    }

    public boolean isBatchSelected() {
        return optionsPanel.isBatchSelected();
    }

    public boolean isInterleaveSelected() {
        return optionsPanel.isInterleaveSelected();
    }

    public int getInterleaveSize() {
        String interleaveSizeValue = optionsPanel.getInterleaveSize();

        return Integer.parseInt(interleaveSizeValue);
    }

    // TODO
    public int getBatchLength() {
        if (isBatchSelected()) {
            return model.getFileCount(model.getRoot());
        } else if (isMergeByDirSelected()) {
            return model.getFolderCount(model.getRoot());
        } else {
            return 1;
        }
    }
}

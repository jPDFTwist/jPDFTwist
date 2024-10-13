package jpdftwist.gui.tab.input;

import com.esotericsoftware.kryo.Kryo;
import com.itextpdf.text.Rectangle;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jpdftwist.core.UnitTranslator;
import jpdftwist.gui.component.ColorChooserButton;
import jpdftwist.gui.component.FileChooser;
import jpdftwist.gui.component.treetable.Node;
import jpdftwist.gui.component.treetable.row.FileTreeTableRow;
import jpdftwist.gui.component.treetable.row.FolderTreeTableRow;
import jpdftwist.gui.component.treetable.row.PageTreeTableRow;
import jpdftwist.gui.component.treetable.row.TreeTableRowType;
import jpdftwist.gui.component.treetable.row.VirtualFileTreeTableRow;
import jpdftwist.tabs.input.treetable.node.NodeFactory;
import jpdftwist.tabs.input.treetable.node.VirtualBlankNodeFactory;
import jpdftwist.tabs.input.treetable.node.VirtualImageNodeFactory;
import jpdftwist.tabs.input.treetable.node.VirtualPdfNodeFactory;
import jpdftwist.utils.SupportedFileTypes;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenerateInputItemsDialog extends JFrame {

    private ColorChooserButton backgroundColorChooserButton;
    private JComboBox<String> unitsComboBox;
    private JRadioButton blankDocumentRadioButton;
    private SpinnerNumberModel numberOfPagesModel;
    private SpinnerNumberModel widthModel;
    private SpinnerNumberModel heightModel;
    private JSpinner repeatSpinner;
    private JTextField repeatFileTextField;
    private JSpinner levelsSpinner;
    private JPanel blankDocumentPanel;
    private JPanel repeatFilePanel;
    private final Listener l;
    private Node node;

    public GenerateInputItemsDialog(Listener l) {
        initComponents();

        this.setLocationRelativeTo(null);
        radioGroupStateChange();

        this.l = l;
    }

    public final void setSelectedNode(Node node) {
        this.node = node;
    }

    private void initComponents() {
        blankDocumentRadioButton = new JRadioButton();
        blankDocumentRadioButton.setSelected(true);
        blankDocumentRadioButton.setText("Blank Document");
        blankDocumentRadioButton.addItemListener(this::jRadioButton1ItemStateChanged);
        widthModel = new SpinnerNumberModel(8.5F, 1F, null, 1F);
        JSpinner widthSpinner = new JSpinner(widthModel);
        heightModel = new SpinnerNumberModel(11F, 1F, null, 1F);
        JSpinner heightSpinner = new JSpinner(heightModel);
        unitsComboBox = new JComboBox<>();
        unitsComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"inches", "mm", "points"}));
        numberOfPagesModel = new SpinnerNumberModel(2, 1, null, 1);
        JSpinner numberOfPagesSpinner = new JSpinner(numberOfPagesModel);
        backgroundColorChooserButton = new ColorChooserButton();
        backgroundColorChooserButton.setSelectedColor(new java.awt.Color(254, 254, 254));
        JRadioButton fileRepeatRadioButton = new JRadioButton();
        fileRepeatRadioButton.setText("File Repeat");
        fileRepeatRadioButton.addItemListener(this::fileRepeatItemStateChanged);
        repeatFileTextField = new JTextField();
        repeatFileTextField.setText("File Path");
        JButton repeatFileBrowseButton = new JButton();
        repeatFileBrowseButton.setText("...");
        repeatFileBrowseButton.addActionListener(this::repeatFileButtonActionPerformed);
        repeatSpinner = new JSpinner();
        repeatSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        levelsSpinner = new JSpinner();
        levelsSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        JButton okButton = new JButton();
        okButton.setText("OK");
        okButton.addActionListener(evt -> {
            try {
                okButtonActionPerformed();
            } catch (Exception ex) {
                Logger.getLogger(GenerateInputItemsDialog.class.getName()).log(Level.SEVERE, "Ex014", ex);
            }
        });
        ButtonGroup generateTypeButtonGroup = new ButtonGroup();
        generateTypeButtonGroup.add(blankDocumentRadioButton);
        generateTypeButtonGroup.add(fileRepeatRadioButton);

        CellConstraints CC = new CellConstraints();

        JPanel wrapper = new JPanel(new FormLayout(
            "$lcgap, f:p:g, $lcgap, f:p, $lcgap, 50dlu, $lcgap, 60dlu, $lcgap",
            "$lgap, f:p, $lgap, f:p, $lgap, f:p, $lgap, f:p, $lgap, f:p, $lgap"));
        wrapper.add(blankDocumentRadioButton, CC.xyw(2, 2, 7));
        wrapper.add(fileRepeatRadioButton, CC.xyw(2, 6, 7));

        blankDocumentPanel = new JPanel(new FormLayout(
            "$lcgap, 60dlu, $lcgap, 5dlu, $lcgap, 60dlu, $lcgap, 50dlu, $lcgap",
            "$lgap, f:p, 1dlu, f:p, 1dlu, f:p, $lgap"));
        blankDocumentPanel.add(widthSpinner, CC.xy(2, 2));
        blankDocumentPanel.add(new JLabel("x"), CC.xy(4, 2));
        blankDocumentPanel.add(heightSpinner, CC.xy(6, 2));
        blankDocumentPanel.add(unitsComboBox, CC.xy(8, 2));
        blankDocumentPanel.add(numberOfPagesSpinner, CC.xy(2, 4));
        blankDocumentPanel.add(new JLabel("Number of pages"), CC.xyw(4, 4, 5));
        blankDocumentPanel.add(backgroundColorChooserButton, CC.xy(2, 6));
        blankDocumentPanel.add(new JLabel("Background color"), CC.xyw(4, 6, 5));
        wrapper.add(blankDocumentPanel, CC.xyw(2, 4, 7));

        repeatFilePanel = new JPanel(new FormLayout(
            "$lcgap, 60dlu, $lcgap, 5dlu, $lcgap, 60dlu, $lcgap, 25dlu, $lcgap, 17dlu, $lcgap",
            "$lgap, f:p, 1dlu, f:p, 1dlu, f:p, $lgap"));
        repeatFilePanel.add(repeatFileTextField, CC.xyw(2, 2, 7));
        repeatFilePanel.add(repeatFileBrowseButton, CC.xy(10, 2));
        repeatFilePanel.add(repeatSpinner, CC.xy(2, 4));
        repeatFilePanel.add(new JLabel("Number of times"), CC.xyw(4, 4, 4));
        wrapper.add(repeatFilePanel, CC.xyw(2, 8, 7));

        JPanel controlsPanel = new JPanel(new FormLayout(
            "$lcgap, 60dlu, $lcgap, 5dlu, $lcgap, 60dlu, $lcgap, 50dlu, $lcgap",
            "$lgap, f:p, $lgap"));
        controlsPanel.add(levelsSpinner, CC.xy(2, 2));
        controlsPanel.add(new JLabel("Levels"), CC.xyw(4, 2, 4));
        controlsPanel.add(okButton, CC.xy(8, 2));
        wrapper.add(controlsPanel, CC.xyw(2, 10, 7));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Generate");
        add(wrapper);
        setResizable(false);
        pack();
    }

    private void okButtonActionPerformed() throws IOException {
        String parentPath;

        if (node == null) {
            parentPath = System.getProperty("java.io.tmpdir");

        } else if (node.getUserObject() instanceof FolderTreeTableRow) {
            parentPath = node.getUserObject().getKey();
        } else if (node.getUserObject() instanceof PageTreeTableRow) {
            Node folderNode = (Node) node.getParent().getParent();
            parentPath = folderNode.getUserObject().getKey();
        } else {
            Node folderNode = (Node) node.getParent();
            parentPath = folderNode.getUserObject().getKey();
        }

        Node templateNode;
        if (blankDocumentRadioButton.isSelected()) {
            templateNode = createBlankFileNode();
        } else {
            templateNode = createFromExistingFileNode();
        }

        ((VirtualFileTreeTableRow) templateNode.getUserObject()).setParent(parentPath);

        try {
            if (node == null) {
                List<Placeholder> h = new ArrayList<Placeholder>();
                h.add(new Placeholder(parentPath, 0));
                populatePlaceholders(templateNode, h);
            } else {
                int levels = Integer.parseInt(levelsSpinner.getValue().toString());
                List<Placeholder> placeholders = calculatePlaceholders(node, levels);
                populatePlaceholders(templateNode, placeholders);
            }
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(GenerateInputItemsDialog.class.getName()).log(Level.SEVERE, "Ex015", ex);
        }

        this.dispose();
    }

    private void fileRepeatItemStateChanged(java.awt.event.ItemEvent evt) {
        radioGroupStateChange();
    }

    private void jRadioButton1ItemStateChanged(java.awt.event.ItemEvent evt) {
        radioGroupStateChange();
    }

    private void repeatFileButtonActionPerformed(java.awt.event.ActionEvent evt) {
        FileChooser fileChooser = new FileChooser();
        File f = fileChooser.getSelectedFile();

        if (f != null) {
            repeatFileTextField.setText(f.getAbsolutePath());
        }
    }

    public interface Listener {

        void importNode(Node node, int index);

        void insertNodeInto(Node node, Node parent, int index);

    }

    private int getIndex(Node node) {
        return node == null ? -1 : node.getParent().getIndex(node);
    }

    private void radioGroupStateChange() {
        Arrays.stream(blankDocumentPanel.getComponents()).forEach(c -> c.setEnabled(blankDocumentRadioButton.isSelected()));
        Arrays.stream(repeatFilePanel.getComponents()).forEach(c -> c.setEnabled(!blankDocumentRadioButton.isSelected()));
    }

    private Node createBlankFileNode() throws IOException {
        String key = "BLANK_" + getSaltString();
        PDDocument document = new PDDocument();
        PDPageContentStream cos;

        int numberOfPages = numberOfPagesModel.getNumber().intValue();
        float width = getPagePostscriptValue(widthModel.getNumber().floatValue());
        float height = getPagePostscriptValue(heightModel.getNumber().floatValue());

        for (int i = 0; i < numberOfPages; i++) {
            PDPage page = new PDPage(new PDRectangle(width, height));
            document.addPage(page);

            cos = new PDPageContentStream(document, page);
            cos.setNonStrokingColor(backgroundColorChooserButton.getSelectedColor());
            cos.addRect(0, 0, width, height);
            cos.close();
        }
        File parent = new File(System.getProperty("java.io.tmpdir"));
        File temp = new File(parent, key + ".pdf");

        if (temp.exists()) {
            temp.delete();
        }

        try {
            temp.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(GenerateInputItemsDialog.class.getName()).log(Level.SEVERE, "Ex061", ex);
        }

        temp.deleteOnExit();
        String name = temp.getAbsolutePath();
        FileOutputStream out = new FileOutputStream(name);
        document.save(out);
        document.close();
        out.close();

        int color = backgroundColorChooserButton.getSelectedColor().getRGB();

        Rectangle size = new Rectangle(width, height);

        VirtualBlankNodeFactory virtualBlankNodeFactory = (VirtualBlankNodeFactory) NodeFactory
            .getFileNodeFactory(TreeTableRowType.VIRTUAL_FILE, FileTreeTableRow.SubType.BLANK);
        virtualBlankNodeFactory.setPageCount(numberOfPages);
        virtualBlankNodeFactory.setColor(color);
        virtualBlankNodeFactory.setSize(size);

        return virtualBlankNodeFactory.getFileNode(key + ".pdf");
    }

    private Node createFromExistingFileNode() {
        if (repeatFileTextField.getText() == null) {
            return null;
        }

        File srcFile = new File(repeatFileTextField.getText());

        if (!srcFile.exists()) {
            return null;
        }

        String extension = SupportedFileTypes.getFileExtension(srcFile);
        Node newNode;

        if (extension.equalsIgnoreCase("pdf")) {
            newNode = createNodeFromPDF(srcFile);
        } else {
            newNode = createNodeFromImage(srcFile);
        }

        return newNode;
    }

    private Node createNodeFromPDF(File srcFile) {
        String key = srcFile.getName() + "_" + getSaltString();
        int repeat = Integer.parseInt(repeatSpinner.getValue().toString());

        VirtualPdfNodeFactory fileNodeFactory = (VirtualPdfNodeFactory) NodeFactory
            .getFileNodeFactory(TreeTableRowType.VIRTUAL_FILE, FileTreeTableRow.SubType.PDF);

        fileNodeFactory.setSrcFile(srcFile.getAbsolutePath());
        fileNodeFactory.setRepeat(repeat);

        return fileNodeFactory.getFileNode(key);
    }

    private Node createNodeFromImage(File srcFile) {
        String key = srcFile.getName() + "_" + getSaltString();
        int repeat = Integer.parseInt(repeatSpinner.getValue().toString());

        VirtualImageNodeFactory fileNodeFactory = (VirtualImageNodeFactory) NodeFactory
            .getFileNodeFactory(TreeTableRowType.VIRTUAL_FILE, FileTreeTableRow.SubType.IMAGE);

        fileNodeFactory.setSrcFile(srcFile.getAbsolutePath());
        fileNodeFactory.setRepeat(repeat);

        return fileNodeFactory.getFileNode(key);
    }

    protected String getSaltString() {
        String SALT_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();

        while (salt.length() < 12) {
            int index = (int) (rnd.nextFloat() * SALT_CHARS.length());
            salt.append(SALT_CHARS.charAt(index));
        }

        return salt.toString();
    }

    private List<Placeholder> calculatePlaceholders(Node parent, int level) {
        List<Placeholder> placeholders = new ArrayList<>();

        if (parent.getUserObject().getType() == TreeTableRowType.PAGE) {
            parent = (Node) parent.getParent();
        }

        if (parent.isFile()) {
            FolderTreeTableRow parentUO = (FolderTreeTableRow) parent.getParent().getUserObject();
            placeholders.add(new Placeholder(parentUO.getKey(), getIndex(parent) + 1));
            return placeholders;
        }

        Enumeration<? extends MutableTreeTableNode> e = parent.children();
        while (e.hasMoreElements()) {
            Node child = (Node) e.nextElement();

            if (child.getUserObject() instanceof PageTreeTableRow) {
                continue;
            }

            FolderTreeTableRow parentUO = (FolderTreeTableRow) child.getParent().getUserObject();
            int index = getIndex(child);
            index = (2 * index) + 1;
            placeholders.add(new Placeholder(parentUO.getKey(), index));

            if (child.getUserObject() instanceof FolderTreeTableRow && level > 1) {
                placeholders.addAll(calculatePlaceholders(child, level - 1));
            }
        }

        return placeholders;
    }

    private void populatePlaceholders(Node node, List<Placeholder> placeholders) throws CloneNotSupportedException {
        try {
            Kryo kryo = new Kryo();
            List<Node> pages = (List<Node>) Collections.list(node.children());

            for (Node page : pages) {
                node.remove(page);
            }

            for (Placeholder p : placeholders) {
                Node cloneNode = kryo.copy(node);
                VirtualFileTreeTableRow vfuo = (VirtualFileTreeTableRow) cloneNode.getUserObject();
                vfuo.setParent(p.getParent());
                l.importNode(cloneNode, p.getIndex());
                for (int i = 0; i < pages.size(); i++) {
                    l.insertNodeInto(kryo.copy(pages.get(i)), cloneNode, i);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(GenerateInputItemsDialog.class.getName()).log(Level.SEVERE, "Ex016", ex);
        }
    }

    public float getPagePostscriptValue(double value) {
        switch (unitsComboBox.getSelectedIndex()) {
            case 0:
                value = UnitTranslator.POINT_POSTSCRIPT * value;
                break;
            case 1:
                value = UnitTranslator.millisToPoints(value);
                break;
        }
        value = round(value);
        return (float) value;
    }

    private double round(double value) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private static class Placeholder {

        String parent;
        int index;

        public Placeholder(String parent, int index) {
            this.parent = parent;
            this.index = index;
        }

        public String getParent() {
            return parent;
        }

        public int getIndex() {
            return index;
        }
    }
}

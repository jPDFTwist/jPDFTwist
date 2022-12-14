package jpdftwist.gui.tab.input;

import com.esotericsoftware.kryo.Kryo;
import com.itextpdf.text.Rectangle;
import jpdftwist.core.UnitTranslator;
import jpdftwist.gui.component.ColorChooserButton;
import jpdftwist.gui.component.FileChooser;
import jpdftwist.gui.component.treetable.Node;
import jpdftwist.gui.component.treetable.TreeTableRowType;
import jpdftwist.gui.component.treetable.row.FileTreeTableRow;
import jpdftwist.gui.component.treetable.row.FolderTreeTableRow;
import jpdftwist.gui.component.treetable.row.PageTreeTableRow;
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
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vasilis Naskos
 * TODO: Extract logic to actions
 */
public class GenerateInputItemsDialog extends JFrame {

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

        ButtonGroup buttonGroup1 = new ButtonGroup();
        JButton jButton1 = new JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        JRadioButton jRadioButton2 = new JRadioButton();
        JLabel jLabel1 = new JLabel();
        JLabel jLabel2 = new JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jTextField1 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jSpinner2 = new javax.swing.JSpinner();
        jSpinner3 = new javax.swing.JSpinner();
        jComboBox1 = new javax.swing.JComboBox();
        JLabel jLabel3 = new JLabel();
        colorChooserButton1 = new ColorChooserButton();
        JLabel jLabel4 = new JLabel();
        jSpinner4 = new javax.swing.JSpinner();
        JLabel jLabel5 = new JLabel();
        levelsSpinner = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Generate");

        jButton1.setText("OK");
        jButton1.addActionListener(evt -> {
            try {
                jButton1ActionPerformed(evt);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Blank Document");
        jRadioButton1.addItemListener(this::jRadioButton1ItemStateChanged);

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("File Repeat");
        jRadioButton2.addItemListener(this::jRadioButton2ItemStateChanged);

        jLabel1.setText("No. of Pages");

        jLabel2.setText("Background Color (Default White)");

        jSpinner1.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        jTextField1.setText("File Path");

        jButton3.setText("...");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jSpinner2.setModel(new SpinnerNumberModel(8F, 1F, null, 1F));

        jSpinner3.setModel(new SpinnerNumberModel(11F, 1F, null, 1F));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"inches", "mm", "points"}));

        jLabel3.setText("x");

        colorChooserButton1.setSelectedColor(new java.awt.Color(254, 254, 254));

        jLabel4.setText("No. of Times");

        jSpinner4.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        jLabel5.setText("Levels:");

        levelsSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(jRadioButton1)
                        .addComponent(jRadioButton2)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(12)
                            .addGroup(layout.createParallelGroup(Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jSpinner2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(jLabel3)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(jSpinner3, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(jComboBox1, GroupLayout.PREFERRED_SIZE, 108, GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel1)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(jSpinner1, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(colorChooserButton1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(jLabel2))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 367, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(jButton3, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(jSpinner4, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)))))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap(219, Short.MAX_VALUE)
                    .addComponent(jLabel5)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(levelsSpinner, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jRadioButton1)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jSpinner2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jSpinner3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jSpinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addGap(5))
                        .addComponent(colorChooserButton1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(jRadioButton2)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton3))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                                    .addComponent(jLabel4)
                                    .addComponent(jSpinner4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton1)
                                .addContainerGap())
                            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(levelsSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addGap(13)))
                        .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addGap(15))))
        );
        layout.linkSize(SwingConstants.HORIZONTAL, jSpinner2, jSpinner3);
        getContentPane().setLayout(layout);

        pack();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) throws IOException {
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
        if (jRadioButton1.isSelected()) {
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
                List<Placeholder> pholders = calculatePlaceholders(node, levels);
                populatePlaceholders(templateNode, pholders);
            }
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(GenerateInputItemsDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.dispose();
    }

    private void jRadioButton2ItemStateChanged(java.awt.event.ItemEvent evt) {
        radioGroupStateChange();
    }

    private void jRadioButton1ItemStateChanged(java.awt.event.ItemEvent evt) {
        radioGroupStateChange();
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        FileChooser fileChooser = new FileChooser();
        File f = fileChooser.getSelectedFile();

        if (f != null) {
            jTextField1.setText(f.getAbsolutePath());
        }
    }

    private ColorChooserButton colorChooserButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JSpinner jSpinner2;
    private javax.swing.JSpinner jSpinner3;
    private javax.swing.JSpinner jSpinner4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JSpinner levelsSpinner;

    public interface Listener {

        void importNode(Node node, int index);

        void insertNodeInto(Node node, Node parent, int index);

    }

    private int getIndex(Node node) {
        return node == null ? -1 : node.getParent().getIndex(node);
    }

    private void radioGroupStateChange() {
        jTextField1.setEnabled(!jRadioButton1.isSelected());
        jButton3.setEnabled(!jRadioButton1.isSelected());
        jSpinner4.setEnabled(!jRadioButton1.isSelected());

        jSpinner1.setEnabled(jRadioButton1.isSelected());
        jSpinner2.setEnabled(jRadioButton1.isSelected());
        jSpinner3.setEnabled(jRadioButton1.isSelected());
        jComboBox1.setEnabled(jRadioButton1.isSelected());
        colorChooserButton1.setEnabled(jRadioButton1.isSelected());
    }

    private Node createBlankFileNode() throws IOException {
        String key = "blank_" + getSaltString();
        PDDocument document = new PDDocument();
        PDPageContentStream cos;
        Float width = null;
        Float height = null;
        int numberOfPages = Integer.parseInt(jSpinner1.getValue().toString());
        for (int i = 0; i < numberOfPages; i++) {
            width = Float.parseFloat(jSpinner2.getValue().toString());
            height = Float.parseFloat(jSpinner3.getValue().toString());

            width = getPagePostscriptValue(width);
            height = getPagePostscriptValue(height);

            PDPage page = new PDPage(new PDRectangle(width, height));
            document.addPage(page);

            cos = new PDPageContentStream(document, page);
            cos.setNonStrokingColor(colorChooserButton1.getSelectedColor());
            cos.addRect(0, 0, width, height);
            cos.close();
        }
        File parent = new File(System.getProperty("java.io.tmpdir"));
        String fname = key + ".pdf";
        File temp = new File(parent, key + ".pdf");

        if (temp.exists()) {
            temp.delete();
        }

        try {
            temp.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        temp.deleteOnExit();
        String name = temp.getAbsolutePath();
        FileOutputStream out = new FileOutputStream(name);
        document.save(out);
        document.close();
        out.close();

        int color = colorChooserButton1.getSelectedColor().getRGB();

        double width1 = Double.parseDouble(jSpinner2.getValue().toString());
        double height1 = Double.parseDouble(jSpinner3.getValue().toString());

        Rectangle size = new Rectangle(getPagePostscriptValue(width1), getPagePostscriptValue(height1));

        VirtualBlankNodeFactory virtualBlankNodeFactory = (VirtualBlankNodeFactory) NodeFactory
            .getFileNodeFactory(TreeTableRowType.VIRTUAL_FILE, FileTreeTableRow.SubType.BLANK);
        virtualBlankNodeFactory.setPageCount(numberOfPages);
        virtualBlankNodeFactory.setColor(color);
        virtualBlankNodeFactory.setSize(size);

        return virtualBlankNodeFactory.getFileNode(key + ".pdf");
    }

    private Node createFromExistingFileNode() {
        if (jTextField1.getText() == null) {
            return null;
        }

        File srcFile = new File(jTextField1.getText());

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
        int repeat = Integer.parseInt(jSpinner4.getValue().toString());

        VirtualPdfNodeFactory fileNodeFactory = (VirtualPdfNodeFactory) NodeFactory
            .getFileNodeFactory(TreeTableRowType.VIRTUAL_FILE, FileTreeTableRow.SubType.PDF);

        fileNodeFactory.setSrcFile(srcFile.getAbsolutePath());
        fileNodeFactory.setRepeat(repeat);

        return fileNodeFactory.getFileNode(key);
    }

    private Node createNodeFromImage(File srcFile) {
        String key = srcFile.getName() + "_" + getSaltString();
        int repeat = Integer.parseInt(jSpinner4.getValue().toString());

        VirtualImageNodeFactory fileNodeFactory = (VirtualImageNodeFactory) NodeFactory
            .getFileNodeFactory(TreeTableRowType.VIRTUAL_FILE, FileTreeTableRow.SubType.IMAGE);

        fileNodeFactory.setSrcFile(srcFile.getAbsolutePath());
        fileNodeFactory.setRepeat(repeat);

        return fileNodeFactory.getFileNode(key);
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();

        while (salt.length() < 11) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }

        String saltStr = salt.toString();
        return saltStr;
    }

    private List<Placeholder> calculatePlaceholders(Node parent, int level) {
        List<Placeholder> placeholders = new ArrayList<Placeholder>();

        if (parent.getUserObject().getType() == TreeTableRowType.PAGE) {
            parent = (Node) parent.getParent();
        }

        if (TreeTableRowType.isFile(parent)) {
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
    }

    public float getPagePostscriptValue(double value) {
        switch (jComboBox1.getSelectedIndex()) {
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

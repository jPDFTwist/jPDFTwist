package jpdftwist.tabs.input.treetable;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import jpdftwist.gui.Preview;
import jpdftwist.gui.component.treetable.Node;
import jpdftwist.gui.component.treetable.TreeTableComponent;
import jpdftwist.gui.component.treetable.TreeTableRowType;
import jpdftwist.gui.component.treetable.event.ControlListener;
import jpdftwist.gui.component.treetable.row.FileTreeTableRow;
import jpdftwist.gui.component.treetable.row.PageTreeTableRow;
import jpdftwist.tabs.input.InputTabFileImporter;
import jpdftwist.utils.JImageParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputTabControlListener implements ControlListener {

    private final Preview previewPanel;
    private final InputTabFileImporter inputTabFileImporter;

    public InputTabControlListener(Preview previewPanel, InputTabFileImporter inputTabFileImporter) {
        this.previewPanel = previewPanel;
        this.inputTabFileImporter = inputTabFileImporter;
    }

    @Override
    public void doubleClick(Node selectedNode) {
        if (selectedNode.getUserObject() instanceof FileTreeTableRow) {
            try {
                File newFile = new File(selectedNode.getUserObject().getKey());
                if (newFile.exists()) {
                    Desktop.getDesktop().open(newFile);
                }

            } catch (IOException ex) {
                Logger.getLogger(TreeTableComponent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void onSelectNode(Node selectedNode) {
        try {
            updatePreview(selectedNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClear() {
        previewPanel.clearPreview();
    }

    @Override
    public void beforeDelete() {
        previewPanel.clearPreview();
    }

    @Override
    public void onExport(TreeTableComponent treeTable) {
        createPdf(treeTable);
    }

    @Override
    public void onSave(TreeTableComponent treeTable) {
        createJSON(treeTable);
    }

    @Override
    public void onLoad(TreeTableComponent treeTable) {
        loadJSON(treeTable);
    }

    private void updatePreview(Node node) throws IOException {
        if (node == null || !(node.getUserObject() instanceof PageTreeTableRow)) {
            previewPanel.clearPreview();
            return;
        }

        final FileTreeTableRow userObject = (FileTreeTableRow) node.getParent().getUserObject();
        String parent = userObject.getKey();

        final Image previewImage;
        if (userObject.getSubType().equals(FileTreeTableRow.SubType.PDF)) {
            PDDocument document = PDDocument.load(new File(parent));
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            previewImage = pdfRenderer.renderImage(Integer.parseInt(node.getUserObject().getKey()) - 1);
            document.close();
        } else {
            previewImage = JImageParser.readAwtImage(parent);
        }

        previewPanel.preview(previewImage);
    }

    private void createPdf(TreeTableComponent treeTable) {
        try {
            //Create Tree Report
            final JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showSaveDialog(treeTable);

            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }

            Document document = new Document(PageSize.A1, 0, 0, 0, 0);

            LocalDateTime now = LocalDateTime.now();
            String date = now.getDayOfMonth() + "-" + now.getMonth() + "-" + now.getYear();
            String time = now.getHour() + "-" + now.getMinute() + "-" + now.getSecond();
            String timestamp = date + "-" + time;

            PdfWriter writer;

            String filename2 = "JPDFTwist-Tree-Report-" + timestamp + ".pdf";
            final String filepath = fc.getSelectedFile().getAbsolutePath() + File.separator + filename2;
            writer = PdfWriter.getInstance(document, Files.newOutputStream(Paths.get(filepath)));

            document.open();

            BufferedImage img2 = (BufferedImage) getImageFromComponent(treeTable);
            addImageToDocument(document, writer, img2);

            document.close();
            JOptionPane.showMessageDialog(null, "File successfully saved at \"" + filepath + "\"");

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private java.awt.Image getImageFromComponent(JComponent component) {
        BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(),
            BufferedImage.TYPE_INT_RGB);
        component.paint(image.getGraphics());
        return image;
    }

    private void addImageToDocument(Document document, PdfWriter writer, java.awt.Image img)
        throws IOException, DocumentException {
        com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance(writer, img, 1);

        PdfContentByte content = writer.getDirectContent();
        image.scaleAbsolute(PageSize.A1.getWidth(), PageSize.A1.getHeight());
        image.setAbsolutePosition(0, 0);

        float width = PageSize.A1.getWidth();
        float heightRatio = image.getHeight() * width / image.getWidth();
        int nPages = (int) (heightRatio / PageSize.A1.getHeight());
        float difference = heightRatio % PageSize.A1.getHeight();

        while (nPages >= 0) {
            document.newPage();
            content.addImage(image, width, 0, 0, heightRatio, 0, -((--nPages * PageSize.A1.getHeight()) + difference));
        }
    }

    private void createJSON(TreeTableComponent treeTable) {
        try {
            //Create Tree Input Structure

            final JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fc.showSaveDialog(treeTable);

            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }
            final org.jdesktop.swingx.treetable.TreeTableModel model = treeTable.getModel();
            final JSONArray inputFiles = new JSONArray(getInputElements((Node) model.getRoot()));
            final JSONObject json = new JSONObject();
            json.put("input-files", inputFiles);

            LocalDateTime now = LocalDateTime.now();
            String timestamp = String.format("%s-%s-%s-%s-%s-%s",
                now.getDayOfMonth(), now.getMonth(), now.getYear(), now.getHour(), now.getMinute(), now.getSecond());

            String filename1 = "JPDFTwist-Tree-Input-" + timestamp + ".json";
            final String filepath = fc.getSelectedFile().getAbsolutePath() + File.separator + filename1;
            FileWriter jsonFile = new FileWriter(filepath);

            jsonFile.write(json.toString(4));
            jsonFile.close();
            JOptionPane.showMessageDialog(null, "File successfully saved at \"" + filepath + "\"");

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private java.util.List<String> getInputElements(Node parent) {
        final List<String> inputElements = new ArrayList<>();

        Enumeration<?> e = parent.children();
        while (e.hasMoreElements()) {
            final Node child = (Node) e.nextElement();
            if (TreeTableRowType.isFile(child)) {
                inputElements.add(child.getUserObject().getKey());
            } else if (child.getUserObject().getType() == TreeTableRowType.FOLDER) {
                inputElements.addAll(getInputElements(child));
            }
        }

        return inputElements;
    }

    private void loadJSON(TreeTableComponent treeTable) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fc.showOpenDialog(treeTable);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
                JSONObject json = new JSONObject(content);
                JSONArray inputFiles = json.getJSONArray("input-files");
                String[] files = new String[inputFiles.length()];
                for (int i = 0; i < inputFiles.length(); i++) {
                    files[i] = inputFiles.getString(i);
                }
                inputTabFileImporter.importFilesToInputTab(files);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }
}

package jpdftwist.gui.tab;

import com.itextpdf.text.DocumentException;
import jpdftwist.core.OutputEventListener;
import jpdftwist.core.PDFTwist;
import jpdftwist.gui.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class AttachmentTab extends Tab {

    private final JList<File> list;
    private final MainWindow mainWindow;
    private final DefaultListModel<File> model;

    public AttachmentTab(MainWindow mf) {
        super(new BorderLayout());
        mainWindow = mf;
        JButton add;
        add(add = new JButton("Add attachment..."), BorderLayout.NORTH);
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser f = new JFileChooser();
                if (f.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
                    model.addElement(f.getSelectedFile());
                }
            }
        });
        add(list = new JList<>(model = new DefaultListModel<>()), BorderLayout.CENTER);
        list.setBackground(new Color(230, 230, 250));
        JButton remove;
        add(remove = new JButton("Remove"), BorderLayout.SOUTH);
        remove.addActionListener(e -> {
            int[] selectedIndices = list.getSelectedIndices();
            for (int i = selectedIndices.length - 1; i >= 0; i--) {
                model.remove(selectedIndices[i]);
            }
        });
    }

    public String getTabName() {
        return "Attachments";
    }

    public PDFTwist run(PDFTwist pdfTwist, OutputEventListener outputEventListener) throws IOException, DocumentException {
        outputEventListener.updateJPDFTwistProgress(getTabName());
        outputEventListener.setAction("Adding attachments");
        outputEventListener.resetProcessedPages();
        for (int i = 0; i < model.getSize(); i++) {
            File f = model.get(i);
            pdfTwist.addFile(f);
        }
        return pdfTwist;
    }
}

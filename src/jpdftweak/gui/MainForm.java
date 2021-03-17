package jpdftweak.gui;

import com.esotericsoftware.kryo.Kryo;
import com.itextpdf.text.DocumentException;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import javax.swing.filechooser.FileNameExtensionFilter;
import jpdftweak.Main;
import jpdftweak.core.PageRange;
import jpdftweak.core.PdfTweak;
import jpdftweak.gui.dialogs.OutputProgressDialog;
import jpdftweak.tabs.AttachmentTab;
import jpdftweak.tabs.BookmarkTab;
import jpdftweak.tabs.DocumentInfoTab;
import jpdftweak.tabs.EncryptSignTab;
import jpdftweak.tabs.InteractionTab;
import jpdftweak.tabs.OutputTab;
import jpdftweak.tabs.PageSizeTab;
import jpdftweak.tabs.ShuffleTab;
import jpdftweak.tabs.Tab;
import jpdftweak.tabs.WatermarkTab;
import jpdftweak.tabs.InputTab;
import jpdftweak.tabs.WatermarkPlusTab;
import jpdftweak.tabs.input.pagerange.BatchPageRangeGenerator;
import jpdftweak.tabs.input.preview.PreviewHandler;
import jpdftweak.tabs.input.treetable.UserObjectValue;
import jpdftweak.tabs.input.treetable.node.Node;
import jpdftweak.tabs.input.treetable.node.factory.VirtualPdfNodeFactory;
import jpdftweak.tabs.input.treetable.node.userobject.FolderUserObject;
import jpdftweak.tabs.input.treetable.node.userobject.UserObjectType;
//import org.apache.pdfbox.exceptions.COSVisitorException;

public class MainForm extends JFrame {

    private static final long serialVersionUID = 5541931608656450178L;

    private final InputTab inputTab;
    private final OutputTab outputTab;
    private final WatermarkPlusTab watermarkPlusTab;
    
    
    private Tab[] tabs = {
        //inputTab,
        new PageSizeTab(this),
        new WatermarkTab(this),
        new ShuffleTab(this),
        //new PageNumberTab(this),
        new BookmarkTab(this),
        new AttachmentTab(this),
        new InteractionTab(this),
        new DocumentInfoTab(this),
        new EncryptSignTab(this)};

    public MainForm() {
        super("jPDF Tweak " + Main.VERSION);
        setIconImage(Toolkit.getDefaultToolkit().createImage(MainForm.class.getResource("/icon.png")));
        
        UserObjectValue.initMap();
        
        setLayout(new FormLayout("f:p:g,f:p,f:p", "f:p:g,f:p"));
        CellConstraints cc = new CellConstraints();
        JTabbedPane jtp;
        add(jtp = new JTabbedPane(), cc.xyw(1, 1, 3));
        
        PreviewHandler previewHandler = new PreviewHandler() {

            @Override
            public void runPreview(Node node) {
                try {
                    virtualRun(node);
                } catch (IOException ex) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        
        inputTab = new InputTab();
        jtp.addTab(inputTab.getTabName(), inputTab.getUserInterface());
        inputTab.setPreviewHandler(previewHandler);
        watermarkPlusTab = new WatermarkPlusTab();
        for (Tab tab : tabs) {
            jtp.addTab(tab.getTabName(), tab);
            if(tab instanceof WatermarkTab) {
                jtp.addTab(watermarkPlusTab.getTabName(), watermarkPlusTab.getUserInterface());
            }
        }
      
        outputTab = new OutputTab(this);
        jtp.addTab(outputTab.getTabName(), outputTab);
        
        JButton run;
        add(run = new JButton("Run"), cc.xy(2, 2));
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        runTweaks();
                    }
                }).start();
            }
        });
        JButton quit;
        add(quit = new JButton("Quit"), cc.xy(3, 2));
        quit.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        pack();
        getRootPane().setDefaultButton(run);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    protected void runTweaks() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        PdfTweak tweak = null;
        int batchLength = inputTab.getBatchLength();
        OutputProgressDialog outputProgress = null;
        try {
            try {
                inputTab.checkRun();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            for (Tab tab : tabs) {
                tab.checkRun();
            }
            outputProgress = new OutputProgressDialog();
            outputProgress.setFileCount(batchLength);
            outputProgress.setVisible(rootPaneCheckingEnabled);
            for (int task = 0; task < batchLength; task++) {
                if (!outputProgress.isVisible()) {
                    break;
                }
                outputProgress.resetTweaksValue();
                inputTab.selectBatchTask(task);
                tweak = inputTab.run(tweak);
                for (Tab tab : tabs) {
                    if (!outputProgress.isVisible()) {
                        break;
                    }
                    if(tab instanceof WatermarkTab) {
                        tweak = watermarkPlusTab.run(tweak);
                    }
                    tweak = tab.run(tweak, outputProgress);
                }
                tweak = outputTab.run(tweak, outputProgress);
                outputProgress.updateOverallProgress();
            }

            if (outputProgress.isVisible()) {
                outputProgress.dispose();
                JOptionPane.showMessageDialog(this, "Finished", "JPDFTweak", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (DocumentException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "jPDF Tweak has run out of memory. You may configure Java so that it may use more RAM, or you can enable the Tempfile option on the output tab.", "Out of memory: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        } finally {
            if (outputProgress != null && outputProgress.isVisible()) {
                outputProgress.dispose();
            }
            this.setCursor(null);
            
            if (tweak != null) {
                tweak.cleanup();
            }
        }
    }

    public InputTab getInputTab() {
        return inputTab;
    }
    
    private void virtualRun(Node node) throws IOException {
        /*if(node == null
                || !(UserObjectType.isFile(node.getUserObject())
                    || node.getUserObject().getType() == UserObjectType.PAGE))
            return;
        
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        PdfTweak tweak = null;
        try {
            for (Tab tab : tabs) {
                tab.checkRun();
            }
            OutputProgressDialog outputProgress = new OutputProgressDialog();
            outputProgress.setFileCount(1);
            outputProgress.setVisible(rootPaneCheckingEnabled);

            outputProgress.resetTweaksValue();
            
            Node folderNode = new Node(new FolderUserObject("/parent"));
            
            if(node.getUserObject().getType() == UserObjectType.PAGE) {
                Node fileNode = new VirtualPdfNodeFactory()
                        .getEmptyFileNode("preview.pdf");
                
                Kryo kryo = new Kryo();
                Node page = kryo.copy(node);
                
                fileNode.insert(page, 0);
                folderNode.insert(fileNode, 0);
            } else if(UserObjectType.isFile(node.getUserObject())) {
                Node fileNode = node;
                folderNode.insert(fileNode, 0);
            } else {
                return;
            }
            
            List<PageRange> ranges = new BatchPageRangeGenerator(folderNode).generate(0);
            
            File tempOutput = File.createTempFile("jpdf_preview", ".pdf");
            tempOutput.deleteOnExit();
            tweak = new PdfTweak(ranges, false, false, 0);

            for (Tab tab : tabs) {
                if (!outputProgress.isVisible()) {
                    break;
                }
                tweak = tab.run(tweak, outputProgress);
            }
            
            tweak.writeOutput(tempOutput.getPath(),
                    false, false, false,
                    false, false, outputProgress);
            
            Desktop.getDesktop().open(tempOutput);
             
            outputProgress.updateOverallProgress();

            if (outputProgress.isVisible()) {
                outputProgress.dispose();
            }
        } catch (DocumentException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "jPDF Tweak has run out of memory. You may configure Java so that it may use more RAM, or you can enable the Tempfile option on the output tab.", "Out of memory: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        } finally {
            this.setCursor(null);
            if (tweak != null) {
                tweak.cleanup();
            }
        }*/
   }
}

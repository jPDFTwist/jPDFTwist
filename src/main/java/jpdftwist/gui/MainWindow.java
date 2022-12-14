package jpdftwist.gui;

import com.itextpdf.text.DocumentException;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import jpdftwist.Main;
import jpdftwist.core.PDFTwist;
import jpdftwist.gui.dialog.OutputProgressDialog;
import jpdftwist.gui.tab.AttachmentTab;
import jpdftwist.gui.tab.BookmarkTab;
import jpdftwist.gui.tab.DocumentInfoTab;
import jpdftwist.gui.tab.EncryptSignTab;
import jpdftwist.gui.tab.InteractionTab;
import jpdftwist.gui.tab.OutputTab;
import jpdftwist.gui.tab.ShuffleTab;
import jpdftwist.gui.tab.WatermarkPlusTab;
import jpdftwist.gui.tab.WatermarkTab;
import jpdftwist.tabs.Forms;
import jpdftwist.tabs.InputTabActions;
import jpdftwist.tabs.PageSizeTab;
import jpdftwist.tabs.Tab;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class MainWindow extends JFrame {

    private InputTabActions inputTabActions;
    private OutputTab outputTab;
    private WatermarkPlusTab watermarkPlusTab;

    private final Tab[] tabs = {
        new PageSizeTab(this), new WatermarkTab(this), new ShuffleTab(this),
        new BookmarkTab(this), new Forms(this), new AttachmentTab(this), new InteractionTab(this),
        new DocumentInfoTab(this), new EncryptSignTab(this),};

    public MainWindow() {
        super("JPDFTwist " + Main.VERSION);
        initGUI();
    }

    private void initGUI() {
        setIconImage(Toolkit.getDefaultToolkit().createImage(MainWindow.class.getResource("/icon.png")));

        getContentPane().setLayout(new FormLayout(new ColumnSpec[]{
            ColumnSpec.decode("pref:grow"),
            FormSpecs.PREF_COLSPEC,
            FormSpecs.PREF_COLSPEC,},
            new RowSpec[]{
                RowSpec.decode("fill:pref:grow"),
                RowSpec.decode("fill:pref"),
                RowSpec.decode("16px"),}));
        CellConstraints CC = new CellConstraints();
        JTabbedPane jtp;
        getContentPane().add(jtp = new JTabbedPane(), CC.xyw(1, 1, 3));
        inputTabActions = new InputTabActions();
        jtp.addTab(inputTabActions.getTabName(), inputTabActions.getUserInterface());

        watermarkPlusTab = new WatermarkPlusTab();
        for (Tab tab : tabs) {
            jtp.addTab(tab.getTabName(), tab);
            if (tab instanceof WatermarkTab) {
                jtp.addTab(watermarkPlusTab.getTabName(), watermarkPlusTab.getUserInterface());
            }
        }

        outputTab = new OutputTab(this);
        jtp.addTab(outputTab.getTabName(), outputTab);

        JButton run;
        getContentPane().add(run = new JButton("Run"), CC.xy(2, 2));
        run.addActionListener(e -> new Thread(this::runTwist).start());
        JButton quit;
        getContentPane().add(quit = new JButton("Quit"), CC.xy(3, 2));
        quit.addActionListener(e -> dispose());

        JProgressBar heapMemoryProgressBar = new JProgressBar();
        heapMemoryProgressBar.setStringPainted(true);
        heapMemoryProgressBar.setToolTipText("Commit Size / Max Heap Size");
        heapMemoryProgressBar.setForeground(new Color(95, 158, 160));

        new Timer(10, e1 -> {
            MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapMemoryUsage = memBean.getHeapMemoryUsage();

            float maxHeap = heapMemoryUsage.getMax() / 1048576.f;
            float committedHeap = heapMemoryUsage.getCommitted() / 1048576.f;

            heapMemoryProgressBar.setMaximum((int) maxHeap);
            heapMemoryProgressBar.setValue((int) committedHeap);
            heapMemoryProgressBar.setString("Commit Size = " + committedHeap + " MB / " + maxHeap + " MB");
        }).start();
        getContentPane().add(heapMemoryProgressBar, "1, 3, 3, 1, fill, center");
        pack();
        getRootPane().setDefaultButton(run);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    protected void runTwist() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        PDFTwist pdfTwist = null;
        int batchLength = inputTabActions.getBatchLength();
        OutputProgressDialog outputProgress = null;
        try {
            try {
                inputTabActions.checkRun();
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
                outputProgress.resetTwistValue();
                inputTabActions.selectBatchTask(task);
                pdfTwist = inputTabActions.run(pdfTwist);
                for (Tab tab : tabs) {
                    if (!outputProgress.isVisible()) {
                        break;
                    }
                    if (tab instanceof WatermarkTab) {
                        pdfTwist = watermarkPlusTab.run(pdfTwist);
                    }
                    pdfTwist = tab.run(pdfTwist, outputProgress);
                }
                pdfTwist = outputTab.run(pdfTwist, outputProgress);
                outputProgress.updateOverallProgress();
            }

            if (outputProgress.isVisible()) {
                outputProgress.dispose();
                JOptionPane.showMessageDialog(this, "Finished", "JPDFTwist", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (OutOfMemoryError ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "JPDFTwist has run out of memory. You may configure Java so that it may use more RAM, or you can enable the Tempfile option on the output tab.",
                "Out of memory: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        } finally {
            if (outputProgress != null && outputProgress.isVisible()) {
                outputProgress.dispose();
            }
            this.setCursor(null);

            if (pdfTwist != null) {
                pdfTwist.cleanup();
            }
        }
    }

    public InputTabActions getInputTab() {
        return inputTabActions;
    }

}

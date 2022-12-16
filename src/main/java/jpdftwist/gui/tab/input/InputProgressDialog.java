package jpdftwist.gui.tab.input;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import jpdftwist.core.Utils;
import jpdftwist.tabs.input.ProcessCancelledListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vasilis Naskos
 */
public class InputProgressDialog extends JFrame {

    private JProgressBar overallProgress, subFolderProgress, fileProgress, pageProgress;
    private JButton cancelButton;
    private JLabel currentFile, waitIcon;
    private JScrollPane tableScroll;
    private DefaultTableModel model;
    private int currentFolderIndex;
    private int filesCount;
    private int foldersCount;
    private int[] filesInFoldersCount;

    private int pageNum, pageCount;

    private ProcessCancelledListener cancelListener;

    private Image waitImage;

    public InputProgressDialog() {
        initComponents();
        buildGui();
        setupFrame();
    }

    private void initComponents() {
        overallProgress = new JProgressBar();
        overallProgress.setStringPainted(true);

        subFolderProgress = new JProgressBar();
        subFolderProgress.setStringPainted(true);

        fileProgress = new JProgressBar();
        fileProgress.setStringPainted(true);

        pageProgress = new JProgressBar();
        pageProgress.setStringPainted(true);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            if (cancelListener != null) {
                cancelListener.cancelled();
            }
            closeDialogWithDelay();
        });

        currentFile = new JLabel();
        currentFile.setAlignmentX(Component.RIGHT_ALIGNMENT);

        waitImage = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/Gears-3.gif"));
        ImageIcon waitImageIcon = new ImageIcon(waitImage);
        waitIcon = new JLabel(waitImageIcon);

        model = new DefaultTableModel(new Object[]{"Processing", "Size", "Modified On"}, 0);
        JTable moreInfoTable = new JTable(model);
        moreInfoTable.getColumnModel().getColumn(0).setPreferredWidth(140);
        moreInfoTable.getColumnModel().getColumn(1).setPreferredWidth(15);
        moreInfoTable.getColumnModel().getColumn(2).setPreferredWidth(25);
        tableScroll = new JScrollPane(moreInfoTable);
        tableScroll.setPreferredSize(new Dimension(200, 200));
    }

    private void buildGui() {
        FormLayout layout = new FormLayout("right:p, 7dlu, p:g, f:p:g, f:p, 4dlu, f:p",
            "f:p, 2dlu, 5dlu, f:p, 4dlu, f:p, 4dlu, f:p, 4dlu, f:p, 7dlu, 4dlu, f:p, 4dlu, f:p, 4dlu, f:p:g");

        PanelBuilder builder = new PanelBuilder(layout);
        builder.border(Borders.DIALOG);

        CellConstraints CC = new CellConstraints();

        builder.addSeparator("Progress", CC.xyw(1, 1, 7));
        builder.addLabel("Overall:", CC.xy(1, 4));
        builder.add(overallProgress, CC.xyw(3, 4, 3));
        builder.addLabel("Folder(s):", CC.xy(1, 6));
        builder.add(subFolderProgress, CC.xyw(3, 6, 3));
        builder.addLabel("File(s):", CC.xy(1, 8));
        builder.add(fileProgress, CC.xyw(3, 8, 3));
        builder.addLabel("Page(s):", CC.xy(1, 10));
        builder.add(pageProgress, CC.xyw(3, 10, 3));
        builder.add(waitIcon, CC.xywh(7, 3, 1, 9));

        builder.addSeparator("Currently Processing", CC.xyw(1, 13, 7));
        builder.add(currentFile, CC.xyw(1, 15, 5));
        builder.add(cancelButton, CC.xy(7, 15));

        builder.add(tableScroll, CC.xyw(1, 17, 7));

        this.add(builder.getPanel());
    }

    private void setupFrame() {
        this.setMinimumSize(new Dimension(460, 260));
        this.setResizable(false);
        this.setTitle("Input Progress");
        this.setUndecorated(false);
        this.setResizable(true);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    public void setFileCount(int filesCount) {
        this.filesCount = filesCount;
        overallProgress.setMaximum(filesCount);
    }

    public void setFoldersCount(int foldersCount) {
        this.foldersCount = foldersCount;
        subFolderProgress.setMaximum(foldersCount);
    }

    public void setFilesInFolderCount(int[] filesInFoldersCount) {
        this.filesInFoldersCount = filesInFoldersCount;
        currentFolderIndex = 0;
        fileProgress.setMaximum(filesInFoldersCount[currentFolderIndex]);
    }

    public void updateProgress() {
        updateFileProgress();

        int folderProcessed = fileProgress.getValue();

        if (folderProcessed == filesInFoldersCount[currentFolderIndex])
            updateFolderProgress();

        updateOverallProgress();

    }

    private void updateOverallProgress() {
        int overallProgressValue = overallProgress.getValue();
        overallProgressValue++;

        int percentage = overallProgressValue * 100 / filesCount;

        overallProgress.setValue(overallProgressValue);
        overallProgress.setString(percentage + "%");
    }

    private void updateFolderProgress() {
        int folderProgressValue = subFolderProgress.getValue();
        folderProgressValue++;

        int percentage = folderProgressValue * 100 / foldersCount;

        subFolderProgress.setValue(folderProgressValue);
        subFolderProgress.setString(percentage + "%");

        if (currentFolderIndex != foldersCount - 1)
            resetFileProgress();
    }

    private void resetFileProgress() {
        fileProgress.setValue(0);
        fileProgress.setString("0%");

        currentFolderIndex++;
        fileProgress.setMaximum(filesInFoldersCount[currentFolderIndex]);
    }

    private void updateFileProgress() {
        int fileProgressValue = fileProgress.getValue();
        fileProgressValue++;

        int percentage = fileProgressValue * 100 / filesInFoldersCount[currentFolderIndex];

        fileProgress.setValue(fileProgressValue);
        fileProgress.setString(percentage + "%");
    }

    public void updateCurrentFile(String filepath) {
        File f = new File(filepath);

        String name = f.getName();

        long fileSize = f.length();
        String size = Utils.readableFileSize(fileSize);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String modified = sdf.format(f.lastModified());

        currentFile.setText(name);
        model.insertRow(0, new Object[]{name, size, modified});
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
        this.pageNum = 0;
        pageProgress.setMaximum(pageCount);
    }

    public synchronized void updatePageProgress() {
        SwingUtilities.invokeLater(() -> {
            pageNum++;
            pageProgress.setValue(pageNum);
            pageProgress.setString(pageNum + "/" + pageCount);
        });
    }

    public void closeDialogWithDelay() {
        try {
            Thread.sleep(500);
            clean();
        } catch (InterruptedException ex) {
            Logger.getLogger(InputProgressDialog.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.dispose();
        }
    }

    public void setCancelledListener(ProcessCancelledListener l) {
        this.cancelListener = l;
    }

    private void clean() {
        if (waitImage != null) {
            waitImage.flush();
            waitImage = null;
        }
        removeAll();
        validate();
        repaint();
    }
}

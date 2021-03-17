package jpdftweak.tabs.input;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import java.awt.Color;
import java.awt.Component;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import jpdftweak.core.Utils;

/**
 *
 * @author Vasilis Naskos
 */
public class InputProgressDialog extends JFrame {
    
    private JProgressBar overallProgress, subFolderProgress, fileProgress, pageProgress;
    private JButton cancelButton;
    private JLabel currentFile, waitIcon;
    private JScrollPane tableScroll;
    private JTable moreInfoTable;
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
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(cancelListener != null) {
                    cancelListener.cancelled();
                }
                closeDialogWithDelay();
            }
        });
        
        currentFile = new JLabel();
        currentFile.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        waitImage = Toolkit.getDefaultToolkit()
                .createImage(getClass().getResource("/Gears-3.gif"));
        ImageIcon waitImageIcon = new ImageIcon(waitImage);
        waitIcon = new JLabel(waitImageIcon);
        
        model = new DefaultTableModel(new Object[]{"Processing", "Size", "Modified On"}, 0);
        moreInfoTable = new JTable(model);
        moreInfoTable.getColumnModel().getColumn(0).setPreferredWidth(140);
        moreInfoTable.getColumnModel().getColumn(1).setPreferredWidth(15);
        moreInfoTable.getColumnModel().getColumn(2).setPreferredWidth(25);
        tableScroll = new JScrollPane(moreInfoTable);
        tableScroll.setPreferredSize(new Dimension(200, 200));
    }
    
    private void buildGui() {
        FormLayout layout = new FormLayout(
                "right:p, 7dlu, p:g, f:p:g, f:p, 4dlu, f:p",
                "f:p, 2dlu, 5dlu, f:p, 4dlu, f:p, 4dlu, f:p, 4dlu, f:p, 7dlu, 4dlu, f:p, 4dlu, f:p, 4dlu, f:p:g");
        
        PanelBuilder builder = new PanelBuilder(layout);
        builder.setBorder(null);;
        
        CellConstraints cc = new CellConstraints();
        
        builder.addSeparator("Progress", cc.xyw(1, 1, 7));
        builder.addLabel("Overall:", cc.xy(1, 4));
        builder.add(overallProgress, cc.xyw(3, 4, 3));
        builder.addLabel("Folder(s):", cc.xy(1, 6));
        builder.add(subFolderProgress, cc.xyw(3, 6, 3));
        builder.addLabel("File(s):", cc.xy(1, 8));
        builder.add(fileProgress, cc.xyw(3, 8, 3));
        builder.addLabel("Page(s):", cc.xy(1, 10));
        builder.add(pageProgress, cc.xyw(3, 10, 3));
        builder.add(waitIcon, cc.xywh(7, 3, 1, 9));

        builder.addSeparator("Currently Processing", cc.xyw(1, 13, 7));
        builder.add(currentFile, cc.xyw(1, 15, 5));
        builder.add(cancelButton, cc.xy(7, 15));
        
        builder.add(tableScroll, cc.xyw(1, 17, 7));
        
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
        
        if(folderProcessed == filesInFoldersCount[currentFolderIndex])
            updateFolderProgress();
        
        updateOverallProgress();
        
    }
    
    private void updateOverallProgress() {
        int overalProgressValue = overallProgress.getValue();
        overalProgressValue++;
        
        int percentage = overalProgressValue*100/filesCount;
        
        overallProgress.setValue(overalProgressValue);
        overallProgress.setString(percentage + "%");
    }
    
    private void updateFolderProgress() {
        int folderProgressValue = subFolderProgress.getValue();
        folderProgressValue++;
        
        int percentage = folderProgressValue*100/foldersCount;
        
        subFolderProgress.setValue(folderProgressValue);
        subFolderProgress.setString(percentage + "%");
        
        if(currentFolderIndex != foldersCount-1)
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
        
        int percentage = fileProgressValue*100/filesInFoldersCount[currentFolderIndex];
        
    	fileProgress.setValue((int)fileProgressValue);
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                pageNum++;
                pageProgress.setValue(pageNum);
                pageProgress.setString(pageNum + "/" + pageCount);
            }
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
        waitImage.flush();
        waitImage = null;
        removeAll();
        validate();
        repaint();
    }
}

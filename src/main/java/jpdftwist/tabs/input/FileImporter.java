package jpdftwist.tabs.input;

import jpdftwist.core.FilenameUtils;
import jpdftwist.gui.component.FileChooser;
import jpdftwist.gui.component.treetable.Node;
import jpdftwist.gui.component.treetable.TreeTableRowType;
import jpdftwist.gui.component.treetable.event.PageEventListener;
import jpdftwist.gui.component.treetable.row.FileTreeTableRow;
import jpdftwist.gui.dialog.ErrorDialog;
import jpdftwist.gui.tab.input.InputProgressDialog;
import jpdftwist.tabs.input.treetable.node.FileNodeFactory;
import jpdftwist.tabs.input.treetable.node.NodeFactory;
import jpdftwist.tabs.input.treetable.node.RealPdfNodeFactory;
import jpdftwist.utils.SupportedFileTypes;

import javax.swing.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vasilis Naskos
 */
public class FileImporter implements Runnable {

    private final InputProgressDialog importDialog;
    private final ModelHandler modelHandler;
    private final ErrorDialog errorDialog;

    private boolean optimizePDF;
    private boolean autoRestrictionsOverwrite;
    private boolean autoRestrioctionsNew;

    private List<File[]> files;

    private boolean cancel;

    public FileImporter(ModelHandler modelHandler) {
        this.cancel = false;
        this.optimizePDF = false;
        this.autoRestrioctionsNew = true;
        this.autoRestrictionsOverwrite = true;

        this.errorDialog = new ErrorDialog();
        this.importDialog = new InputProgressDialog();
        ProcessCancelledListener cancelListener = () -> cancel = true;
        importDialog.setCancelledListener(cancelListener);

        this.modelHandler = modelHandler;
    }

    public void setParentFrame(JFrame parentFrame) {
    }

    public void setOptimizePDF(boolean optimizePDF) {
        this.optimizePDF = optimizePDF;
    }

    public void setAutoRestrictionsOverwrite(boolean autoRestrictionsOverwrite) {
        this.autoRestrictionsOverwrite = autoRestrictionsOverwrite;
    }

    public void setAutoRestrioctionsNew(boolean autoRestrioctionsNew) {
        this.autoRestrioctionsNew = autoRestrioctionsNew;
    }

    public void setUseTempFiles(boolean useTempFiles) {
    }

    public FileImporter(ModelHandler handler, File... f) {
        this(handler);
        files = new ArrayList<>();
        files.add(f);
    }

    public FileImporter(ModelHandler handler, List<File[]> files) {
        this(handler);
        this.files = files;
    }

    public void run() {
        if (files != null && !files.isEmpty()) {
            showProgressDialog();
            setProgressBarLimits();

            for (File[] fileArray : files) {
                if (!importDialog.isVisible()) {
                    break;
                }
                importDirectory(fileArray);
            }

            importDialog.closeDialogWithDelay();
            errorDialog.showErrors();

            modelHandler.updateTableUI();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        File[] selectedFiles = fileChooser.getSelectedFiles();
        if (selectedFiles == null)
            return;

        showProgressDialog();

        DirectoryScanner scanner = new DirectoryScanner(selectedFiles);
        files = scanner.getFiles();

        setProgressBarLimits();

        Thread t = new Thread(() -> {
            for (File[] directory : files) {
                if (cancel) {
                    break;
                }
                importDirectory(directory);
            }

            importDialog.closeDialogWithDelay();
            errorDialog.showErrors();
            modelHandler.updateTableUI();
            System.gc();
        });

        t.start();
    }

    private void setProgressBarLimits() {
        int foldersCount = files.size();
        int[] filesInFolders = new int[foldersCount];
        int totalFiles = 0;

        for (int i = 0; i < foldersCount; i++) {
            filesInFolders[i] = files.get(i).length;
            totalFiles += filesInFolders[i];
        }

        importDialog.setFileCount(totalFiles);
        importDialog.setFoldersCount(foldersCount);
        importDialog.setFilesInFolderCount(filesInFolders);
    }

    private void showProgressDialog() {
        importDialog.setVisible(true);
    }

    private void importDirectory(File[] directory) {
        for (File file : directory) {
            if (cancel) {
                break;
            }
            importDialog.updateCurrentFile(FilenameUtils.normalize(file.getPath()));
            importFile(file);
            importDialog.updateProgress();
        }
    }

    private void importFile(File file) {
        // System.out.println("in import file:"+file);
        try {
            FileTreeTableRow.SubType subType = SupportedFileTypes.isPDF(file.getAbsolutePath())
                ? FileTreeTableRow.SubType.PDF
                : FileTreeTableRow.SubType.IMAGE;

            FileNodeFactory fileNodeFactory = NodeFactory.getFileNodeFactory(TreeTableRowType.REAL_FILE, subType);
            if (fileNodeFactory instanceof RealPdfNodeFactory) {
                ((RealPdfNodeFactory) fileNodeFactory).setAutoRestrictionsNew(autoRestrioctionsNew);
                ((RealPdfNodeFactory) fileNodeFactory).setAutoRestrictionsOverwrite(autoRestrictionsOverwrite);
            }
            fileNodeFactory.setOptimize(optimizePDF);
            fileNodeFactory.addPageEventListener(new PageEventListener() {

                public void pageCountChanged(int pages) {
                    importDialog.setPageCount(pages);
                }


                public void nextPage(int page) {
                    importDialog.updatePageProgress();
                }
            });
            Node node = fileNodeFactory.getFileNode(file.getAbsolutePath());
            if (node != null) {
                modelHandler.insertFileNode(node);
            }
        } catch (Exception ex) {
            Logger.getLogger(FileImporter.class.getName()).log(Level.SEVERE, null, ex);
            String exceptionTrace = getExceptionTrace(ex);
            errorDialog.reportError(file.getPath(), exceptionTrace);
        }
    }

    private String getExceptionTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);

        return sw.toString();
    }

    public void setReadPageSize(boolean readPageSizeSelected) {
        // TODO Auto-generated method stub

    }

}

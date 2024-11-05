package jpdftwist.tabs.input;

import jpdftwist.core.FilenameUtils;
import jpdftwist.gui.component.treetable.Node;
import jpdftwist.gui.component.treetable.event.PageEventListener;
import jpdftwist.gui.component.treetable.row.FileTreeTableRow;
import jpdftwist.gui.component.treetable.row.TreeTableRowType;
import jpdftwist.gui.tab.input.ImportItemsListener;
import jpdftwist.tabs.input.treetable.node.FileNodeFactory;
import jpdftwist.tabs.input.treetable.node.NodeFactory;
import jpdftwist.tabs.input.treetable.node.RealPdfNodeFactory;
import jpdftwist.utils.SupportedFileTypes;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vasilis Naskos
 */
public class FileImporter implements Runnable {

    private final ModelHandler modelHandler;
    private final ImportItemsListener importItemsListener;
    private final File[] files;
    private boolean optimizePDF = false;
    private boolean autoRestrictionsOverwrite = true;
    private boolean autoRestrictionsNew = true;
    private boolean cancel = false;

    public FileImporter(ModelHandler modelHandler, ImportItemsListener importItemsListener, File[] files) {
        ProcessCancelledListener cancelListener = () -> cancel = true;

        this.importItemsListener = importItemsListener;
        this.importItemsListener.onInit(cancelListener);

        this.modelHandler = modelHandler;

        this.files = files;
    }

    public void setOptimizePDF(boolean optimizePDF) {
        this.optimizePDF = optimizePDF;
    }

    public void setAutoRestrictionsOverwrite(boolean autoRestrictionsOverwrite) {
        this.autoRestrictionsOverwrite = autoRestrictionsOverwrite;
    }

    public void setAutoRestrictionsNew(boolean autoRestrictionsNew) {
        this.autoRestrictionsNew = autoRestrictionsNew;
    }

    public void run() {
        if (files == null || files.length == 0) {
            return;
        }
        importItemsListener.onRunStart();

        DirectoryScanner scanner = new DirectoryScanner();
        List<File[]> allFiles = scanner.getFiles(files);
        setProgressBarLimits(allFiles);

        for (File[] fileArray : allFiles) {
            if (cancel) {
                break;
            }
            importDirectory(fileArray);
        }

        importItemsListener.onRunFinish();
        modelHandler.updateTableUI();
    }

    private void setProgressBarLimits(List<File[]> files) {
        int foldersCount = files.size();
        int[] filesInFolders = new int[foldersCount];
        int totalFiles = 0;

        for (int i = 0; i < foldersCount; i++) {
            filesInFolders[i] = files.get(i).length;
            totalFiles += filesInFolders[i];
        }

        importItemsListener.onRunInit(totalFiles, foldersCount, filesInFolders);
    }

    private void importDirectory(File[] directory) {
        for (File file : directory) {
            if (cancel) {
                break;
            }
            importItemsListener.onFileReadStart(FilenameUtils.normalize(file.getPath()));
            importFile(file);
            importItemsListener.onFileReadFinish();
        }
    }

    private void importFile(File file) {
        try {
            FileTreeTableRow.SubType subType = SupportedFileTypes.isPDF(file.getAbsolutePath())
                ? FileTreeTableRow.SubType.PDF
                : FileTreeTableRow.SubType.IMAGE;

            FileNodeFactory fileNodeFactory = NodeFactory.getFileNodeFactory(TreeTableRowType.REAL_FILE, subType);
            if (fileNodeFactory == null) {
                Logger.getLogger(FileImporter.class.getName()).log(Level.SEVERE, "Ex141");
            }

            if (fileNodeFactory instanceof RealPdfNodeFactory) {
                ((RealPdfNodeFactory) fileNodeFactory).setAutoRestrictionsNew(autoRestrictionsNew);
                ((RealPdfNodeFactory) fileNodeFactory).setAutoRestrictionsOverwrite(autoRestrictionsOverwrite);
            }
            fileNodeFactory.setOptimize(optimizePDF);
            fileNodeFactory.addPageEventListener(new PageEventListener() {

                public void pageCountChanged(int pages) {
                    importItemsListener.onPageCountChange(pages);
                }

                public void nextPage(int page) {
                    importItemsListener.onPageRead(page);
                }
            });
            Node node = fileNodeFactory.getFileNode(file.getAbsolutePath());
            if (node != null) {
                modelHandler.insertFileNode(node);
            }
        } catch (Exception ex) {
            Logger.getLogger(FileImporter.class.getName()).log(Level.SEVERE, "Ex027");
            String exceptionTrace = getExceptionTrace(ex);
            importItemsListener.onError(file.getPath(), exceptionTrace);
        }
    }

    private String getExceptionTrace(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Logger.getLogger(FileImporter.class.getName()).log(Level.SEVERE, "Ex076", ex);
        return sw.toString();
    }
}

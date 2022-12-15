package jpdftwist.gui.tab.input;

import jpdftwist.gui.dialog.ErrorDialog;
import jpdftwist.tabs.input.ProcessCancelledListener;

public class DialogsImportItemsListener implements ImportItemsListener {

    final ErrorDialog errorDialog = new ErrorDialog();
    final InputProgressDialog importDialog = new InputProgressDialog();

    @Override
    public void onInit(ProcessCancelledListener cancelListener) {
        importDialog.setCancelledListener(cancelListener); //TODO: This can be onCancel
    }

    @Override
    public void onRunStart() {
        importDialog.setVisible(true);
    }

    @Override
    public void onRunFinish() {
        importDialog.closeDialogWithDelay();
        errorDialog.showErrors();
    }

    @Override
    public void onRunInit(int totalFiles, int foldersCount, int[] filesInFolders) {
        importDialog.setFileCount(totalFiles);
        importDialog.setFoldersCount(foldersCount);
        importDialog.setFilesInFolderCount(filesInFolders);
    }

    @Override
    public void onFileReadStart(String filepath) {
        importDialog.updateCurrentFile(filepath);
    }

    @Override
    public void onFileReadFinish() {
        importDialog.updateProgress();
    }

    @Override
    public void onPageCountChange(int pages) {
        importDialog.setPageCount(pages);
    }

    @Override
    public void onPageRead(int page) {
        importDialog.updatePageProgress();
    }

    @Override
    public void onError(String filepath, String exceptionTrace) {
        errorDialog.reportError(filepath, exceptionTrace);
    }
}

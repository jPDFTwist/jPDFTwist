package jpdftwist.gui.tab.input;

import jpdftwist.tabs.input.ProcessCancelledListener;

public interface ImportItemsListener {

    void onInit(ProcessCancelledListener cancelListener);

    void onRunStart();

    void onRunFinish();

    void onRunInit(int totalFiles, int foldersCount, int[] filesInFolders);

    void onFileReadStart(String filepath);

    void onFileReadFinish();

    void onPageCountChange(int pages);

    void onPageRead(int page);

    void onError(String filepath, String exceptionTrace);
}

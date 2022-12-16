package jpdftwist.core;

public interface OutputEventListener {

    void setPageCount(int pageCount);

    void updatePagesProgress();

    void setAction(String action);

    void updateJPDFTwistProgress(String tabName);

    void resetProcessedPages();

    void dispose();
}

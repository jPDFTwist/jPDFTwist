package jpdftwist.core;

public class TransitionManager {

    private final PdfReaderManager pdfReaderManager;

    private int[][] transitionValues;

    public TransitionManager(final PdfReaderManager pdfReaderManager) {
        this.pdfReaderManager = pdfReaderManager;
    }

    public void setTransition(int page, int type, int tduration, int pduration) {
        if (transitionValues == null) {
            transitionValues = new int[pdfReaderManager.getPageCount()][3];
            for (int i = 0; i < transitionValues.length; i++) {
                transitionValues[i][2] = -1;
            }
        }
        transitionValues[page - 1][0] = type;
        transitionValues[page - 1][1] = tduration;
        transitionValues[page - 1][2] = pduration;
    }

    public int[][] getTransitionValues() {
        return transitionValues;
    }
}

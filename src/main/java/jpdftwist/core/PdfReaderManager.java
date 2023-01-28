package jpdftwist.core;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;

import java.io.IOException;
import java.util.List;

public class PdfReaderManager {

    private final TempFileManager tempFileManager;
    private final InputOrderManager inputOrderManager;
    private final PdfEncryptionManager pdfEncryptionManager;

    private PdfReader currentReader;

    public PdfReaderManager(final TempFileManager tempFileManager, final InputOrderManager inputOrderManager,
                            final PdfEncryptionManager pdfEncryptionManager) {
        this.tempFileManager = tempFileManager;
        this.inputOrderManager = inputOrderManager;
        this.pdfEncryptionManager = pdfEncryptionManager;
    }

    public void initializeReader(List<PageRange> pageRanges, int interleaveSize) throws IOException {
        try {
            this.currentReader = inputOrderManager.initializeReader(tempFileManager, pageRanges, pdfEncryptionManager.getOwnerPassword(), interleaveSize, tempFileManager.getTempFile());
        } catch (DocumentException ex) {
            throw new IOException("Could not read the input", ex);
        }
    }

    public PdfReader getCurrentReader() {
        return currentReader;
    }

    public void setCurrentReader(final PdfReader currentReader) {
        this.currentReader = currentReader;
    }

    public int getPageCount() {
        return currentReader.getNumberOfPages();
    }

    public void setPageContent(int page) throws IOException {
        currentReader.setPageContent(page, currentReader.getPageContent(page));
    }

    public void cleanup() {
        if (currentReader == null) {
            return;
        }

        currentReader.close();
        currentReader = null;
    }

    /**
     * Some stuff that is unconditionally done by pdftk. Maybe it helps.
     */
    public void cargoCult() {
        currentReader.consolidateNamedDestinations();
        currentReader.removeUnusedObjects();
        currentReader.shuffleSubsetNames();
    }
}

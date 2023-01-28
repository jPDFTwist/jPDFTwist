package jpdftwist.core;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputOrderManager {

    private TempFileManager tempFileManager;

    public PdfReader initializeReader(TempFileManager tempFileManager, List<PageRange> pageRanges, byte[] ownerPassword, int interleaveSize, File tempFile) throws DocumentException, IOException {
        this.tempFileManager = tempFileManager;

        if (interleaveSize == 0) {
            return serial(pageRanges, ownerPassword, tempFile);
        }

        return interleaved(pageRanges, ownerPassword, interleaveSize, tempFile);
    }

    public PdfReader serial(List<PageRange> pageRanges, byte[] ownerPassword, File tempFile) throws DocumentException, IOException {
        Document document = new Document();
        PdfReader currentReader;
        PdfCopy copy;
        OutputStream baos = tempFileManager.createTempOutputStream();
        try {
            copy = new PdfCopy(document, baos);
        } catch (DocumentException ex) {
            Logger.getLogger(PDFTwist.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        document.open();
        int pagesBefore = 0;

        for (PageRange pageRange : pageRanges) {
            InputReader inputReader = new InputReader();
            currentReader = inputReader.getPdfReader(pageRange, ownerPassword);

            int[] pages = pageRange.getPages(pagesBefore);
            for (int page : pages) {
                if (page == -1) {
                    copy.addPage(currentReader.getPageSizeWithRotation(1), 0);
                } else {
                    copy.addPage(copy.getImportedPage(currentReader, page));
                }
            }
            copy.freeReader(currentReader);
            currentReader.close();

            pagesBefore += pages.length;
        }

        document.close();
        return PDFTwist.getTempPdfReader(baos, tempFile);
    }

    public PdfReader interleaved(List<PageRange> pageRanges, byte[] ownerPassword, int interleaveSize, File tempFile) throws IOException, DocumentException {
        Document document = new Document();
        PdfCopy copy;
        OutputStream baos = tempFileManager.createTempOutputStream();
        try {
            copy = new PdfCopy(document, baos);
        } catch (DocumentException ex) {
            Logger.getLogger(PDFTwist.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        document.open();
        PagesPerRange pagesPerRange = calculateMaxLength(pageRanges);

        int blockCount = (pagesPerRange.getMaxLength() + interleaveSize - 1) / interleaveSize;
        for (int i = 0; i < blockCount; i++) {
            for (int j = 0; j < pageRanges.size(); j++) {
                InputReader inputReader = new InputReader();
                PdfReader currentReader = inputReader.getPdfReader(pageRanges.get(j), ownerPassword);
                int[] pages = pagesPerRange.getPagesPerRange()[j];
                for (int k = 0; k < interleaveSize; k++) {
                    int pageIndex = i * interleaveSize + k;
                    int pageNum = pageIndex < pages.length ? pages[pageIndex] : -1;
                    if (pageNum == -1) {
                        copy.addPage(currentReader.getPageSizeWithRotation(1), 0);
                    } else {
                        copy.addPage(copy.getImportedPage(currentReader, pageNum));
                    }
                }
                currentReader.close();
            }
        }

        document.close();
        return PDFTwist.getTempPdfReader(baos, tempFile);
    }

    public PagesPerRange calculateMaxLength(List<PageRange> pageRanges) {
        int[][] pagesPerRange = new int[pageRanges.size()][];
        int maxLength = 0;

        for (int i = 0; i < pagesPerRange.length; i++) {
            PageRange range = pageRanges.get(i);
            pagesPerRange[i] = range.getPages(0);
            if (pagesPerRange[i].length > maxLength) {
                maxLength = pagesPerRange[i].length;
            }
        }

        return new PagesPerRange(pagesPerRange, maxLength);
    }

    public static class PagesPerRange {

        private final int[][] pagesPerRange;
        private final int maxLength;

        public PagesPerRange(final int[][] pagesPerRange, final int maxLength) {
            this.pagesPerRange = pagesPerRange;
            this.maxLength = maxLength;
        }

        public int[][] getPagesPerRange() {
            return pagesPerRange;
        }

        public int getMaxLength() {
            return maxLength;
        }
    }
}

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

public class InputOrderProcessor {

    public PdfReader initializeReader(OutputStream baos, List<PageRange> pageRanges, byte[] ownerPassword, int interleaveSize, boolean useTempFiles, File tempFile) throws DocumentException, IOException {
        if (interleaveSize == 0) {
            return serial(baos, pageRanges, ownerPassword, useTempFiles, tempFile);
        }

        return interleaved(baos, pageRanges, ownerPassword, interleaveSize, useTempFiles, tempFile);
    }

    public PdfReader serial(OutputStream baos, List<PageRange> pageRanges, byte[] ownerPassword, boolean useTempFiles, File tempFile) throws DocumentException, IOException {
        Document document = new Document();
        PdfReader currentReader;
        PdfCopy copy;
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
        return PDFTwist.getTempPdfReader(baos, useTempFiles, tempFile);
    }

    public PdfReader interleaved(OutputStream baos, List<PageRange> pageRanges, byte[] ownerPassword, int interleaveSize, boolean useTempFiles, File tempFile) throws IOException, DocumentException {
        Document document = new Document();
        PdfCopy copy;
        try {
            copy = new PdfCopy(document, baos);
        } catch (DocumentException ex) {
            Logger.getLogger(PDFTwist.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        document.open();
        int[][] pagesPerRange = new int[pageRanges.size()][];
        int maxLength = 0;

        for (int i = 0; i < pagesPerRange.length; i++) {
            PageRange range = pageRanges.get(i);
            pagesPerRange[i] = range.getPages(0);
            if (pagesPerRange[i].length > maxLength) {
                maxLength = pagesPerRange[i].length;
            }
        }

        int blockCount = (maxLength + interleaveSize - 1) / interleaveSize;
        for (int i = 0; i < blockCount; i++) {
            for (int j = 0; j < pageRanges.size(); j++) {
                InputReader inputReader = new InputReader();
                PdfReader currentReader = inputReader.getPdfReader(pageRanges.get(j), ownerPassword);
                int[] pages = pagesPerRange[j];
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
        return PDFTwist.getTempPdfReader(baos, useTempFiles, tempFile);
    }
}

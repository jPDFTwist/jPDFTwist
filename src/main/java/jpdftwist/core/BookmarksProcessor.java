package jpdftwist.core;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class BookmarksProcessor {

    private final TempFileManager tempFileManager;
    private final PdfReaderManager pdfReaderManager;

    public BookmarksProcessor(final TempFileManager tempFileManager, final PdfReaderManager pdfReaderManager) {
        this.tempFileManager = tempFileManager;
        this.pdfReaderManager = pdfReaderManager;
    }

    public void updateBookmarks(PdfBookmark[] bm, File tempFile) throws DocumentException, IOException {
        OutputStream baos = tempFileManager.createTempOutputStream();
        PdfStamper stamper = new PdfStamper(pdfReaderManager.getCurrentReader(), baos);
        stamper.setOutlines(PdfBookmark.makeBookmarks(bm));
        stamper.close();
        pdfReaderManager.setCurrentReader(PDFTwist.getTempPdfReader(baos, tempFile));
    }
}

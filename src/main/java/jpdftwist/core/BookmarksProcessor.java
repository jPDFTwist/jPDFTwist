package jpdftwist.core;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class BookmarksProcessor {

    private final TempFileManager tempFileManager;

    public BookmarksProcessor(final TempFileManager tempFileManager) {
        this.tempFileManager = tempFileManager;
    }

    public PdfReader updateBookmarks(PdfReader currentReader, PdfBookmark[] bm, File tempFile) throws DocumentException, IOException {
        OutputStream baos = tempFileManager.createTempOutputStream();
        PdfStamper stamper = new PdfStamper(currentReader, baos);
        stamper.setOutlines(PdfBookmark.makeBookmarks(bm));
        stamper.close();
        return PDFTwist.getTempPdfReader(baos, tempFile);
    }
}

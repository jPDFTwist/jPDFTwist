package jpdftwist.core;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class BookmarksProcessor {

    public PdfReader updateBookmarks(PdfReader currentReader, OutputStream baos, PdfBookmark[] bm, boolean useTempFiles, File tempFile) throws DocumentException, IOException {
        PdfStamper stamper = new PdfStamper(currentReader, baos);
        stamper.setOutlines(PdfBookmark.makeBookmarks(bm));
        stamper.close();
        return PDFTwist.getTempPdfReader(baos, useTempFiles, tempFile);
    }
}

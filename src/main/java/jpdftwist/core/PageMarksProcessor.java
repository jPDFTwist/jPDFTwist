package jpdftwist.core;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;

public class PageMarksProcessor {

    private static final String PDF_TWIST_PAGE_MARKER = "pdftk_PageNum";

    private final PdfReaderManager pdfReaderManager;

    public PageMarksProcessor(final PdfReaderManager pdfReaderManager) {
        this.pdfReaderManager = pdfReaderManager;
    }

    public void addPageMarks() {
        int pageCount = pdfReaderManager.getPageCount();
        for (int i = 1; i <= pageCount; ++i) {
            PdfDictionary p = pdfReaderManager.getCurrentReader().getPageN(i);
            if (p != null && p.isDictionary()) {
                p.put(new PdfName(PDF_TWIST_PAGE_MARKER), new PdfNumber(i));
            }
        }
    }

    public void removePageMarks() {
        int pageCount = pdfReaderManager.getPageCount();
        for (int i = 1; i <= pageCount; ++i) {
            PdfDictionary p = pdfReaderManager.getCurrentReader().getPageN(i);
            if (p != null && p.isDictionary()) {
                p.remove(new PdfName(PDF_TWIST_PAGE_MARKER));
            }
        }
    }

}

package jpdftwist.core;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;

public class PageMarksProcessor {

    private static final String PDF_TWIST_PAGE_MARKER = "pdftk_PageNum";

    public PdfReader addPageMarks(PdfReader currentReader) {
        int pageCount = currentReader.getNumberOfPages();
        for (int i = 1; i <= pageCount; ++i) {
            PdfDictionary p = currentReader.getPageN(i);
            if (p != null && p.isDictionary()) {
                p.put(new PdfName(PDF_TWIST_PAGE_MARKER), new PdfNumber(i));
            }
        }
        return currentReader;
    }
    
    public PdfReader removePageMarks(PdfReader currentReader) {
        int pageCount = currentReader.getNumberOfPages();
        for (int i = 1; i <= pageCount; ++i) {
            PdfDictionary p = currentReader.getPageN(i);
            if (p != null && p.isDictionary()) {
                p.remove(new PdfName(PDF_TWIST_PAGE_MARKER));
            }
        }
        return currentReader;
    }

}

package jpdftwist.core;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRAcroForm;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPageLabels;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class PageNumberProcessor {

    private final TempFileManager tempFileManager;
    private final PdfReaderManager pdfReaderManager;

    public PageNumberProcessor(final TempFileManager tempFileManager, final PdfReaderManager pdfReaderManager) {
        this.tempFileManager = tempFileManager;
        this.pdfReaderManager = pdfReaderManager;
    }

    public void addPageNumbers(OutputEventListener outputEventListener, PdfPageLabels.PdfPageLabelFormat[] labelFormats, File tempFile) throws DocumentException, IOException {
        OutputStream baos = tempFileManager.createTempOutputStream();
        PdfPageLabels lbls = new PdfPageLabels();
        for (PdfPageLabels.PdfPageLabelFormat format : labelFormats) {
            lbls.addPageLabel(format);
        }
        Document document = new Document(pdfReaderManager.getCurrentReader().getPageSizeWithRotation(1));
        PdfCopy copy = new PdfCopy(document, baos);
        document.open();
        PdfImportedPage page;
        outputEventListener.setAction("Adding page numbers");
        outputEventListener.setPageCount(pdfReaderManager.getPageCount());
        for (int i = 0; i < pdfReaderManager.getPageCount(); i++) {
            outputEventListener.updatePagesProgress();
            page = copy.getImportedPage(pdfReaderManager.getCurrentReader(), i + 1);
            copy.addPage(page);
        }
        PRAcroForm form = pdfReaderManager.getCurrentReader().getAcroForm();
        if (form != null) {
            copy.copyAcroForm(pdfReaderManager.getCurrentReader());
        }
        copy.setPageLabels(lbls);
        PDFTwist.copyXMPMetadata(pdfReaderManager.getCurrentReader(), copy);
        document.close();

        PdfReader resultReader = PDFTwist.getTempPdfReader(baos, tempFile);
        PDFTwist.copyInformation(pdfReaderManager.getCurrentReader(), resultReader);
        pdfReaderManager.setCurrentReader(resultReader);
    }
}

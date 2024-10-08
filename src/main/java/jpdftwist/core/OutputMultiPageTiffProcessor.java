package jpdftwist.core;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRAcroForm;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;

import jpdftwist.utils.ImageParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutputMultiPageTiffProcessor {

    private final PdfReaderManager pdfReaderManager;

    private boolean isCanceled = false;

    public OutputMultiPageTiffProcessor(final PdfReaderManager pdfReaderManager) {
        this.pdfReaderManager = pdfReaderManager;
    }

    public void output(OutputEventListener outputEventListener, String outputFile, PdfToImage pdfImages) throws IOException, DocumentException {
        if (outputFile.indexOf('*') != -1) {
            Logger.getLogger(OutputMultiPageTiffProcessor.class.getName()).log(Level.SEVERE, "Ex111");
            throw new IOException("TIFF multi-page filename should not contain *");
        }
        Document document = new Document(pdfReaderManager.getCurrentReader().getPageSizeWithRotation(1));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfCopy copy = new PdfCopy(document, baos);
        document.open();
        PdfImportedPage page;
        for (int pagenum = 1; pagenum <= pdfReaderManager.getPageCount(); pagenum++) {
            outputEventListener.updatePagesProgress();
            if (isCanceled) {
                Logger.getLogger(OutputMultiPageTiffProcessor.class.getName()).log(Level.SEVERE, "Ex097");
                throw new CancelOperationException();
            }
            page = copy.getImportedPage(pdfReaderManager.getCurrentReader(), pagenum);
            copy.addPage(page);
        }
        PRAcroForm form = pdfReaderManager.getCurrentReader().getAcroForm();
        if (form != null) {
            copy.copyAcroForm(pdfReaderManager.getCurrentReader());
        }
        document.close();
        pdfImages.convertToMultiTiff(baos.toByteArray(), outputFile);
    }

    public void cancel() {
        isCanceled = true;
    }
}

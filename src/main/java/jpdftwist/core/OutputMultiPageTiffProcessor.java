package jpdftwist.core;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRAcroForm;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class OutputMultiPageTiffProcessor {

    private boolean isCanceled = false;

    public void output(OutputEventListener outputEventListener, PdfReader currentReader, String outputFile, PdfToImage pdfImages) throws IOException, DocumentException {
        if (outputFile.indexOf('*') != -1) {
            throw new IOException("TIFF multi-page filename should not contain *");
        }
        Document document = new Document(currentReader.getPageSizeWithRotation(1));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfCopy copy = new PdfCopy(document, baos);
        document.open();
        PdfImportedPage page;
        for (int pagenum = 1; pagenum <= currentReader.getNumberOfPages(); pagenum++) {
            outputEventListener.updatePagesProgress();
            if (isCanceled) {
                throw new CancelOperationException();
            }
            page = copy.getImportedPage(currentReader, pagenum);
            copy.addPage(page);
        }
        PRAcroForm form = currentReader.getAcroForm();
        if (form != null) {
            copy.copyAcroForm(currentReader);
        }
        document.close();
        pdfImages.convertToMultiTiff(baos.toByteArray(), outputFile);
    }

    public void cancel() {
        isCanceled = true;
    }
}

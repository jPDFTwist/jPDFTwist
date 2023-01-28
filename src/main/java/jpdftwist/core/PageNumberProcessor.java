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

    public PdfReader addPageNumbers(OutputEventListener outputEventListener, PdfReader currentReader, OutputStream baos, PdfPageLabels.PdfPageLabelFormat[] labelFormats, File tempFile) throws DocumentException, IOException {
        PdfPageLabels lbls = new PdfPageLabels();
        for (PdfPageLabels.PdfPageLabelFormat format : labelFormats) {
            lbls.addPageLabel(format);
        }
        Document document = new Document(currentReader.getPageSizeWithRotation(1));
        PdfCopy copy = new PdfCopy(document, baos);
        document.open();
        PdfImportedPage page;
        outputEventListener.setAction("Adding page numbers");
        outputEventListener.setPageCount(currentReader.getNumberOfPages());
        for (int i = 0; i < currentReader.getNumberOfPages(); i++) {
            outputEventListener.updatePagesProgress();
            page = copy.getImportedPage(currentReader, i + 1);
            copy.addPage(page);
        }
        PRAcroForm form = currentReader.getAcroForm();
        if (form != null) {
            copy.copyAcroForm(currentReader);
        }
        copy.setPageLabels(lbls);
        PDFTwist.copyXMPMetadata(currentReader, copy);
        document.close();

        PdfReader resultReader = PDFTwist.getTempPdfReader(baos, tempFile);
        PDFTwist.copyInformation(currentReader, resultReader);
        return resultReader;
    }
}

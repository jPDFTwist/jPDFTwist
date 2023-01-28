package jpdftwist.core;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class RemoveRotationProcessor {

    private final TempFileManager tempFileManager;

    public RemoveRotationProcessor(final TempFileManager tempFileManager) {
        this.tempFileManager = tempFileManager;
    }

    public PdfReader apply(OutputEventListener outputEventListener, PdfReader currentReader, boolean preserveHyperlinks,
                           ArrayList<List<PDAnnotation>> pdAnnotations, File tempFile) throws DocumentException, IOException {
        OutputStream baos = tempFileManager.createTempOutputStream();
        outputEventListener.setAction("Removing Rotation");
        outputEventListener.setPageCount(currentReader.getNumberOfPages());
        boolean needed = false;
        for (int i = 1; i <= currentReader.getNumberOfPages(); i++) {
            if (currentReader.getPageRotation(i) != 0) {
                needed = true;
            }
        }
        if (!needed) {
            return currentReader;
        }
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        PdfContentByte cb = null;
        PdfImportedPage page;
        for (int i = 1; i <= currentReader.getNumberOfPages(); i++) {
            outputEventListener.updatePagesProgress();
            Rectangle currentSize = currentReader.getPageSizeWithRotation(i);
            currentSize = new Rectangle(currentSize.getWidth(), currentSize.getHeight()); // strip rotation
            document.setPageSize(currentSize);
            if (cb == null) {
                document.open();
                cb = writer.getDirectContent();
            } else {
                document.newPage();
            }
            int rotation = currentReader.getPageRotation(i);
            page = writer.getImportedPage(currentReader, i);
            float a, b, c, d, e, f;
            if (rotation == 0) {
                a = 1;
                b = 0;
                c = 0;
                d = 1;
                e = 0;
                f = 0;
            } else if (rotation == 90) {
                a = 0;
                b = -1;
                c = 1;
                d = 0;
                e = 0;
                f = currentSize.getHeight();
            } else if (rotation == 180) {
                a = -1;
                b = 0;
                c = 0;
                d = -1;
                e = currentSize.getWidth();
                f = currentSize.getHeight();
            } else if (rotation == 270) {
                a = 0;
                b = 1;
                c = -1;
                d = 0;
                e = currentSize.getWidth();
                f = 0;
            } else {
                throw new IOException("Unparsable rotation value: " + rotation);
            }
            cb.addTemplate(page, a, b, c, d, e, f);
            if (preserveHyperlinks)
                PDFTwist.repositionAnnotations(pdAnnotations, i, a, b, c, d, e, f);
        }
        PDFTwist.copyXMPMetadata(currentReader, writer);
        document.close();

        PdfReader resultReader = PDFTwist.getTempPdfReader(baos, tempFile);
        PDFTwist.copyInformation(currentReader, resultReader);
        return resultReader;
    }
}

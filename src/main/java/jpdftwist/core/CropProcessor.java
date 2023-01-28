package jpdftwist.core;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CropProcessor {

    public PdfReader apply(OutputEventListener outputEventListener, PdfReader currentReader, OutputStream baos, PageBox cropTo, boolean preserveHyperlinks,
                      ArrayList<List<PDAnnotation>> pdAnnotations, File tempFile) throws DocumentException, IOException {
        outputEventListener.setAction("Cropping");
        outputEventListener.setPageCount(currentReader.getNumberOfPages());

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        PdfContentByte cb = null;
        int[] rotations = new int[currentReader.getNumberOfPages()];
        for (int i = 1; i <= currentReader.getNumberOfPages(); i++) {
            outputEventListener.updatePagesProgress();

            PageBox box = cropTo;
            Rectangle pageSize = currentReader.getPageSize(i);
            Rectangle currentSize = null;
            while (box != null) {
                currentSize = currentReader.getBoxSize(i, box.getBoxName());
                if (currentSize != null) {
                    break;
                }
                box = box.defaultBox;
            }
            if (currentSize == null) {
                currentSize = pageSize;
            }
            document.setMargins(0, 0, 0, 0);
            document.setPageSize(new Rectangle(currentSize.getWidth(), currentSize.getHeight()));
            if (cb == null) {
                document.open();
                cb = writer.getDirectContent();
            } else {
                document.newPage();
            }
            rotations[i - 1] = currentReader.getPageRotation(i);
            PdfImportedPage page = writer.getImportedPage(currentReader, i);
            cb.addTemplate(page, pageSize.getLeft() - currentSize.getLeft(),
                pageSize.getBottom() - currentSize.getBottom());
            if (preserveHyperlinks) {
                PDFTwist.repositionAnnotations(pdAnnotations, i, 1, 0, 0, 1, pageSize.getLeft() - currentSize.getLeft(),
                    pageSize.getBottom() - currentSize.getBottom());
            }
        }
        PDFTwist.copyXMPMetadata(currentReader, writer);
        document.close();

        PdfReader resultReader = PDFTwist.getTempPdfReader(baos, tempFile);
        PDFTwist.copyInformation(currentReader, resultReader);
        // restore rotation
        for (int i = 1; i <= currentReader.getNumberOfPages(); i++) {
            PdfDictionary dic = currentReader.getPageN(i);
            dic.put(PdfName.ROTATE, new PdfNumber(rotations[i - 1]));
        }

        return resultReader;
    }
}

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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class CropProcessor {

    private final TempFileManager tempFileManager;
    private final PdfReaderManager pdfReaderManager;
    private final AnnotationsProcessor annotationsProcessor;

    public CropProcessor(final TempFileManager tempFileManager, final PdfReaderManager pdfReaderManager,
                         final AnnotationsProcessor annotationsProcessor) {
        this.tempFileManager = tempFileManager;
        this.pdfReaderManager = pdfReaderManager;
        this.annotationsProcessor = annotationsProcessor;
    }

    public void apply(OutputEventListener outputEventListener, PageBox cropTo, File tempFile) throws DocumentException, IOException {
        OutputStream baos = tempFileManager.createTempOutputStream();

        outputEventListener.setAction("Cropping");
        outputEventListener.setPageCount(pdfReaderManager.getPageCount());

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        PdfContentByte cb = null;
        int[] rotations = new int[pdfReaderManager.getPageCount()];
        for (int i = 1; i <= pdfReaderManager.getPageCount(); i++) {
            outputEventListener.updatePagesProgress();

            PageBox box = cropTo;
            Rectangle pageSize = pdfReaderManager.getCurrentReader().getPageSize(i);
            Rectangle currentSize = null;
            while (box != null) {
                currentSize = pdfReaderManager.getCurrentReader().getBoxSize(i, box.getBoxName());
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
            rotations[i - 1] = pdfReaderManager.getCurrentReader().getPageRotation(i);
            PdfImportedPage page = writer.getImportedPage(pdfReaderManager.getCurrentReader(), i);
            cb.addTemplate(page, pageSize.getLeft() - currentSize.getLeft(),
                pageSize.getBottom() - currentSize.getBottom());
            if (annotationsProcessor.isPreserveHyperlinks()) {
                annotationsProcessor.repositionAnnotations(i, 1, 0, 0, 1, pageSize.getLeft() - currentSize.getLeft(),
                    pageSize.getBottom() - currentSize.getBottom());
            }
        }
        PDFTwist.copyXMPMetadata(pdfReaderManager.getCurrentReader(), writer);
        document.close();

        PdfReader resultReader = PDFTwist.getTempPdfReader(baos, tempFile);
        PDFTwist.copyInformation(pdfReaderManager.getCurrentReader(), resultReader);
        // restore rotation
        for (int i = 1; i <= pdfReaderManager.getCurrentReader().getNumberOfPages(); i++) {
            PdfDictionary dic = pdfReaderManager.getCurrentReader().getPageN(i);
            dic.put(PdfName.ROTATE, new PdfNumber(rotations[i - 1]));
        }

        pdfReaderManager.setCurrentReader(resultReader);
    }
}

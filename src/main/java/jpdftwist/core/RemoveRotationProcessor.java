package jpdftwist.core;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfStream;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

public class RemoveRotationProcessor {

    private final TempFileManager tempFileManager;
    private final PdfReaderManager pdfReaderManager;
    private final AnnotationsProcessor annotationsProcessor;
    private final InfoDictionaryProcessor infoDictionaryProcessor;
    
    public RemoveRotationProcessor(final TempFileManager tempFileManager, final PdfReaderManager pdfReaderManager,
                                   final AnnotationsProcessor annotationsProcessor, final InfoDictionaryProcessor infoDictionaryProcessor) {
        this.tempFileManager = tempFileManager;
        this.pdfReaderManager = pdfReaderManager;
        this.annotationsProcessor = annotationsProcessor;
        this.infoDictionaryProcessor = infoDictionaryProcessor;
    }

    public void apply(OutputEventListener outputEventListener, File tempFile) throws DocumentException, IOException {
    	
        OutputStream baos1 = tempFileManager.createTempOutputStream();
        outputEventListener.setAction("Removing Rotation");
        outputEventListener.setPageCount(pdfReaderManager.getPageCount());
        boolean needed = false;
        for (int i = 1; i <= pdfReaderManager.getPageCount(); i++) {
            if (pdfReaderManager.getCurrentReader().getPageRotation(i) != 0) {
                needed = true;
            }
        }
        if (!needed) {
            return;
        }
//      PdfStamper stamper = new PdfStamper(pdfReaderManager.getCurrentReader(), null);
        Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, baos1);
		PdfContentByte cb1 = null;
        PdfImportedPage page;
        for (int i = 1; i <= pdfReaderManager.getPageCount(); i++) {
            outputEventListener.updatePagesProgress();
            Rectangle currentSize = pdfReaderManager.getCurrentReader().getPageSizeWithRotation(i);
            currentSize = new Rectangle(currentSize.getWidth(), currentSize.getHeight());		// strip rotation
            document.setPageSize(currentSize);
            if (cb1 == null) {
                document.open();
              cb1 =  writer.getDirectContent();
            } else {
                document.newPage();
            }
            int rotation = pdfReaderManager.getCurrentReader().getPageRotation(i);
            page = writer.getImportedPage(pdfReaderManager.getCurrentReader(), i);
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
                Logger.getLogger(RemoveRotationProcessor.class.getName()).log(Level.SEVERE, "Ex117");
                throw new IOException("Unparsable rotation value: " + rotation);
            }
            cb1.addTemplate(page, a, b, c, d, e, f);
            if (annotationsProcessor.isPreserveHyperlinks()) {
                annotationsProcessor.repositionAnnotations(i, a, b, c, d, e, f);
            }
        }
        PDFTwist.copyXMPMetadata(pdfReaderManager.getCurrentReader(), writer);
        document.close();

        PdfReader resultReader = PDFTwist.getTempPdfReader(baos1, tempFile);
        infoDictionaryProcessor.copyInformation(pdfReaderManager.getCurrentReader(), resultReader);
        pdfReaderManager.setCurrentReader(resultReader);
    }
}

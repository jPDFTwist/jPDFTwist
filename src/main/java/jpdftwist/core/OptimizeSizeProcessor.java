package jpdftwist.core;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRAcroForm;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPageLabels;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSmartCopy;
import com.itextpdf.text.pdf.SimpleBookmark;
import com.itextpdf.text.pdf.internal.PdfViewerPreferencesImp;

import java.io.IOException;
import java.io.OutputStream;

public class OptimizeSizeProcessor {

    private final TempFileManager tempFileManager;

    private boolean isCanceled = false;

    public OptimizeSizeProcessor(final TempFileManager tempFileManager) {
        this.tempFileManager = tempFileManager;
    }

    /**
     *
     * @param outputEventListener An event bus where the method reports it's current state
     * @param currentReader The current input item that the method should process
     * @return The processed output
     */
    public PdfReader optimizeSize(OutputEventListener outputEventListener, PdfReader currentReader) throws IOException, DocumentException {
        Document document = new Document(currentReader.getPageSizeWithRotation(1));
        OutputStream baos = tempFileManager.createTempOutputStream();
        PdfSmartCopy copy = new PdfSmartCopy(document, baos);
        document.open();
        PdfImportedPage page;
        outputEventListener.setPageCount(currentReader.getNumberOfPages());
        if (isCanceled) {
            throw new CancelOperationException();
        }
        for (int i = 0; i < currentReader.getNumberOfPages(); i++) {
            outputEventListener.updatePagesProgress();
            if (isCanceled) {
                throw new CancelOperationException();
            }
            page = copy.getImportedPage(currentReader, i + 1);
            copy.addPage(page);
        }
        PRAcroForm form = currentReader.getAcroForm();
        if (form != null) {
            copy.copyAcroForm(currentReader);
        }
        copy.setOutlines(SimpleBookmark.getBookmark(currentReader));
        PdfViewerPreferencesImp.getViewerPreferences(currentReader.getCatalog())
            .addToCatalog(copy.getExtraCatalog());
        PDFTwist.copyXMPMetadata(currentReader, copy);
        PdfPageLabels.PdfPageLabelFormat[] formats = PdfPageLabels.getPageLabelFormats(currentReader);
        if (formats != null) {
            PdfPageLabels lbls = new PdfPageLabels();
            for (PdfPageLabels.PdfPageLabelFormat format : formats) {
                lbls.addPageLabel(format);
            }
            copy.setPageLabels(lbls);
        }
        document.close();

        PdfReader optimizedSizeReader = PDFTwist.getTempPdfReader(baos, tempFileManager.getTempFile());
        PDFTwist.copyInformation(currentReader, optimizedSizeReader);

        return optimizedSizeReader;
    }

    public void cancel() {
        isCanceled = true;
    }
}

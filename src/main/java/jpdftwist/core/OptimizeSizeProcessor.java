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
    private final PdfReaderManager pdfReaderManager;
    private final InfoDictionaryProcessor infoDictionaryProcessor;

    private boolean isCanceled = false;

    public OptimizeSizeProcessor(final TempFileManager tempFileManager, final PdfReaderManager pdfReaderManager,
                                 final InfoDictionaryProcessor infoDictionaryProcessor) {
        this.tempFileManager = tempFileManager;
        this.pdfReaderManager = pdfReaderManager;
        this.infoDictionaryProcessor = infoDictionaryProcessor;
    }

    /**
     * @param outputEventListener An event bus where the method reports it's current state
     */
    public void optimizeSize(OutputEventListener outputEventListener) throws IOException, DocumentException {
        Document document = new Document(pdfReaderManager.getCurrentReader().getPageSizeWithRotation(1));
        OutputStream baos = tempFileManager.createTempOutputStream();
        PdfSmartCopy copy = new PdfSmartCopy(document, baos);
        document.open();
        PdfImportedPage page;
        outputEventListener.setPageCount(pdfReaderManager.getPageCount());
        if (isCanceled) {
            throw new CancelOperationException();
        }
        for (int i = 0; i < pdfReaderManager.getPageCount(); i++) {
            outputEventListener.updatePagesProgress();
            if (isCanceled) {
                throw new CancelOperationException();
            }
            page = copy.getImportedPage(pdfReaderManager.getCurrentReader(), i + 1);
            copy.addPage(page);
        }
        PRAcroForm form = pdfReaderManager.getCurrentReader().getAcroForm();
        if (form != null) {
            copy.copyAcroForm(pdfReaderManager.getCurrentReader());
        }
        copy.setOutlines(SimpleBookmark.getBookmark(pdfReaderManager.getCurrentReader()));
        PdfViewerPreferencesImp.getViewerPreferences(pdfReaderManager.getCurrentReader().getCatalog())
            .addToCatalog(copy.getExtraCatalog());
        PDFTwist.copyXMPMetadata(pdfReaderManager.getCurrentReader(), copy);
        PdfPageLabels.PdfPageLabelFormat[] formats = PdfPageLabels.getPageLabelFormats(pdfReaderManager.getCurrentReader());
        if (formats != null) {
            PdfPageLabels lbls = new PdfPageLabels();
            for (PdfPageLabels.PdfPageLabelFormat format : formats) {
                lbls.addPageLabel(format);
            }
            copy.setPageLabels(lbls);
        }
        document.close();

        PdfReader optimizedSizeReader = PDFTwist.getTempPdfReader(baos, tempFileManager.getTempFile());
        infoDictionaryProcessor.copyInformation(pdfReaderManager.getCurrentReader(), optimizedSizeReader);

        pdfReaderManager.setCurrentReader(optimizedSizeReader);
    }

    public void cancel() {
        isCanceled = true;
    }
}

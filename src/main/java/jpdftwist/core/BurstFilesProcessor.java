package jpdftwist.core;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PRAcroForm;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPageLabels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BurstFilesProcessor {

    private final PdfReaderManager pdfReaderManager;
    private final PdfEncryptionManager pdfEncryptionManager;

    private boolean isCanceled = false;

    public BurstFilesProcessor(final PdfReaderManager pdfReaderManager, final PdfEncryptionManager pdfEncryptionManager) {
        this.pdfReaderManager = pdfReaderManager;
        this.pdfEncryptionManager = pdfEncryptionManager;
    }

    public void burst(OutputEventListener outputEventListener, String outputFile, boolean fullyCompressed, PdfToImage pdfImages) throws IOException, DocumentException {
        if (outputFile.indexOf('*') == -1) {
            Logger.getLogger(BurstFilesProcessor.class.getName()).log(Level.SEVERE, "Ex092");
            throw new IOException("Output filename does not contain *");
        }
        String prefix = outputFile.substring(0, outputFile.indexOf('*'));
        String suffix = outputFile.substring(outputFile.indexOf('*') + 1);
        String[] pageLabels = PdfPageLabels.getPageLabels(pdfReaderManager.getCurrentReader());
        PdfCopy copy;
        ByteArrayOutputStream baos = null;
        for (int pagenum = 1; pagenum <= pdfReaderManager.getPageCount(); pagenum++) {
            outputEventListener.updatePagesProgress();
            if (isCanceled) {
                Logger.getLogger(BurstFilesProcessor.class.getName()).log(Level.SEVERE, "Ex093");
                throw new CancelOperationException();
            }
            Document document = new Document(pdfReaderManager.getCurrentReader().getPageSizeWithRotation(1));
            String pageNumber = "" + pagenum;
            if (pageLabels != null && pagenum <= pageLabels.length) {
                pageNumber = pageLabels[pagenum - 1];
            }
            File outFile = new File(prefix + pageNumber + suffix);
            if (!outFile.getParentFile().isDirectory()) {
                outFile.getParentFile().mkdirs();
            }
            if (pdfImages.shouldExecute()) {
                baos = new ByteArrayOutputStream();
                copy = new PdfCopy(document, baos);
            } else {
                copy = new PdfCopy(document, Files.newOutputStream(outFile.toPath()));
                pdfEncryptionManager.setEncryptionSettings(copy);
                if (fullyCompressed) {
                    copy.setFullCompression();
                }
            }
            document.open();
            PdfImportedPage page;
            page = copy.getImportedPage(pdfReaderManager.getCurrentReader(), pagenum);
            copy.addPage(page);
            PRAcroForm form = pdfReaderManager.getCurrentReader().getAcroForm();
            if (form != null) {
                copy.copyAcroForm(pdfReaderManager.getCurrentReader());
            }
            document.close();
            if (pdfImages.shouldExecute()) {
                pdfImages.convertToImage(baos.toByteArray(), prefix + pageNumber + suffix);
            }
        }
    }

    public void cancel() {
        this.isCanceled = true;
    }
}

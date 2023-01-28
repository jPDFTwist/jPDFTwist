package jpdftwist.core;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfTransition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class OutputPdfProcessor {

    private final PdfReaderManager pdfReaderManager;
    private final PdfEncryptionManager pdfEncryptionManager;
    private final SignatureManager signatureManager;
    private final AttachmentsManager attachmentsManager;
    private final TransitionManager transitionManager;
    private final ViewerPreferencesManager viewerPreferencesManager;

    private boolean isCanceled;

    public OutputPdfProcessor(final PdfReaderManager pdfReaderManager, final PdfEncryptionManager pdfEncryptionManager,
                              final SignatureManager signatureManager, final AttachmentsManager attachmentsManager,
                              final TransitionManager transitionManager, final ViewerPreferencesManager viewerPreferencesManager) {
        this.pdfReaderManager = pdfReaderManager;
        this.pdfEncryptionManager = pdfEncryptionManager;
        this.signatureManager = signatureManager;
        this.attachmentsManager = attachmentsManager;
        this.transitionManager = transitionManager;
        this.viewerPreferencesManager = viewerPreferencesManager;
    }

    public void output(OutputEventListener outputEventListener, String outputFile, boolean fullyCompressed, int total) throws IOException, DocumentException {
        PdfStamper stamper;
        if (signatureManager.getKey() != null) {
            new File(outputFile).getParentFile().mkdirs();
            stamper = PdfStamper.createSignature(pdfReaderManager.getCurrentReader(), Files.newOutputStream(Paths.get(outputFile)), '\0', null,
                true);
            PdfSignatureAppearance sap = stamper.getSignatureAppearance();
            sap.setCrypto(signatureManager.getKey(), signatureManager.getCertChain(), null, PdfSignatureAppearance.WINCER_SIGNED);
            sap.setCertificationLevel(signatureManager.getCertificationLevel());
            if (signatureManager.isSigVisible()) {
                sap.setVisibleSignature(new Rectangle(100, 100, 200, 200), 1, null);
            }
        } else {
            new File(outputFile).getParentFile().mkdirs();
            stamper = new PdfStamper(pdfReaderManager.getCurrentReader(), Files.newOutputStream(Paths.get(outputFile)));
        }
        pdfEncryptionManager.setEncryptionSettings(stamper);
        if (fullyCompressed) {
            stamper.setFullCompression();
        }
        for (int i = 1; i <= total; i++) {
            outputEventListener.updatePagesProgress();
            if (isCanceled) {
                throw new CancelOperationException();
            }

            pdfReaderManager.setPageContent(i);
        }

        int[][] transitionValues = transitionManager.getTransitionValues();
        if (transitionValues != null) {
            for (int i = 0; i < total; i++) {
                PdfTransition t = transitionValues[i][0] == 0 ? null
                    : new PdfTransition(transitionValues[i][0], transitionValues[i][1]);
                stamper.setTransition(t, i + 1);
                stamper.setDuration(transitionValues[i][2], i + 1);
            }
        }

        if (viewerPreferencesManager.getOptionalViewerPreferences() != null) {
            stamper.setViewerPreferences(viewerPreferencesManager.getSimpleViewerPreferences());
            for (Map.Entry<PdfName, PdfObject> e : viewerPreferencesManager.getOptionalViewerPreferences().entrySet()) {
                stamper.addViewerPreference(e.getKey(), e.getValue());
            }
        }

        for (File f : attachmentsManager.getAttachments()) {
            stamper.addFileAttachment(f.getName(), null, f.getAbsolutePath(), f.getName());
        }
        stamper.close();
    }

    public void cancel() {
        this.isCanceled = true;
    }
}

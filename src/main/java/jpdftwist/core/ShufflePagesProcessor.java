package jpdftwist.core;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ShufflePagesProcessor {

    private final TempFileManager tempFileManager;
    private final PdfReaderManager pdfReaderManager;
    private final AnnotationsProcessor annotationsProcessor;

    public ShufflePagesProcessor(final TempFileManager tempFileManager, final PdfReaderManager pdfReaderManager,
                                 final AnnotationsProcessor annotationsProcessor) {
        this.tempFileManager = tempFileManager;
        this.pdfReaderManager = pdfReaderManager;
        this.annotationsProcessor = annotationsProcessor;
    }

    public void apply(OutputEventListener outputEventListener, int passLength, int blockSize, ShuffleRule[] shuffleRules,
                      File tempFile) throws DocumentException, IOException {
        OutputStream baos = tempFileManager.createTempOutputStream();
        outputEventListener.setAction("Shuffling");
        outputEventListener.setPageCount(pdfReaderManager.getPageCount());

        RemoveRotationProcessor removeRotationProcessor = new RemoveRotationProcessor(tempFileManager, pdfReaderManager, annotationsProcessor);
        removeRotationProcessor.apply(outputEventListener, tempFile);

        Rectangle size = pdfReaderManager.getCurrentReader().getPageSize(1);
        for (int i = 1; i <= pdfReaderManager.getCurrentReader().getNumberOfPages(); i++) {
            outputEventListener.updatePagesProgress();
            if (pdfReaderManager.getCurrentReader().getPageSize(i).getWidth() != size.getWidth()
                || pdfReaderManager.getCurrentReader().getPageSize(i).getHeight() != size.getHeight()) {
                throw new IOException(
                    "Pages must have equals sizes to be shuffled. Use the Scale option on the PageSize tab first.");
            }
            if (pdfReaderManager.getCurrentReader().getPageRotation(i) != 0) {
                throw new RuntimeException();
            }
        }
        Document document = new Document(size, 0, 0, 0, 0);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();
        PdfContentByte cb = writer.getDirectContent();
        PdfTemplate page;
        int pl = Math.abs(passLength);
        int cnt = pdfReaderManager.getCurrentReader().getNumberOfPages();
        int passes = blockSize == 0 ? 1 : (cnt + blockSize - 1) / blockSize;
        int[] destinationPageNumbers;
        destinationPageNumbers = new int[cnt + 1];
        int ddPage = 0;
        for (int pass = 0; pass < passes; pass++) {
            int passcnt = pass == passes - 1 ? cnt - pass * blockSize : blockSize;
            int refcnt = ((passcnt + (pl - 1)) / pl) * pl;
            for (int i = 0; i < passcnt; i += pl) {
                int idx = i;
                int reverseIdx = refcnt - idx - pl;
                if (passLength < 0) {
                    idx = i / 2;
                    reverseIdx = refcnt - idx - pl;
                }
                idx += pass * blockSize;
                reverseIdx += pass * blockSize;
                for (ShuffleRule sr : shuffleRules) {
                    if (sr.isNewPageBefore()) {
                        ddPage++;
                    }
                    int pg = sr.getPageNumber();
                    if (sr.getPageBase() == ShuffleRule.PageBase.BEGINNING) {
                        pg += idx;
                    } else if (sr.getPageBase() == ShuffleRule.PageBase.END) {
                        pg += reverseIdx;
                    }
                    if (pg < 1) {
                        throw new IOException("Invalid page number. Check your n-up rules.");
                    }
                    if (pg <= cnt) {
                        destinationPageNumbers[pg] = ddPage;
                    }
                }
            }
        }

        ArrayList<List<PDAnnotation>> tmp = new ArrayList<>();

        for (int pass = 0; pass < passes; pass++) {
            int passcnt = pass == passes - 1 ? cnt - pass * blockSize : blockSize;
            int refcnt = ((passcnt + (pl - 1)) / pl) * pl;

            for (int i = 0; i < passcnt; i += pl) {
                int idx = i;
                int reverseIdx = refcnt - idx - pl;
                if (passLength < 0) {
                    idx = i / 2;
                    reverseIdx = refcnt - idx - pl;
                }
                idx += pass * blockSize;
                reverseIdx += pass * blockSize;
                for (ShuffleRule sr : shuffleRules) {
                    if (sr.isNewPageBefore()) {
                        document.newPage();
                    }
                    float s = (float) sr.getScale();
                    float offsetx = (float) sr.getOffsetX();
                    float offsety = (float) sr.getOffsetY();
                    if (sr.isOffsetXPercent()) {
                        offsetx = offsetx * size.getWidth() / 100;
                    }
                    if (sr.isOffsetXPercent()) {
                        offsety = offsety * size.getHeight() / 100;
                    }
                    float a, b, c, d, e, f;
                    switch (sr.getRotate()) {
                        case 'N':
                            a = s;
                            b = 0;
                            c = 0;
                            d = s;
                            e = offsetx * s;
                            f = offsety * s;
                            break;
                        case 'R':
                            a = 0;
                            b = -s;
                            c = s;
                            d = 0;
                            e = offsety * s;
                            f = -offsetx * s;
                            break;
                        case 'U':
                            a = -s;
                            b = 0;
                            c = 0;
                            d = -s;
                            e = -offsetx * s;
                            f = -offsety * s;
                            break;
                        case 'L':
                            a = 0;
                            b = s;
                            c = -s;
                            d = 0;
                            e = -offsety * s;
                            f = offsetx * s;
                            break;
                        default:
                            throw new RuntimeException("" + sr.getRotate());
                    }
                    int pg = sr.getPageNumber();
                    if (sr.getPageBase() == ShuffleRule.PageBase.BEGINNING) {
                        pg += idx;
                    } else if (sr.getPageBase() == ShuffleRule.PageBase.END) {
                        pg += reverseIdx;
                    }
                    if (pg < 1) {
                        throw new IOException("Invalid page number. Check your n-up rules.");
                    }
                    if (pg <= cnt) {
                        page = writer.getImportedPage(pdfReaderManager.getCurrentReader(), pg);
                        cb.addTemplate(page, a, b, c, d, e, f);
                        if (annotationsProcessor.isPreserveHyperlinks()) {
                            annotationsProcessor.repositionAnnotations(pg, a, b, c, d, e, f);
                        }
                        if (sr.getFrameWidth() > 0) {
                            cb.setLineWidth((float) sr.getFrameWidth());
                            cb.rectangle(e, f, a * size.getWidth() + c * size.getHeight(),
                                b * size.getWidth() + d * size.getHeight());
                            cb.stroke();
                        }
                    } else {
                        writer.setPageEmpty(false);
                    }

                    if (annotationsProcessor.isPreserveHyperlinks()) {
                        if (pg < destinationPageNumbers.length) {
                            if (destinationPageNumbers[pg] - 1 < tmp.size()) {
                                tmp.get(destinationPageNumbers[pg] - 1).addAll(annotationsProcessor.getAnnotations(pg - 1));
                            } else {
                                tmp.add(destinationPageNumbers[pg] - 1, annotationsProcessor.getAnnotations(pg - 1));
                            }
                        }
                    }
                }
            }
        }

        PDFTwist.copyXMPMetadata(pdfReaderManager.getCurrentReader(), writer);
        document.close();

        PdfReader resultReader = PDFTwist.getTempPdfReader(baos, tempFile);
        PDFTwist.copyInformation(pdfReaderManager.getCurrentReader(), resultReader);

        pdfReaderManager.setCurrentReader(resultReader);
        annotationsProcessor.setAnnotations(tmp);
    }
}

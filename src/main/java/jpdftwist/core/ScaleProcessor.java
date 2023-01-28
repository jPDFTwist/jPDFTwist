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
import jpdftwist.core.tabparams.ScaleParameters;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ScaleProcessor {

    private final TempFileManager tempFileManager;
    private final PdfReaderManager pdfReaderManager;

    private float offsetX;
    private float offsetY;

    public ScaleProcessor(final TempFileManager tempFileManager, final PdfReaderManager pdfReaderManager) {
        this.tempFileManager = tempFileManager;
        this.pdfReaderManager = pdfReaderManager;
    }

    public void apply(OutputEventListener outputEventListener, ScaleParameters param, boolean preserveHyperlinks,
                           ArrayList<List<PDAnnotation>> pdAnnotations, File tempFile) throws DocumentException, IOException {
        OutputStream baos = tempFileManager.createTempOutputStream();
        outputEventListener.setAction("Scaling");
        outputEventListener.setPageCount(pdfReaderManager.getPageCount());

        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();
        PdfContentByte cb = writer.getDirectContent();
        PdfImportedPage page;

        for (int i = 1; i <= pdfReaderManager.getPageCount(); i++) {
            outputEventListener.updatePagesProgress();

            Rectangle newSize = new Rectangle(param.getPageDim().getWidth(), param.getPageDim().getHeight());

            Rectangle currentSize = pdfReaderManager.getCurrentReader().getPageSizeWithRotation(i);

            int rotation = pdfReaderManager.getCurrentReader().getPageRotation(i);
            PdfDictionary dic = pdfReaderManager.getCurrentReader().getPageN(i);
            dic.remove(PdfName.ROTATE);

            if (!param.isLandscape() && !param.isPortrait()) {
                if (param.getPageDim().isPercentange()) {
                    float width = ((param.getPageDim().getWidth() / 100)) * currentSize.getWidth();
                    float height = ((param.getPageDim().getHeight() / 100)) * currentSize.getHeight();
                    newSize = new Rectangle(width, height);
                }
            }

            if (currentSize.getWidth() > currentSize.getHeight()) { // landscape
                if (param.isLandscape()) {
                    if (currentSize.getWidth() >= param.getLandscapeLowerLimit()
                        && currentSize.getWidth() <= param.getLandscapeUpperLimit()) {
                        if (!param.getLandscapePageDim().isPercentange()) {
                            newSize = new Rectangle(param.getLandscapePageDim().getWidth(),
                                param.getLandscapePageDim().getHeight());
                        } else {
                            float width = ((param.getLandscapePageDim().getWidth() / 100)) * currentSize.getWidth();
                            float height = ((param.getLandscapePageDim().getHeight() / 100)) * currentSize.getHeight();
                            newSize = new Rectangle(width, height);
                        }
                    } else {
                        newSize = currentSize;
                    }
                } else if (param.isPortrait()) {
                    newSize = currentSize;
                }
            } else { // portrait
                if (param.isPortrait()) {
                    if (currentSize.getHeight() >= param.getPortraitLowerLimit()
                        && currentSize.getHeight() <= param.getPortraitUpperLimit()) {
                        if (!param.getPortraitPageDim().isPercentange()) {
                            newSize = new Rectangle(param.getPortraitPageDim().getWidth(),
                                param.getPortraitPageDim().getHeight());
                        } else {
                            float width = ((param.getPortraitPageDim().getWidth() / 100)) * currentSize.getWidth();
                            float height = ((param.getPortraitPageDim().getHeight() / 100)) * currentSize.getHeight();
                            newSize = new Rectangle(width, height);
                        }
                    } else {
                        newSize = currentSize;
                    }
                } else if (param.isLandscape()) {
                    newSize = currentSize;
                }
            }

            if (rotation == 90 || rotation == 270) {
                newSize = new Rectangle(newSize.getHeight(), newSize.getWidth());
                currentSize = new Rectangle(currentSize.getHeight(), currentSize.getWidth());
            }

            document.setPageSize(newSize);
            document.newPage();

            float factorX = newSize.getWidth() / currentSize.getWidth();
            float factorY = newSize.getHeight() / currentSize.getHeight();

            if (param.isNoEnlarge()) {
                if (factorX > 1) {
                    factorX = 1;
                }
                if (factorY > 1) {
                    factorY = 1;
                }
            }
            if (param.isPreserveAspectRatio()) {
                factorX = Math.min(factorX, factorY);
                factorY = factorX;
            }

            offsetX = (newSize.getWidth() - (currentSize.getWidth() * factorX)) / 2f;
            offsetY = (newSize.getHeight() - (currentSize.getHeight() * factorY)) / 2f;

            if (currentSize.getWidth() > currentSize.getHeight() && param.isLandscape()) {
                justify(justifyValueWithRotation(rotation, param.getJustifyLandscape()), currentSize, newSize, factorX,
                    factorY);
            } else if (currentSize.getWidth() <= currentSize.getHeight() && param.isPortrait()) {
                justify(justifyValueWithRotation(rotation, param.getJustifyPortrait()), currentSize, newSize, factorX,
                    factorY);
            } else {
                justify(justifyValueWithRotation(rotation, param.getJustify()), currentSize, newSize, factorX, factorY);
            }

            page = writer.getImportedPage(pdfReaderManager.getCurrentReader(), i);
            cb.addTemplate(page, factorX, 0, 0, factorY, offsetX, offsetY);

            if (preserveHyperlinks) {
                PDFTwist.repositionAnnotations(pdAnnotations, i, factorX, 0, 0, factorY, offsetX, offsetY);
            }
            writer.addPageDictEntry(PdfName.ROTATE, new PdfNumber(rotation));
        }
        PDFTwist.copyXMPMetadata(pdfReaderManager.getCurrentReader(), writer);
        document.close();
        PdfReader resultReader = PDFTwist.getTempPdfReader(baos, tempFile);
        PDFTwist.copyInformation(pdfReaderManager.getCurrentReader(), resultReader);
        pdfReaderManager.setCurrentReader(resultReader);
    }

    private int justifyValueWithRotation(int rotation, int index) {
        int value = index;

        if (rotation == 90 || rotation == 270) {
            switch (index) {
                case 0:
                    value = 6;
                    break;
                case 1:
                    value = 3;
                    break;
                case 2:
                    value = 0;
                    break;
                case 3:
                    value = 7;
                    break;
                case 4:
                    value = 4;
                    break;
                case 5:
                    value = 1;
                    break;
                case 6:
                    value = 8;
                    break;
                case 7:
                    value = 5;
                    break;
                case 8:
                    value = 2;
                    break;
            }
        }

        if (rotation == 270 || rotation == 180) {
            value = 8 - value;
        }

        return value;
    }

    public void justify(int index, Rectangle currentSize, Rectangle newSize, float factorX, float factorY) {
        switch (index) {
            case 0: // TOP LEFT
                offsetX = 0;
                offsetY = newSize.getHeight() - (currentSize.getHeight() * factorY);
                break;
            case 1: // TOP_CENTER
                offsetX = (newSize.getWidth() - (currentSize.getWidth() * factorX)) / 2f;
                offsetY = newSize.getHeight() - (currentSize.getHeight() * factorY);
                break;
            case 2: // TOP_RIGHT
                offsetX = newSize.getWidth() - (currentSize.getWidth() * factorX);
                offsetY = newSize.getHeight() - (currentSize.getHeight() * factorY);
                break;
            case 3: // LEFT
                offsetX = 0;
                offsetY = (newSize.getHeight() - (currentSize.getHeight() * factorY)) / 2f;
                break;
            case 5: // RIGHT
                offsetX = newSize.getWidth() - (currentSize.getWidth() * factorX);
                offsetY = (newSize.getHeight() - (currentSize.getHeight() * factorY)) / 2f;
                break;
            case 6: // BOTTOM LEFT
                offsetX = 0;
                offsetY = 0;
                break;
            case 7: // BOTTOM CENTER
                offsetX = (newSize.getWidth() - (currentSize.getWidth() * factorX)) / 2f;
                offsetY = 0;
                break;
            case 8: // BOTTOM RIGHT
                offsetX = newSize.getWidth() - (currentSize.getWidth() * factorX);
                offsetY = 0;
                break;
            case 4: // CENTER
            default:
                offsetX = (newSize.getWidth() - (currentSize.getWidth() * factorX)) / 2f;
                offsetY = (newSize.getHeight() - (currentSize.getHeight() * factorY)) / 2f;
                break;
        }

        if (offsetX < 0) {
            offsetX = 0;
        }
        if (offsetY < 0) {
            offsetY = 0;
        }

    }
}

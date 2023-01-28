package jpdftwist.core;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import jpdftwist.core.tabparams.RotateParameters;

public class RotateProcessor {

    private final PdfReaderManager pdfReaderManager;

    public RotateProcessor(final PdfReaderManager pdfReaderManager) {
        this.pdfReaderManager = pdfReaderManager;
    }

    public void apply(OutputEventListener outputEventListener, RotateParameters param) {
        outputEventListener.setAction("Rotating");
        outputEventListener.setPageCount(pdfReaderManager.getPageCount());
        for (int i = 1; i <= pdfReaderManager.getPageCount(); i++) {
            outputEventListener.updatePagesProgress();
            int rotation = pdfReaderManager.getCurrentReader().getPageRotation(i);
            Rectangle r = pdfReaderManager.getCurrentReader().getPageSizeWithRotation(i);
            int count;
            if (r.getWidth() > r.getHeight()) { // landscape
                if (param.isLandscape()) {
                    if (r.getWidth() >= param.getLandscapeLowerLimit()
                        && r.getWidth() <= param.getLandscapeUpperLimit()) {
                        count = param.getLandscapeCount();
                    } else {
                        count = 0;
                    }
                } else {
                    count = param.getLandscapeCount();
                }
            } else {
                if (param.isPortrait()) {
                    if (r.getHeight() >= param.getPortraitLowerLimit()
                        && r.getHeight() <= param.getPortraitUpperLimit()) {
                        count = param.getPortraitCount();
                    } else {
                        count = 0;
                    }
                } else {
                    count = param.getPortraitCount();
                }
            }
            rotation = (rotation + 90 * count) % 360;
            PdfDictionary dic = pdfReaderManager.getCurrentReader().getPageN(i);
            dic.put(PdfName.ROTATE, new PdfNumber(rotation));
        }
    }
}

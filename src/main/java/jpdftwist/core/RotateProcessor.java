package jpdftwist.core;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfReader;
import jpdftwist.core.tabparams.RotateParameters;

public class RotateProcessor {

    public PdfReader apply(OutputEventListener outputEventListener, PdfReader currentReader, RotateParameters param) {
        outputEventListener.setAction("Rotating");
        outputEventListener.setPageCount(currentReader.getNumberOfPages());
        for (int i = 1; i <= currentReader.getNumberOfPages(); i++) {
            outputEventListener.updatePagesProgress();
            int rotation = currentReader.getPageRotation(i);
            Rectangle r = currentReader.getPageSizeWithRotation(i);
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
            PdfDictionary dic = currentReader.getPageN(i);
            dic.put(PdfName.ROTATE, new PdfNumber(rotation));
        }

        return currentReader;
    }
}

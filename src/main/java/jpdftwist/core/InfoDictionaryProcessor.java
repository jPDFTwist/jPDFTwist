package jpdftwist.core;

import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfString;

import java.util.Map;

public class InfoDictionaryProcessor {

    private final PdfReaderManager pdfReaderManager;

    public InfoDictionaryProcessor(final PdfReaderManager pdfReaderManager) {
        this.pdfReaderManager = pdfReaderManager;
    }

    public void updateInfoDictionary(Map<String, String> newInfo) {
        PdfDictionary trailer = pdfReaderManager.getCurrentReader().getTrailer();
        if (trailer != null && trailer.isDictionary()) {
            PdfObject info = PdfReader.getPdfObject(trailer.get(PdfName.INFO));
            if (info != null && info.isDictionary()) {
                PdfDictionary infoDic = (PdfDictionary) info;
                for (Map.Entry<String, String> entry : newInfo.entrySet()) {
                    if (entry.getValue().length() == 0) {
                        infoDic.remove(new PdfName(entry.getKey()));
                    } else {
                        infoDic.put(new PdfName(entry.getKey()),
                            new PdfString(entry.getValue(), PdfObject.TEXT_UNICODE));
                    }
                }
            }
        }
        // remove XMP metadata
        pdfReaderManager.getCurrentReader().getCatalog().remove(PdfName.METADATA);
    }

    public void copyInformation(PdfReader source, PdfReader destination) {
        PdfDictionary srcTrailer = source.getTrailer();
        PdfDictionary dstTrailer = destination.getTrailer();
        if (srcTrailer != null && srcTrailer.isDictionary() && dstTrailer != null && dstTrailer.isDictionary()) {
            PdfObject srcInfo = PdfReader.getPdfObject(srcTrailer.get(PdfName.INFO));
            PdfObject dstInfo = PdfReader.getPdfObject(dstTrailer.get(PdfName.INFO));
            if (srcInfo != null && srcInfo.isDictionary() && dstInfo != null && dstInfo.isDictionary()) {
                PdfDictionary srcInfoDic = (PdfDictionary) srcInfo;
                PdfDictionary dstInfoDic = (PdfDictionary) dstInfo;
                for (PdfName key : srcInfoDic.getKeys()) {
                    PdfObject value = srcInfoDic.get(key);
                    dstInfoDic.put(key, value);
                }
            }
        }
        source.close();
    }
}

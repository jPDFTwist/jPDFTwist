package jpdftwist.core;

import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;

import java.util.Map;

public class ViewerPreferencesManager {

    private Map<PdfName, PdfObject> optionalViewerPreferences;
    private int simpleViewerPreferences;

    public void setViewerPreferences(int simplePrefs, Map<PdfName, PdfObject> optionalPrefs) {
        this.optionalViewerPreferences = optionalPrefs;
        this.simpleViewerPreferences = simplePrefs;
    }

    public Map<PdfName, PdfObject> getOptionalViewerPreferences() {
        return optionalViewerPreferences;
    }

    public int getSimpleViewerPreferences() {
        return simpleViewerPreferences;
    }
}

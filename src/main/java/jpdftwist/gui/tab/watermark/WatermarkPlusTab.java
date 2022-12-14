package jpdftwist.gui.tab.watermark;

import com.itextpdf.text.DocumentException;
import jpdftwist.core.PDFTwist;
import jpdftwist.core.watermark.WatermarkStyle;
import jpdftwist.tabs.ActionTab;

import javax.swing.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vasilis Naskos
 */
public class WatermarkPlusTab extends ActionTab {

    private static final String TAB_NAME = "Watermark +";
    private WatermarkPlusTabPanel watermarkPlusTabPanel;

    public String getTabName() {
        return TAB_NAME;
    }

    public JPanel getUserInterface() {
        watermarkPlusTabPanel = WatermarkPlusTabPanel.getWatermarkTabPanel();

        return watermarkPlusTabPanel;
    }

    public void checkRun() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PDFTwist run(PDFTwist input) {
        for (WatermarkStyle style : watermarkPlusTabPanel.getStyles()) {
            try {
                input.addWatermark(style);
            } catch (DocumentException | IOException ex) {
                Logger.getLogger(WatermarkPlusTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return input;
    }
}

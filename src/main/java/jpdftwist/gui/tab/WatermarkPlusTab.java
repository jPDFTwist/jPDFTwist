package jpdftwist.gui.tab;

import com.itextpdf.text.DocumentException;
import jpdftwist.core.PDFTwist;
import jpdftwist.tabs.ActionTab;
import jpdftwist.tabs.watermark.WatermarkStyle;
import jpdftwist.tabs.watermark.WatermarkTabPanel;

import javax.swing.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vasilis Naskos
 */
public class WatermarkPlusTab extends ActionTab {

    private static final String TAB_NAME = "Watermark +";
    private WatermarkTabPanel watermarkTabPanel;

    public String getTabName() {
        return TAB_NAME;
    }

    public JPanel getUserInterface() {
        watermarkTabPanel = WatermarkTabPanel.getWatermarkTabPanel();

        return watermarkTabPanel;
    }

    public void checkRun() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PDFTwist run(PDFTwist input) {
        for (WatermarkStyle style : watermarkTabPanel.getStyles()) {
            try {
                input.addWatermark(style);
            } catch (DocumentException | IOException ex) {
                Logger.getLogger(WatermarkPlusTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return input;
    }
}

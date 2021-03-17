package jpdftweak.tabs;

import com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import jpdftweak.core.PdfTweak;
import jpdftweak.tabs.watermark.WatermarkStyle;
import jpdftweak.tabs.watermark.WatermarkTabPanel;

/**
 *
 * @author Vasilis Naskos
 */
public class WatermarkPlusTab extends ActionTab {

    private static final String TAB_NAME = "Watermark+";
    private WatermarkTabPanel watermarkTabPanel;
    
    @Override
    public String getTabName() {
        return TAB_NAME;
    }

    @Override
    public JPanel getUserInterface() {
        watermarkTabPanel = WatermarkTabPanel.getWatermarkTabPanel();
        
        return watermarkTabPanel;
    }

    @Override
    public void checkRun() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PdfTweak run(PdfTweak input) {
        for(WatermarkStyle style : watermarkTabPanel.getStyles()) {
            try {
                input.addWatermark(style);
            } catch (DocumentException ex) {
                Logger.getLogger(WatermarkPlusTab.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(WatermarkPlusTab.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return input;
    }
    
}

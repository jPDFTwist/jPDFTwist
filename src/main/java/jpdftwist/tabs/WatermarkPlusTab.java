package jpdftwist.tabs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;

import com.itextpdf.text.DocumentException;

import jpdftwist.core.PDFTwist;
import jpdftwist.tabs.watermark.WatermarkStyle;
import jpdftwist.tabs.watermark.WatermarkTabPanel;

/**
 *
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
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
		// Tools | Templates.
	}

	
	public PDFTwist run(PDFTwist input) {
		for (WatermarkStyle style : watermarkTabPanel.getStyles()) {
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

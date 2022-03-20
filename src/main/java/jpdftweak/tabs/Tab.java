package jpdftweak.tabs;

import java.awt.LayoutManager;
import java.io.IOException;

import javax.swing.JPanel;

import com.itextpdf.text.DocumentException;

import jpdftweak.core.PdfTweak;
import jpdftweak.gui.dialogs.OutputProgressDialog;

public abstract class Tab extends JPanel {

	public Tab(LayoutManager layout) {
		super(layout);
	}

	public abstract String getTabName();

	public void checkRun() throws IOException {
	}

	public abstract PdfTweak run(PdfTweak input, OutputProgressDialog outDialog) throws IOException, DocumentException;
}

package jpdftwist.tabs;

import com.itextpdf.text.DocumentException;
import jpdftwist.core.PDFTwist;
import jpdftwist.gui.dialog.OutputProgressDialog;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public abstract class Tab extends JPanel {

	public Tab(LayoutManager layout) {
		super(layout);
	}

	public abstract String getTabName();

	public void checkRun() throws IOException {
	}

	public abstract PDFTwist run(PDFTwist input, OutputProgressDialog outDialog) throws IOException, DocumentException;
}

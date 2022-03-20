package jpdftweak.tabs;

import java.awt.BorderLayout;

import javax.swing.JLabel;

import jpdftweak.core.PdfTweak;
import jpdftweak.gui.dialogs.OutputProgressDialog;

public class DummyTab extends Tab {

	private final String name;

	public DummyTab(String name) {
		super(new BorderLayout());
		this.name = name;
		add(BorderLayout.CENTER, new JLabel("Not implemented yet."));
	}

	
	public String getTabName() {
		return name;
	}

	
	public PdfTweak run(PdfTweak input, OutputProgressDialog outDialog) {
		return input;
	}

}

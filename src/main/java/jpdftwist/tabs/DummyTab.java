package jpdftwist.tabs;

import jpdftwist.core.PDFTwist;
import jpdftwist.gui.dialog.OutputProgressDialog;

import javax.swing.*;
import java.awt.*;

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

	
	public PDFTwist run(PDFTwist input, OutputProgressDialog outDialog) {
		return input;
	}

}

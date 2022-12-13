package jpdftwist.tabs;

import javax.swing.JPanel;

import jpdftwist.core.PDFTwist;

/**
 *
 * @author Vasilis Naskos
 */
public abstract class ActionTab {

	public abstract String getTabName();

	public abstract JPanel getUserInterface();

	public abstract void checkRun();

	public abstract PDFTwist run(PDFTwist input);

}

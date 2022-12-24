package jpdftwist.tabs;

import jpdftwist.core.OutputEventListener;
import jpdftwist.core.PDFTwist;
import jpdftwist.gui.dialog.OutputProgressDialog;

import javax.swing.*;

/**
 * @author Vasilis Naskos
 */
public abstract class ActionTab {

    public abstract String getTabName();

    public abstract JPanel getUserInterface();

    public abstract void checkRun();

    public abstract PDFTwist run(PDFTwist input, OutputEventListener outputEventListener, OutputProgressDialog outputProgressDialog);

}

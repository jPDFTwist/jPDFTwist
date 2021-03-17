package jpdftweak.tabs;

import javax.swing.JPanel;
import jpdftweak.core.PdfTweak;

/**
 *
 * @author Vasilis Naskos
 */
public abstract class ActionTab {

    public abstract String getTabName();

    public abstract JPanel getUserInterface();
    
    public abstract void checkRun();

    public abstract PdfTweak run(PdfTweak input);
    
}

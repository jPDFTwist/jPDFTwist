package jpdftweak.tabs.input.treetable.node.factory;

import com.itextpdf.text.Rectangle;
import java.awt.Color;
import jpdftweak.tabs.input.treetable.node.Node;

/**
 *
 * @author Vasilis Naskos
 */
public abstract class PageNodeFactory {
    
    protected int color;
    protected Rectangle size;
    protected int colorDepth;

    public PageNodeFactory() {
        color = -1;
        colorDepth = -1;
    }
    
    public abstract Node getPageNode(int index);

    public void setSize(Rectangle size) {
        this.size = size;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setColorDepth(int colorDepth) {
        this.colorDepth = colorDepth;
    }
    
}

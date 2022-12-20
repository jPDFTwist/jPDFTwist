package jpdftwist.core.input;

import java.awt.*;

public class VirtualBlankPage {
    private final Color backgroundColor;
    private final PageDimensions pageDimensions;

    public VirtualBlankPage(Color backgroundColor, double width, double height) {
        this.backgroundColor = backgroundColor;
        this.pageDimensions = new PageDimensions(width, height);
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public double getWidth() {
        return pageDimensions.getWidth();
    }

    public double getHeight() {
        return pageDimensions.getHeight();
    }
}

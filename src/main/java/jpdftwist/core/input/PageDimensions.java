package jpdftwist.core.input;

public class PageDimensions {
    private final double width;
    private final double height;

    public PageDimensions(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}

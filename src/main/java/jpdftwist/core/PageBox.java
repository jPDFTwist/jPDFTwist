package jpdftwist.core;

public enum PageBox {

    MediaBox(null),
    CropBox(PageBox.MediaBox),
    BleedBox(PageBox.CropBox),
    TrimBox(PageBox.CropBox),
    ArtBox(PageBox.TrimBox);

    public final PageBox defaultBox;

    PageBox(PageBox defaultBox) {
        this.defaultBox = defaultBox;
    }

    public String getBoxName() {
        return name().substring(0, name().length() - 3).toLowerCase();
    }
}
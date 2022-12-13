package jpdftwist.gui.component.treetable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vasilis Naskos
 */
public enum TreeTableColumn {

    FILE(0, "File"),
    ID(1, "ID"),
    PAPER_SIZE(2, "Paper Size"),
    ORIENTATION(3, "Orientation"),
    COLOR_DEPTH(4, "Color Depth"),
    SIZE(5, "Size"),
    PAGES(6, "Pages"),
    FROM(7, "From"),
    TO(8, "To"),
    EVEN(9, "Include Even"),
    ODD(10, "Include Odd"),
    EMPTY_BEFORE(11, "Empty Before"),
    BOOKMARK_LEVEL(12, "Bookmark Level");

    private final static Map<Integer, TreeTableColumn> CACHE;

    static {
        CACHE = new HashMap<>();
        for (TreeTableColumn value : TreeTableColumn.values()) {
            CACHE.put(value.getIndex(), value);
        }
    }

    private final int index;
    private final String name;

    TreeTableColumn(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public final int getIndex() {
        return index;
    }

    public static TreeTableColumn fromIndex(int value) {
        return CACHE.get(value);
    }

    public String getName() {
        return name;
    }

}

package jpdftweak.tabs.input.treetable;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Vasilis Naskos
 */
public enum UserObjectValue {

    FILE(0, "File"), PAPER_SIZE(1, "Paper Size"), ORIENTATION(2, "Orientation"),
    COLOR_DEPTH(3, "Color Depth"), SIZE(4, "Size"), PAGES(5, "Pages"),
    FROM(6, "From"), TO(7, "To"), EVEN(8, "Include Even"), ODD(9, "Include Odd"),
    EMPTY_BEFORE(10, "Empty Before"), BOOKMARK_LEVEL(11, "Bookmark Level");

    private final static Map<Integer, UserObjectValue> MAPPING
            = new HashMap<Integer, UserObjectValue>();
    
    private final int index;
    private final String name;
        
    private UserObjectValue(int index, String name) {
        this.index = index;
        this.name = name;
    }
    
    public final static void initMap() {
        UserObjectValue[] values = UserObjectValue.values();
        for(UserObjectValue value : UserObjectValue.values()) {
            MAPPING.put(value.getIndex(), value);
        }
    } 

    public final int getIndex() {
        return index;
    }
    
    public final static UserObjectValue fromInt(int value) {
        return MAPPING.get(value);
    }

    public String getName() {
        return name;
    }
    
}

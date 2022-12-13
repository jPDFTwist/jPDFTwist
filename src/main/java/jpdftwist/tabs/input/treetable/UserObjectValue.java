package jpdftwist.tabs.input.treetable;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Vasilis Naskos
 */
public enum UserObjectValue {

	FILE(0, "File"), ID(1, "Paper Size"),PAPER_SIZE(2, "Paper Size"), ORIENTATION(3, "Orientation"), COLOR_DEPTH(4, "Color Depth"),
	SIZE(5, "Size"), PAGES(6, "Pages"), FROM(7, "From"), TO(8, "To"), EVEN(9, "Include Even"), ODD(10, "Include Odd"),
	EMPTY_BEFORE(11, "Empty Before"), BOOKMARK_LEVEL(12, "Bookmark Level");

	private final static Map<Integer, UserObjectValue> MAPPING = new HashMap<Integer, UserObjectValue>();														
	private final int index;
	private final String name;

	private UserObjectValue(int index, String name) {
		this.index = index;
		this.name = name;
	}

	public final static void initMap() {
		UserObjectValue[] values = UserObjectValue.values();
		for (UserObjectValue value : UserObjectValue.values()) {
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

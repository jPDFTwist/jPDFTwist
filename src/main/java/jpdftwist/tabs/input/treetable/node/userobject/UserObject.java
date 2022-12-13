package jpdftwist.tabs.input.treetable.node.userobject;

import jpdftwist.tabs.input.treetable.UserObjectValue;
import java.io.File;

/**
 *
 * @author Vasilis Naskos
 */
public abstract class UserObject {

	protected final String key;
	private final UserObjectType type;

	public UserObject(String key, UserObjectType type) {
		this.key = key;
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public UserObjectType getType() {
		return type;
	}

	public String getFileName() {
		int beginIndex = key.lastIndexOf(File.separatorChar);

		return key.substring(beginIndex + 1);
	}

	public <T extends Object> T getValueAt(UserObjectValue v, Class<T> type) {

		return type.cast(getValueAt(v.getIndex()));
	}

	public abstract Object getValueAt(int column);

	public void setValueAt(Object value, int column) {
		UserObjectValue headerValue = UserObjectValue.fromInt(column);

		setValueAt(value, headerValue);
	}

	public abstract void setValueAt(Object value, UserObjectValue column);
}

package jpdftweak.tabs.input.treetable.node.userobject;

import jpdftweak.core.Utils;
import jpdftweak.tabs.input.treetable.UserObjectValue;

/**
 *
 * @author Vasilis Naskos
 */

public class RealFileUserObject extends FileUserObject {

	private long filesize;

	public RealFileUserObject() {
		super(null, UserObjectType.REAL_FILE, null);
	}

	public RealFileUserObject(String key, SubType subType) {
		super(key, UserObjectType.REAL_FILE, subType);
	}


	public Object getValueAt(int column) {
		UserObjectValue headerValue = UserObjectValue.fromInt(column);

		switch (headerValue) {
		case SIZE:
			return Utils.readableFileSize(filesize);
		default:
			return super.getValueAt(column);
		}
	}


	public void setValueAt(Object value, UserObjectValue column) {
		switch (column) {
		case SIZE:
			filesize = (Long) value;
			break;
		default:
			super.setValueAt(value, column);
		}
	}

}

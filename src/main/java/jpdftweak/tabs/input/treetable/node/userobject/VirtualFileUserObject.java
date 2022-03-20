package jpdftweak.tabs.input.treetable.node.userobject;

import java.io.File;

import jpdftweak.tabs.input.treetable.UserObjectValue;

/**
 *
 * @author Vasilis Naskos
 */

public class VirtualFileUserObject extends FileUserObject {

	private String parent;
	private String srcFilePath;

	public VirtualFileUserObject() {
	}

	public VirtualFileUserObject(String key, SubType subType, String parent) {
		super(key, UserObjectType.VIRTUAL_FILE, subType);
		this.parent = parent;
	}

	
	public Object getValueAt(int column) {
		UserObjectValue headerValue = UserObjectValue.fromInt(column);

		switch (headerValue) {
		case SIZE:
			return "";
		default:
			return super.getValueAt(column);
		}
	}

	public void setSrcFilePath(String srcFilePath) {
		this.srcFilePath = srcFilePath;
	}

	public String getSrcFilePath() {
		return srcFilePath;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getParent() {
		return parent;
	}

	
	public String getKey() {
		if (parent.endsWith(File.separator)) {
			return parent + key;
		}

		return parent + File.separator + key;
	}
}

package jpdftweak.tabs.input.treetable.node.userobject;

import jpdftweak.tabs.input.treetable.node.Node;

/**
 *
 * @author Vasilis Naskos
 */

public enum UserObjectType {
	
//	FOLDER, REAL_FILE, VIRTUAL_FILE, PAGE;
	FOLDER("FOLDER", 0), REAL_FILE("REAL_FILE", 1), VIRTUAL_FILE("VIRTUAL_FILE", 2), PAGE("PAGE", 3), ID("ID", 4);
	
	private UserObjectType(final String s, final int n) {
	}													  
	public static boolean isFile(final Node n) {
		return isFile(n.getUserObject());
	}

	public static boolean isFile(final UserObject uo) {
		return uo.getType() == UserObjectType.REAL_FILE || uo.getType() == UserObjectType.VIRTUAL_FILE;
	}
}
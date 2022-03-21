package jpdftweak.tabs.input.treetable.node.userobject;

import jpdftweak.tabs.input.treetable.UserObjectValue;
import jpdftweak.core.IntegerList;

public abstract class FileUserObject extends UserObject {
	private Object id;
	private final SubType subType;
	private int pages;
	private int from;
	private int to;
	private boolean even;
	private boolean odd;
	private IntegerList emptyBefore;

	public FileUserObject() {
		super((String) null, (UserObjectType) null);
		this.subType = null;
	}

	public FileUserObject(final String key, final UserObjectType type, final SubType subType) {
		super(key, type);
		this.subType = subType;
	}

	public Object getValueAt(final int column) {
		final UserObjectValue headerValue = UserObjectValue.fromInt(column);
		switch (headerValue) {
			case ID : 
			{
				return this.id;
			}
			case FILE : {
				return this.getFileName();
			}
			case PAGES : {
				return this.pages;
			}
			case FROM : {
				return this.from;
			}
			case TO : {
				return this.to;
			}
			case EVEN : {
				return this.even;
			}
			case ODD : {
				return this.odd;
			}
			case EMPTY_BEFORE : {
				return this.emptyBefore;
			}
			default : {
				return "";
			}
		}
	}

	public void setValueAt(final Object value, final UserObjectValue column) {
		switch (column) {
			case ID : 
			{
				this.id = value;
//				this.id = "";
				break;
			}
			case PAGES : {
				this.pages = (int) value;
				break;
			}
			case FROM : {
				this.from = (int) value;
				break;
			}
			case TO : {
				this.to = (int) value;
				break;
			}
			case EVEN : {
				this.even = (boolean) value;
				break;
			}
			case ODD : {
				this.odd = (boolean) value;
				break;
			}
			case EMPTY_BEFORE : {
				this.emptyBefore = (IntegerList) value;
				break;
			}
		}
	}

	public SubType getSubType() {
		return this.subType;
	}

	public enum SubType {
		IMAGE("IMAGE", 0), PDF("PDF", 1), BLANK("BLANK", 2);

		private SubType(final String s, final int n) {
		}
	}
}
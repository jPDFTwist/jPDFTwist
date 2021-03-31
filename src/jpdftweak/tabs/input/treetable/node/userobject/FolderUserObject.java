package jpdftweak.tabs.input.treetable.node.userobject;

import jpdftweak.tabs.input.treetable.UserObjectValue;

 /**
 *
 * @author Vasilis Naskos
 */    

public class FolderUserObject extends UserObject {
	private String id;

	public FolderUserObject() {
		super((String) null, UserObjectType.FOLDER);
	}

	public FolderUserObject(final String key) {
		super(key, UserObjectType.FOLDER);
	}

	public Object getValueAt(final int column) {
		final UserObjectValue headerValue = UserObjectValue.fromInt(column);
		switch (headerValue) {
			case ID : {
				
				return this.id;
			}
			case FILE : {
				final String filename = this.getFileName();
				
//				System.out.println("key:"+this.getKey());
//				System.out.println("filename:"+filename.length());
//				return this.getKey().equals("/") ? this.getKey() : filename;
				
				if(filename.length()==0)
				{
				return this.getKey();
				}
				else
				{
					return filename;
				}				
			}
			default : {
				return "";
			}
		}
	}

	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}

		public void setValueAt(final Object value, final UserObjectValue column) {
		}
	}

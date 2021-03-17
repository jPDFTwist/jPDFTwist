package jpdftweak.utils;

public class ConstantClass {
	private static Integer fileId;
	private static Integer id;
	private static Integer preId;

	static {
		ConstantClass.fileId = -1;
		ConstantClass.id = 0;
		ConstantClass.preId = ConstantClass.id;
	}

	public Integer getId() {
		return ConstantClass.id;
	}

	public static Integer getPreId() {
		return ConstantClass.preId;
	}

	public static void setPreId(final Integer preId) {
		ConstantClass.preId = preId;
	}

	public void setIdtoPreId() {
		ConstantClass.preId = ConstantClass.id;
	}

	public String getPreidAsString(final Integer id) {
		String str = "";
		for (int i = 10; i > id.toString().length(); --i) {
			str = str.concat("0");
		}
		return str.concat(id.toString());
	}

	public void setId(final Integer id) {
		ConstantClass.id = id;
	}

	public String getStringFileId() {
		String str = "";
		++ConstantClass.fileId;
		for (int i = 10; i > ConstantClass.fileId.toString().length(); --i) {
			str = str.concat("0");
		}
		return str.concat(ConstantClass.fileId.toString());
	}

	public String getStringFileId(final Integer id) {
		String str = "";
		ConstantClass.fileId = id;
		++ConstantClass.fileId;
		for (int i = 10; i > ConstantClass.fileId.toString().length(); --i) {
			str = str.concat("0");
		}
		return str.concat(ConstantClass.fileId.toString());
	}

	public String getStringId() {
		String str = "";
		++ConstantClass.id;
		for (int i = 10; i > ConstantClass.id.toString().length(); --i) {
			str = str.concat("0");
		}
		return str.concat(ConstantClass.id.toString());
	}

	public void setFileIdtoId(final Integer fileId) {
		ConstantClass.id = fileId;
	}

	public void setFileIdtoId() {
		ConstantClass.id = ConstantClass.fileId;
	}

	public static Integer getFileId() {
		return ConstantClass.fileId;
	}

	public static void setFileId(final Integer fileId) {
		ConstantClass.fileId = fileId;
	}

	public void setIdtoFileId() {
		ConstantClass.fileId = ConstantClass.id;
	}
}

/*
	DECOMPILATION REPORT
	
	Decompiled with Procyon 0.5.32.
*/
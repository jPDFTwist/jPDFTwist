package jpdftweak.utils;

public class ConstantClass1 {
	private static Integer id;
	private static Integer preId;
	private static Integer fid;

	static {
		ConstantClass1.id = 0;
		ConstantClass1.preId = -1;
		ConstantClass1.fid = 0;
	}

	public Integer getId() {
		return ConstantClass1.id;
	}

	public void setId(final Integer id) {
		ConstantClass1.id = id;
	}

	public Integer getPreId() {
		return ConstantClass1.preId;
	}

	public void setPreId(final Integer preId) {
		ConstantClass1.preId = preId;
	}

	public Integer getFid() {
		return ConstantClass1.fid;
	}

	public void setFid(final Integer fid) {
		ConstantClass1.fid = fid;
	}

	public String getStringAsId() {
		String str = "";
		++ConstantClass1.id;
		for (int i = 10; i > ConstantClass1.id.toString().length(); --i) {
			str = str.concat("0");
		}
		return str.concat(ConstantClass1.id.toString());
	}

	public String getStringAsPreId() {
		String str = "";
		++ConstantClass1.preId;
		for (int i = 10; i > ConstantClass1.preId.toString().length(); --i) {
			str = str.concat("0");
		}
		return str.concat(ConstantClass1.preId.toString());
	}

	public String getIdString(final Integer idString) {
		String str = "";
		for (int i = 10; i > idString.toString().length(); --i) {
			str = str.concat("0");
		}
		return str.concat(idString.toString());
	}
}

/*
	DECOMPILATION REPORT
	
	Decompiled with Procyon 0.5.32.
*/
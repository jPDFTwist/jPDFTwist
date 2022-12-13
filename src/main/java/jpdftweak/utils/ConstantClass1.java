package jpdftweak.utils;

public class ConstantClass1 {
	private static Integer id;

	static {
		ConstantClass1.id = 0;
	}

	public Integer getId() {
		return ConstantClass1.id;
	}

	public void setId(final Integer id) {
		ConstantClass1.id = id;
	}

	public String getStringAsId() {
		String str = "";
		++ConstantClass1.id;
		for (int i = 10; i > ConstantClass1.id.toString().length(); --i) {
			str = str.concat("0");
		}
		return str.concat(ConstantClass1.id.toString());
	}
}

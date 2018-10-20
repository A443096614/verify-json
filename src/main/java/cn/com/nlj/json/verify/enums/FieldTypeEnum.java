package cn.com.nlj.json.verify.enums;

/***
* 类说明：
* @author nlj
* 2018年10月15日 下午11:06:18
*/
public enum FieldTypeEnum {
	
	FIELD_OBJ("obj", "对象"),
	FIELD_ARRAY("array", "数组");

	FieldTypeEnum(String value, String memo){
		this.value = value;
		this.memo = memo;
	}
	
	public static FieldTypeEnum getEnumByValue(String value) {
		FieldTypeEnum[] valueOf = FieldTypeEnum.values();
		for (FieldTypeEnum typeEnum : valueOf) {
			if (typeEnum.getValue().equals(value)) {
				return typeEnum;
			}
		}
		return null;
	}
	
	private String value;
	
	private String memo;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	
}

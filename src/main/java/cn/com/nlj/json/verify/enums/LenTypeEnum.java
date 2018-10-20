package cn.com.nlj.json.verify.enums;

/**
 * 类说明：
 * 
 * @date 2018年10月15日 下午4:58:38
 * @author ninglj
 *
 */
public enum LenTypeEnum {
	
	SINGLE_BYTE("0", "一个汉字的长度算一位"),
	MULTIPLE_BYTE("1", "一个汉字的长度算两位");

	LenTypeEnum(String value, String memo){
		this.value = value;
		this.memo = memo;
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

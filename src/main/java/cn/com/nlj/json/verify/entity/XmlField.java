package cn.com.nlj.json.verify.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.nlj.json.verify.enums.FieldTypeEnum;
import cn.com.nlj.json.verify.enums.LenTypeEnum;
import lombok.Data;

/**
 * 类说明：xml对应字段
 * 
 * @date 2018年10月15日 下午12:26:08
 * @author ninglj
 *
 */
@Data(staticConstructor="XmlFieldFactory")
public class XmlField {

	private String name;//字段名
	private String isNull;//是否为空
	private String isValid;//是否教验以下属性
	private String fieldType = FieldTypeEnum.FIELD_OBJ.getValue();//字段属性类型；默认为对象字符串
	private String preName;//前置条件的字段名
	private String preValue;//前置的值
	private int maxLength = Integer.MAX_VALUE;//最大长度
	private int minLength = 0;//最小长度
	private String lenType = LenTypeEnum.SINGLE_BYTE.getValue();//默认一个汉字算一位
	private BigDecimal maxValue;//最大值，比如金额
	private BigDecimal minValue;//最小值
	private List<String> contains = new ArrayList<>();//范围值，当A属性为value时，B属性只能为该值内的值
	private String memo;//备注
	private Map<String, XmlField> preconditions = new HashMap<>();//前置条件
	private String linkTemplateId;//为对象时字段对应的对象属性的验证
	
}

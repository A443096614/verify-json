package cn.com.nlj.json.verify;
/**
 * 类说明：验证json数据
 * @date 2018年10月15日 下午2:40:15
 * @author ninglj
 *
 */

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.com.nlj.json.verify.entity.XmlField;
import cn.com.nlj.json.verify.enums.FieldTypeEnum;
import cn.com.nlj.json.verify.enums.LenTypeEnum;

public class VerifyManager {

	/***
	 * 返回未验证成功的字段
	 * @param xmlId
	 * @param jsonType json类型：对象或数组
	 * @param json
	 * @return null:验证通过
	 */
	public static Map<String, String> verify(String xmlId, String jsonStr, FieldTypeEnum fieldTypeEnum){
		Map<String, String> map = new HashMap<>();
		checkXmlIdAndJson(xmlId, jsonStr, fieldTypeEnum, map);
		if (map.size() > 0) {
			return map;
		}
		return null;
	}

	private static Map<String, String> checkXmlIdAndJson(String xmlId, String jsonStr, FieldTypeEnum fieldTypeEnum, Map<String, String> map) {
		Map<String, XmlField> xmlFieldMap = ParseXml.getXmlFieldList(xmlId);
		if (xmlFieldMap == null) {
			map.put("message", "传入的xmlId未定义，请检查传的值");
			return map;
		}
		JSONArray jsonArray = new JSONArray();
		try {
			//json格式为对象或字符的
			String jsonType = fieldTypeEnum.getValue();
			if (FieldTypeEnum.FIELD_OBJ.getValue().equals(jsonType)) {
				JSONObject jsonObj = JSON.parseObject(jsonStr);
				jsonArray.add(jsonObj);
			} else if(FieldTypeEnum.FIELD_ARRAY.getValue().equals(jsonType)) {
				jsonArray = JSON.parseArray(jsonStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("message", "xmlId["+xmlId+"]传入的json格式有问题，请检查传的值");
			return map;
		}
		checkField(xmlFieldMap, jsonArray, map);
		return map;
	}
	
	private static void checkField(Map<String, XmlField> xmlFieldMap, final JSONArray jsonArray, Map<String, String> map) {
		
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			for (Map.Entry<String, XmlField> entry : xmlFieldMap.entrySet()) {
				String field = entry.getKey();
				XmlField xmlField = entry.getValue();
				boolean conditionFlag = false;//是否存在前置条件
				Map<String, XmlField> preconditionMap = xmlField.getPreconditions();//前置条件
				//判断是否存在前置条件
				if (!preconditionMap.isEmpty()) {
					conditionFlag = true;
				}
				String value = String.valueOf(jsonObj.get(field));
				if (conditionFlag) {//存在前置条件
					boolean checkFlag = true;//是否没有与前置条件值相等
					for (Map.Entry<String, XmlField> preEntry : preconditionMap.entrySet()) {
						String checkValue = String.valueOf(jsonObj.get(preEntry.getKey()));//前置条件的值
						XmlField preXmlField = preEntry.getValue();
						if (checkValue.equals(preXmlField.getPreValue())) {//前置条件的值与设定的前置条件值是否相等，相等则判断
							checkFlag = false;
							preXmlField.setMemo(xmlField.getMemo());
							validationConCondition(preXmlField, field, value, map, true);
						}
					}
					if (checkFlag) {
						validationConCondition(xmlField, field, value, map, false);
					}
				} else {
					validationConCondition(xmlField, field, value, map, false);
				}
			}
		}
	}
	
	
	private static void validationConCondition(XmlField xmlField, String field, String value, Map<String, String> map, boolean isPrecondition) {
		String memo = xmlField.getMemo();
		if (!isPrecondition) {//不为前置条件时判断是否允许为空
			boolean isNull = Boolean.valueOf(xmlField.getIsNull());//是否能为空，false不允许
			if(isEmpty(value) && !isNull) {//值为空或json对应的key不存在且不允许为空
				map.put(field, memo + "不能为空");
				return;
			}
			if (isEmpty(value)) {//允许为空且值为空时
				return;
			}
		}
		
		boolean isValid = isPrecondition == true ? true : Boolean.valueOf(xmlField.getIsValid());
		if (isValid) {//验证其它约束，如长度、最大值等
			String linkTemplateId = xmlField.getLinkTemplateId();
			if (isNotEmpty(linkTemplateId)) {//是否存在对象属性验证
				checkXmlIdAndJson(linkTemplateId, value, FieldTypeEnum.getEnumByValue(xmlField.getFieldType()), map);
			} else {
				List<String> list = xmlField.getContains();
				if (!list.isEmpty()) {
					if (list.contains(value)) {//满足时其它条件不用判断了
						return;
					}
					map.put(field, memo + "只能为"+ list);
					return;
				}
				int maxLen = xmlField.getMaxLength();
				int minLen = xmlField.getMinLength();
				BigDecimal maxValue = xmlField.getMaxValue();
				BigDecimal minValue = xmlField.getMinValue();
				int length = 0;
				//一个汉字算一个长度
				if(LenTypeEnum.SINGLE_BYTE.getValue().equals(xmlField.getLenType())) {
					length = value.length();
				} else {
					//一个汉字算两个长度
					String str = value.replaceAll("[^\\x00-\\xff]", "**");;
					length = str.length();
				}
				boolean validFlag = (length >= minLen && length <= maxLen);
				if (!validFlag) {//长度验证不通过
					map.put(field, memo + "长度必须在[" + minLen + "~" + maxLen + "]之间");
					return;
				}
				if (maxValue != null) {
					int v = maxValue.compareTo(new BigDecimal(value));
					if (v < 0) {//值大于最大值
						map.put(field, memo + "值大于最大值"+maxValue);
						return;
					}
				}
				if (minValue != null) {
					int v = minValue.compareTo(new BigDecimal(value));
					if (v > 0) {//值小于最小值
						map.put(field, memo + "值小于最小值"+maxValue);
						return;
					}
				}
			}
		}
	}
	
	/**
	 * 判断字符串不为空
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(Object obj) {
		String str = (obj == null) ? null : obj.toString();
		boolean isNotEmpty = false;
		if (str != null && !str.trim().equals("") && !str.trim().equalsIgnoreCase("NULL")) {
			isNotEmpty = true;
		}
		return isNotEmpty;
	}
	
	/**
	 * 判断字符串为空
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(Object obj) {
		return !isNotEmpty(obj);
	}
}

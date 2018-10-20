package cn.com.nlj.json.verify;
/**
 * 类说明：
 * @date 2018年10月15日 下午2:42:11
 * @author ninglj
 *
 */

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.com.nlj.json.verify.entity.XmlField;
import cn.com.nlj.json.verify.enums.FieldTypeEnum;
import cn.com.nlj.json.verify.enums.LenTypeEnum;
import cn.com.nlj.json.verify.exception.ParseXmlException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParseXml {
	
	
	private static final Map<String, Map<String, XmlField>> map = new HashMap<>();
	
	private ParseXml() {}
	
	static {
		try {
			loadParseXml();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("parse xml error");
		}
	}
	
	//解析xml
	private static void loadParseXml() throws Exception {
		SAXReader reader = new SAXReader();
		String path = Thread.currentThread().getContextClassLoader().getResource("verif-xml/").getPath();
		File rootDir = new File(path);
		String[] fileList = rootDir.list();
		Document document = null;
		for (int i = 0; i < fileList.length; i++) {
			//通过reader对象的read方法加载path目录下的文件,获取docuemnt对象。
			try {
				document = reader.read(new File(rootDir.getAbsolutePath() + "/" + fileList[i]));
				// 通过document对象获取根节点
				Element root = document.getRootElement();
				//获取对象节点
				List<Element> elements = root.elements();
				for (Iterator<Element> iterator = elements.iterator(); iterator.hasNext();) {
					Element element = (Element) iterator.next();
					Map<String, XmlField> fieldMap = getConfigField(element.elements(), false);
					map.put(element.attributeValue("id"), fieldMap);
				}
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
	}
	
	/***
	 * 获取字段验证规则
	 * @param fieldElements 字段规则
	 * @param isPrecondition 是否为前置条件：false不为，否则反
	 * @return
	 * @throws Exception 
	 */
	private static Map<String, XmlField> getConfigField(List<Element> fieldElements, boolean isPrecondition) throws Exception{
		Map<String, XmlField> fieldMap = new HashMap<>();
		XmlField xjObj = null;
		for (Iterator<Element> iterator = fieldElements.iterator(); iterator.hasNext();) {
			xjObj = XmlField.XmlFieldFactory();
			Element fieldElement = (Element) iterator.next();
			List<Attribute> fieldAttributes = fieldElement.attributes();
			for (Iterator<Attribute> fieldAttr = fieldAttributes.iterator(); fieldAttr.hasNext();) {
				Attribute attr = (Attribute) fieldAttr.next();
				String fieldName = attr.getName();
				if ("contains".equals(fieldName)) {
					List<String> asList = Arrays.asList(fieldElement.attributeValue(fieldName).split(","));
					BeanUtils.setProperty(xjObj, fieldName, asList);
				} else {
					BeanUtils.setProperty(xjObj, fieldName, fieldElement.attributeValue(fieldName));
				}
			}
			
			List<Element> preElement = fieldElement.elements();//获取前置条件
			if (preElement.size() > 0) {//存在前置条件
				BeanUtils.setProperty(xjObj, "preconditions", getConfigField(preElement, true));
			}
			boolean fieldTypeFlag = false;//fieldType值不在枚举内
			FieldTypeEnum[] fieldTypeEnum = FieldTypeEnum.values();
			for (FieldTypeEnum typeEnum : fieldTypeEnum) {
				fieldTypeFlag = typeEnum.getValue().equals(xjObj.getFieldType());
				if (fieldTypeFlag) {//符合则跳出当前循环
					break;
				}
			}
			if (!fieldTypeFlag) {
				throw new ParseXmlException("fieldType 值不符合");
			}
			
			boolean lenTypeFlag = false;//fieldType值不在枚举内
			LenTypeEnum[] lenTypeEnum = LenTypeEnum.values();
			for (LenTypeEnum typeEnum : lenTypeEnum) {
				lenTypeFlag = typeEnum.getValue().equals(xjObj.getLenType());
				if (lenTypeFlag) {//符合则跳出当前循环
					break;
				}
			}
			if (!lenTypeFlag) {
				throw new ParseXmlException("lenTypeFlag 值不符合");
			}
			if (isPrecondition) {//为前置条件时以preName作为key
				fieldMap.put(xjObj.getPreName(), xjObj);
			} else {
				fieldMap.put(xjObj.getName(), xjObj);
			}
		}
		return fieldMap;
	}
	
	/***
	 * 根据模板Id获取Xml教验格式
	 * @param xmlId
	 * @return
	 */
	public static Map<String, XmlField> getXmlFieldList(String xmlId){
		return map.get(xmlId);
	}
}

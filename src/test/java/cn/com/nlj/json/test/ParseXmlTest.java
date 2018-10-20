package cn.com.nlj.json.test;

import java.util.Map;

import org.junit.Test;

import cn.com.nlj.json.verify.ParseXml;
import cn.com.nlj.json.verify.VerifyManager;
import cn.com.nlj.json.verify.enums.FieldTypeEnum;

/***
* 类说明：
* @author nlj
* 2018年10月15日 下午9:57:54
*/
public class ParseXmlTest {

	@Test
	public void test() {
		ParseXml.getXmlFieldList("demo").forEach((k, v) -> {
			System.err.println("key: "+ k + " value: "+ v);
		});
	}
	
	@Test
	public void json() {
		String json = "{\"name\": \"Michael\", \"type\" : \"12\", \"status\" : \"8\", \"amt\":\"50\", \"obj\" : {\"aa\":\"cc\"}}";
		Map<String, String> verify = VerifyManager.verify("demo", json, FieldTypeEnum.FIELD_OBJ);
		verify.forEach((k, v) -> {
			System.err.println(v);
		});
	}
}

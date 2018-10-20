package cn.com.nlj.json.verify.exception;
/***
* 类说明：
* @author nlj
* 2018年10月17日 下午10:50:17
*/
public class ParseXmlException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3248788793179736383L;
	
	public ParseXmlException() {
		super();
	}

	public ParseXmlException(String message) {
		super(message);
	}
	
    public ParseXmlException(String message, Throwable cause) {
        super(message, cause);
    } 

}

/**
 * 
 */
package org.groupfio.etl.process.test;

import org.ofbiz.base.util.Debug;

/**
 * @author Group Fio
 *
 */
public class GeneralTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			
			String input = "kkkkkkkkk";
			
			//input = StringUtil.
			
			/*String propertyValue = "{\"firstString\": \"replaceString\"}";
			
			JSON jsonFeed = JSON.from(propertyValue);
			
			JSONToMap jsonMap = new JSONToMap();
			Map<String, Object> dataMap = jsonMap.convert(jsonFeed);*/
			
			/*String propertyValue = "Hellow";
			System.out.println(propertyValue.length());
			
			int i = 0;*/
			
			/*String cellValue = "XML_A_001";
			
			//System.out.println( cellValue.substring(0, 8) );
			Debug.log(cellValue.substring(0, 8));
			
			String encodeString = "Basic YWRtaW46b2ZiaXo=";
			
			encodeString = encodeString.substring(encodeString.lastIndexOf(" ")+1);
			
			String decodedStr = Base64.base64Decode(encodeString);
			
			System.out.println(decodedStr);*/
			
			/*String dateString = UtilDateTime.timeStampToString(UtilDateTime.nowTimestamp(), "MM/dd/YYYY HH:MM", TimeZone.getDefault(), null);
			
			//System.out.println(dateString);
			Debug.log(dateString);*/
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage());
		}
		
	}

}

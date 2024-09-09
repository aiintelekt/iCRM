/**
 * 
 */
package org.groupfio.common.portal.extractor.data;

import java.util.Map;

/**
 * @author Sharif
 *
 */
public abstract class DataExtractor implements Data {

	protected Data extractedData;
	
	public DataExtractor() {};
	
	public DataExtractor(Data extractedData){
		this.extractedData = extractedData;
	}
	
	@Override
	public Map<String, Object> retrieve(Map<String, Object> context) {
		return extractedData.retrieve(context);
	}

}

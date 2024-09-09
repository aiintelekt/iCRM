/**
 * 
 */
package org.groupfio.dyna.screen.filter;

import java.util.List;

import org.ofbiz.entity.GenericValue;

/**
 * @author Sharif
 *
 */
public interface Filter {

	public List<GenericValue> filter(List<GenericValue> dataList);
	
}

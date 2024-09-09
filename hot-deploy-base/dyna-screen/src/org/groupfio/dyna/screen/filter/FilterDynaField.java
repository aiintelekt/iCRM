/**
 * 
 */
package org.groupfio.dyna.screen.filter;

import java.util.ArrayList;
import java.util.List;

import org.fio.homeapps.util.UtilPermission;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;

/**
 * @author Sharif
 *
 */
public class FilterDynaField implements Filter {
	
	private GenericValue userLogin;
	private Delegator delegator;
	
	public FilterDynaField(GenericValue userLogin, Delegator delegator) {
		this.userLogin = userLogin;
		this.delegator = delegator;
	}

	@Override
	public List<GenericValue> filter(List<GenericValue> dataList) {
		List<GenericValue> filteredData = new ArrayList<GenericValue>();
		if (UtilValidate.isNotEmpty(dataList)) {
			for (GenericValue data : dataList) {
				if (UtilValidate.isEmpty(data.getString("roleTypeId")) || UtilPermission.hasRole(delegator, userLogin, data.getString("roleTypeId"))) {
					filteredData.add(data);
				}
			}
		}
		return filteredData;
	}

}

/*
 * Copyright (c) Open Source Strategies, Inc.
 *
 * Opentaps is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Opentaps is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Opentaps.  If not, see <http://www.gnu.org/licenses/>.
 */
/* Copyright (c) Open Source Strategies, Inc. */

/*
 *  Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.fio.crm.common;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.fio.crm.util.LoginFilterUtil;
import org.fio.crm.util.UtilMessage;

/**
 * DataSource services. The service documentation is in services_datasource.xml.
 */
public final class DataSourceServices {

	private DataSourceServices() { }

	private static final String MODULE = DataSourceServices.class.getName();

	public static Map<String, Object> addAccountDataSource(DispatchContext dctx, Map<String, Object> context) {
		return addDataSourceWithPermission(dctx, context, "CRMSFA_ACCOUNT", "_UPDATE");
	}

	public static Map<String, Object> addLeadDataSource(DispatchContext dctx, Map<String, Object> context) {
		return addDataSourceWithPermission(dctx, context, "CRMSFA_LEAD", "_UPDATE");
	}

	/**
	 * Parametrized service to add a data source to a party. Pass in the security to check.
	 */
	private static Map<String, Object> addDataSourceWithPermission(DispatchContext dctx, Map<String, Object> context, String module, String operation) 
	{
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Delegator delegator = dctx.getDelegator();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		String partyId = (String) context.get("partyId");
		String dataSourceId = (String) context.get("dataSourceId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");

		// check parametrized security
		String userLoginId = userLogin.getString("partyId");
		if (LoginFilterUtil.checkEmployeePosition(delegator, userLoginId) && !security.hasEntityPermission(module, operation, userLogin)) {
			return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
		}
		try {
			// create the PartyDataSource to relate the optional data source to this party
			Map<String, Object> serviceResults = dispatcher.runSync("createPartyDataSource", UtilMisc.toMap("partyId", partyId , "dataSourceId", dataSourceId, "userLogin", userLogin, "fromDate", fromDate));
			if (ServiceUtil.isError(serviceResults)) {
				return UtilMessage.createAndLogServiceError(serviceResults, "CrmErrorAddDataSource", locale, MODULE);
			}
		} catch (GenericServiceException e) {
			return UtilMessage.createAndLogServiceError(e, "CrmErrorAddDataSource", locale, MODULE);
		}
		return ServiceUtil.returnSuccess();
	}

	public static Map<String, Object> removeAccountDataSource(DispatchContext dctx, Map<String, Object> context) {
		return removeDataSourceWithPermission(dctx, context, "CRMSFA_ACCOUNT", "_UPDATE");
	}

	public static Map<String, Object> removeLeadDataSource(DispatchContext dctx, Map<String, Object> context) {
		return removeDataSourceWithPermission(dctx, context, "CRMSFA_LEAD", "_UPDATE");
	}

	/**
	 * Parametrized method to remove a data source from a party. Pass in the security to check.
	 * TODO: this isn't implemented until necessary
	 */
	private static Map<String, Object> removeDataSourceWithPermission(DispatchContext dctx, Map<String, Object> context, String module, String operation) {
		Security security = dctx.getSecurity();
		Delegator delegator =  dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		String partyId = (String) context.get("partyId");
		String userLoginId = userLogin.getString("partyId");
		// check parametrized security
		if (LoginFilterUtil.checkEmployeePosition(delegator, userLoginId) && !security.hasEntityPermission(module, operation, userLogin)) {
			return UtilMessage.createAndLogServiceError("CrmErrorPermissionDenied", locale, MODULE);
		}

		String dataSourceId = (String) context.get("dataSourceId");
		Timestamp fromDate = (Timestamp) context.get("fromDate");
		try {
			GenericValue partyDataSource = delegator.findOne("PartyDataSource", UtilMisc.toMap("partyId", partyId, "dataSourceId", dataSourceId, "fromDate", fromDate),false);
			if(UtilValidate.isEmpty(partyDataSource))
			{
				return UtilMessage.createAndLogServiceError("Data source "+dataSourceId+" Not Found for the party "+partyId, locale, MODULE);
			}
			else{
				partyDataSource.remove();
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
}

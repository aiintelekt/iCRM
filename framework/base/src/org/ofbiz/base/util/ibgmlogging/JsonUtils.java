package org.ofbiz.base.util.ibgmlogging;

import com.google.gson.Gson;

public class JsonUtils {

	private static final I3beLogService LOG = LogFactory.getLog(JsonUtils.class);
	
	public static final String DEFAULT_RESPONSE_JSON_STRING = "{   \"header\":{   \"msgId\":\"\", \"timeStamp\":\"\" }, \"txnResponse\":{ \"clientRecordRefId\":\"\",   \"txnType\":\"\",  \"walletAcctId\":\"\", \"responseCode\":\"\",  \"responseDesc\":\"\", \"walletAmt\" : \"\"} }";
	

	public static String convertRequestJson(final Object request) {
		final Gson gson = new Gson();
		String json = null;
		try {
			json = gson.toJson(request);
		} catch (final Exception e) {
			LOG.info("JsonProcessingException " + e.getStackTrace());
			json = JsonUtils.DEFAULT_RESPONSE_JSON_STRING;
		}
		return json;
	}
	
	
}

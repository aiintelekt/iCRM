/**
 * 
 */
package org.fio.homeapps.rest.client.parser;

import java.util.Map;

import org.fio.homeapps.rest.client.model.Token;
import org.fio.homeapps.util.ParamUtil;
import org.ofbiz.base.util.UtilValidate;

/**
 * @author sharif
 *
 */
public class TokenParser {

	public static Token parseToken(Map<String, Object> response) {
		
		Token token = new Token();
		
		try {
		
			if (UtilValidate.isEmpty(response)) {
				return token;
			}
		
			String accessToken = ParamUtil.getString(response, "access_token");
			String tokenType = ParamUtil.getString(response, "token_type");
			long expiresIn = ParamUtil.getLong(response, "expires_in");
			String refreshToken = ParamUtil.getString(response, "refresh_token");
			
			token.setAccessToken(accessToken);
			token.setTokenType(tokenType);
			token.setExpiresIn(expiresIn);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return token;
		
	}
	
}

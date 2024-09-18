/**
 * 
 */
package org.groupfio.etl.process.client;

import org.groupfio.etl.process.client.ClientConstants.ChannelAccessType;
import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

/**
 * @author Group Fio
 *
 */
public class ClientUtil {

	public static WalletPartyClient getWalletPartyClient(Delegator delegator, GenericValue channelAccess) {
    	
    	try {
    		WalletPartyClient client = new WalletPartyClient(delegator, channelAccess, ChannelAccessType.ACCESS_TYPE_PARTY);
			
			return client;
		} catch (Exception e) {
			Debug.log("Exception in getWalletPartyClient==="+e.getMessage());
		}
    	
		return null;
		
    }
	
	public static WalletWalletClient getWalletWalletClient(Delegator delegator, GenericValue channelAccess) {
    	
    	try {
    		WalletWalletClient client = new WalletWalletClient(delegator, channelAccess, ChannelAccessType.ACCESS_TYPE_WALLET);
			
			return client;
		} catch (Exception e) {
			Debug.log("Exception in getWalletWalletClient==="+e.getMessage());
		}
    	
		return null;
		
    }
	
}

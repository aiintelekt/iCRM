<#if OrderByIdViewList?exists>
	<div>&nbsp;</div>
	<div class="card-head margin-adj dash-panel">
		<h2>
		     Contact Information
		</h2>
	</div>   
	<div class='dash-panel'>
	   <table width="100%" border="0" cellpadding="1" cellspacing="0">
	  <tbody>
	  <tr>
	    <td align="right" valign="top" width="15%">
	     <div class="tabletext" >&nbsp;<b>Customer</b></div>
	   	</td>
	    <td width="5" >&nbsp;</td>
	    <td align="left" valign="top" width="80%">
	          <div class="tabletext" >
	              <span>${partyName}(${partyIdVal})</span>             
	          </div>
	    </td>
	  </tr>
			
	  <tr>
	    <td align="right" valign="top" width="15%">
	      <div class="tabletext" >&nbsp;<b>Contact</b></div>
	    </td>
	    <td width="5" >&nbsp;</td>
	    <td align="left" valign="top" width="80%">
	          <div class="tabletext">
	          </div>          
	    </td>
	  </tr>
	
	  <tr><td colspan="7" ><hr class="sepbar" ></td></tr>
	  <tr>
	    <td align="right" valign="top" width="15%">
	      <div class="tabletext" >&nbsp;<b >Email</b></div>
	    </td>
	    <td width="5" >&nbsp;</td>
	    
	     <#if partyEmail?has_content>
	    <td align="left" valign="top" width="80%">
	    <div class="tabletext" >&nbsp;<b>${partyEmail?if_exists}</b></div>
	    
	    <#else>
	    <td align="left" valign="top" width="80%">
	    <div class="tabletext" >&nbsp;<b>no records found</b></div>
	    </#if>
	    </td>
	  </tr>
	 </tbody>
	</table>
	  <table width="100%" border="0" cellpadding="1" cellspacing="0">
	    <tbody>
	     <tr>
	     	<td colspan="7">
	     	</td>
	     </tr>
	  </tbody>
	  </table>
	  <table width="100%" border="0" cellpadding="1" cellspacing="0">
	      <tr><td colspan="7">&nbsp;</td></tr>
	      <tr>
	         <td align="right" valign="top" width="15%">
	              <div class="tabletext"> Phone Number</div>
	         </td>                   
	         <td width="5">&nbsp;</td>
	         <td align="left" valign="top" width="80%">
	         	<div class="tabletext">
	         		${primaryPhoneNumber?if_exists}
	         	</div>
	        </td>
	      </tr>
	  </table>
	  <table width="100%" border="0" cellpadding="1" cellspacing="0">
	    <tr><td colspan="7">&nbsp;</td></tr>
	  </table>
	
	</div>
</#if> 

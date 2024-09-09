<div class="screenlet-title-bar">
<ul><li class="h3">SFTP Configuration</li>
</ul><br class="clear"></div>
<form action="<@ofbizUrl>sftpConfig</@ofbizUrl>" name="sftpConfig" method="post" id="sftpConfig">
	<input type="hidden" name="sequenceId"  value="${(sftpConfig.seqId)?if_exists}"/>	
		<table>
	 		<tr>
				<td class="label">User Name</td>
				<td>
					<input type="text" name="userName" id="userName" value="${(sftpConfig.userName)?if_exists}"/>
				</td>
			</tr>
			<tr>
				<td class="label">Password</td>
				<td>
					<input type="text" name="password" id="password" value="${(sftpConfig.password)?if_exists}"/>
				</td>
			</tr>
			<tr>
				<td class="label">Port</td>
				<td>
					<input type="text" name="port" id="port" value="${(sftpConfig.port)?if_exists}"/>
				</td>
			</tr>
			<tr>
				<td class="label">Host</td>
				<td>
					<input type="text" name="host" id="host" value="${(sftpConfig.host)?if_exists}"/>
				</td>
			</tr>
			<tr>
			<td></td>
				<td><input type="submit" value="Submit"/></td>
			</tr>
		</table>
		</form>
		
<div class="screenlet-title-bar">
<ul><li class="h3">SFTP Configuration List</li>
</ul><br class="clear"></div>
		<table style="width:100%">
		<thead>
		<tr>
		  <td style="font-size: 12px;"><b>Id</b></td>
		   <td style="font-size: 12px;"><b>UserName</b></td>
		   <td style="font-size: 12px;"><b>Password</b></td>
		   <td style="font-size: 12px;"><b>Port</b></td>
		   <td style="font-size: 12px;"><b>Host</b></td>
		   <td style="font-size: 12px;"><b>Edit</b></td>
		 </tr>
		 </thead>
	     <tbody>
			<#if sftpConfigurations?exists && sftpConfigurations?has_content>
				<#list sftpConfigurations as sftpConfiguration>
					<tr>
					 <td>${sftpConfiguration.seqId?if_exists}</td>
					  <td>${sftpConfiguration.userName?if_exists}</td>
					  <td>${sftpConfiguration.password?if_exists}</td>
					  <td>${sftpConfiguration.port?if_exists}</td>
					  <td>${sftpConfiguration.host?if_exists}</td>
                        <td><a href="<@ofbizUrl>sftpConfiguration?seqId=${sftpConfiguration.seqId?if_exists}</@ofbizUrl>">Edit</a></td>
					 </tr>
				</#list>
			</#if>
	     </tbody>
	     </table>
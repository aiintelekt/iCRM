package org.groupfio.custom.field.test;

import java.io.File;
import java.io.FileInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.groupfio.custom.field.constants.CustomFieldConstants;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilXml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Sharif
 *
 */
public class OtherTest {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			String groupId = "Segment_code_04";
			
			String modelSqlPath = "D:/work/fio-dev-dbs-crm/hot-deploy/custom-field/data/";
			
			String entityModelSegSql = "CREATE TABLE  custom_field_seg_${groupId} (GROUP_ID varchar(20) NOT NULL,CUSTOM_FIELD_ID varchar(20) NOT NULL,PARTY_ID varchar(20) NOT NULL,GROUP_ACTUAL_VALUE longtext,INCEPTION_DATE datetime DEFAULT NULL,LAST_UPDATED_STAMP datetime DEFAULT NULL,LAST_UPDATED_TX_STAMP datetime DEFAULT NULL,CREATED_STAMP datetime DEFAULT NULL,CREATED_TX_STAMP datetime DEFAULT NULL,PRIMARY KEY (GROUP_ID,CUSTOM_FIELD_ID,PARTY_ID),KEY CSM_SGT_CD_04_TP (LAST_UPDATED_TX_STAMP),KEY CSM_SGT_CD_04_TS (CREATED_TX_STAMP)) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
			
			entityModelSegSql = entityModelSegSql.replace("${groupId}", groupId.toLowerCase());
			
			entityModelSegSql = FileUtil.readString("UTF-8", new File(modelSqlPath+"dynamic-entitymodel.sql")) + "\r\t" + entityModelSegSql; 
			
			System.out.println(entityModelSegSql);
			
			FileUtil.writeString(modelSqlPath, "dynamic-entitymodel.sql", entityModelSegSql);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}

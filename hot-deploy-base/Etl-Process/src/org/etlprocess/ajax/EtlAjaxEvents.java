package org.etlprocess.ajax;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;


public class EtlAjaxEvents {

    public static final String module = EtlAjaxEvents.class.getName();

	public EtlAjaxEvents() {
		
	}
	
public static void updateProductMapping(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
		PrintWriter out = response.getWriter();
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        try{
        HttpSession session = request.getSession(true);
        String DESCRIPTION_1 = (String)request.getParameter("DESCRIPTION_1");
        String DESCRIPTION_2 = (String)request.getParameter("DESCRIPTION_2");
        String DESCRIPTION_3 = (String)request.getParameter("DESCRIPTION_3");
        String DESCRIPTION_4 = (String)request.getParameter("DESCRIPTION_4");
        String DESCRIPTION_5 = (String)request.getParameter("DESCRIPTION_5");
        String etlModelId = (String)request.getParameter("modelId");
        String desc[]={DESCRIPTION_1,DESCRIPTION_2,DESCRIPTION_3,DESCRIPTION_4,DESCRIPTION_5};
        for(int i=1;i<=5;i++)
        {
        	GenericValue gv=(GenericValue)delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PRODUCT","dmStgColName","DESCRIPTION"+i),false);
        	if(UtilValidate.isNotEmpty(gv))
            {
        	gv.set("dmTypeValue", desc[i-1]);
        	gv.store();
            }else{
            	gv=(GenericValue)delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PRODUCT","dmStgColName","DESCRIPTION"+i));
               	gv.set("dmTypeValue", desc[i-1]);
            	gv.create();
            }
        }
        Debug.log("Success");
        }
        catch(GenericEntityException e)
        {
             Debug.logError(validate(e.getMessage()), module);
        }
        
    }
// Logic For Updating Product Image Mapping 
public static void updateProductImageMapping(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
	PrintWriter out = response.getWriter();
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    try{
    HttpSession session = request.getSession(true);
    String smallImage = (String)request.getParameter("smallImage");
    String mediumImage = (String)request.getParameter("mediumImage");
    String largeImage = (String)request.getParameter("largeImage");
    String detailImage = (String)request.getParameter("detailImage");
    String etlModelId = (String)request.getParameter("modelId");
    GenericValue smallImagegv=(GenericValue)delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PRODUCT","dmStgColName","IMAGE_URL_PREFIX_SMALL"),false);
    if(UtilValidate.isNotEmpty(smallImagegv))
    {
    	smallImagegv.set("dmTypeValue",smallImage);
    	smallImagegv.store();
    }else{
    	smallImagegv=(GenericValue)delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PRODUCT","dmStgColName","IMAGE_URL_PREFIX_SMALL"));
    	smallImagegv.set("dmTypeValue",smallImage);
    	smallImagegv.create();	
    }
    GenericValue mediumImagegv=(GenericValue)delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PRODUCT","dmStgColName","IMAGE_URL_PREFIX_MEDIUM"),false);
    if(UtilValidate.isNotEmpty(mediumImagegv))
    {
    	mediumImagegv.set("dmTypeValue",mediumImage);
    	mediumImagegv.store();
    }else{
    	mediumImagegv=(GenericValue)delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PRODUCT","dmStgColName","IMAGE_URL_PREFIX_MEDIUM"));
    	mediumImagegv.set("dmTypeValue",mediumImage);
    	mediumImagegv.create();
    }
    GenericValue largeImagegv=(GenericValue)delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PRODUCT","dmStgColName","IMAGE_URL_PREFIX_LARGE"),false);
    if(UtilValidate.isNotEmpty(largeImagegv))
    {
    	largeImagegv.set("dmTypeValue",largeImage);
    	largeImagegv.store();
    }else{
    	largeImagegv=(GenericValue)delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PRODUCT","dmStgColName","IMAGE_URL_PREFIX_LARGE"));
    	largeImagegv.set("dmTypeValue",largeImage);
    	largeImagegv.create();
    }
    GenericValue detailImagegv=(GenericValue)delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PRODUCT","dmStgColName","IMAGE_URL_PREFIX_DETAIL"),false);
    if(UtilValidate.isNotEmpty(detailImagegv))
    {
    	detailImagegv.set("dmTypeValue",detailImage);
    	detailImagegv.store();
    }else{
    	detailImagegv=(GenericValue)delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PRODUCT","dmStgColName","IMAGE_URL_PREFIX_DETAIL"));
    	detailImagegv.set("dmTypeValue",detailImage);
    	detailImagegv.create();
    }
    
    Debug.log("Success");
    }
    catch(GenericEntityException e)
    {
        Debug.logError(validate(e.getMessage()), module);
    }
    
}


public static void UpdateDmgdataMappingAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
	PrintWriter out = response.getWriter();
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    try{
    HttpSession session = request.getSession(true);
    String attributeOneValue = (String)request.getParameter("ATTRIBUTE_1");
	String attributeTwoValue =(String)request.getParameter("ATTRIBUTE_2");
	String attributeThreeValue = (String)request.getParameter("ATTRIBUTE_3");
	String attributeFourValue = (String)request.getParameter("ATTRIBUTE_4");
	String attributeFiveValue = (String)request.getParameter("ATTRIBUTE_5");
	String etlModelId = (String)request.getParameter("modelId");

	GenericValue dmgDataAttrOne = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_ACCOUNT", "dmStgColName", "ATTRIBUTE_1"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrOne))
	{
		dmgDataAttrOne.set("dmTypeValue",attributeOneValue);
		dmgDataAttrOne.store();
	}else{
		dmgDataAttrOne = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_ACCOUNT", "dmStgColName", "ATTRIBUTE_1"));
		dmgDataAttrOne.set("dmTypeValue",attributeOneValue);
		dmgDataAttrOne.create();
	}
	
	GenericValue dmgDataAttrTwo = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_ACCOUNT", "dmStgColName", "ATTRIBUTE_2"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrTwo))
	{
		dmgDataAttrTwo.set("dmTypeValue",attributeTwoValue);
		dmgDataAttrTwo.store();
	}else{
		dmgDataAttrTwo = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_ACCOUNT", "dmStgColName", "ATTRIBUTE_2"));
		dmgDataAttrTwo.set("dmTypeValue",attributeTwoValue);
		dmgDataAttrTwo.create();
	}
	GenericValue dmgDataAttrThree = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_ACCOUNT", "dmStgColName", "ATTRIBUTE_3"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrThree))
	{
		dmgDataAttrThree.set("dmTypeValue",attributeThreeValue);
		dmgDataAttrThree.store();
	}else{
		dmgDataAttrThree = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_ACCOUNT", "dmStgColName", "ATTRIBUTE_3"));
		dmgDataAttrThree.set("dmTypeValue",attributeThreeValue);
		dmgDataAttrThree.create();
	}
	
	GenericValue dmgDataAttrrFour = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_ACCOUNT", "dmStgColName", "ATTRIBUTE_4"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrrFour))
	{
		dmgDataAttrrFour.set("dmTypeValue",attributeFourValue);
		dmgDataAttrrFour.store();
	}else{
		dmgDataAttrrFour = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_ACCOUNT", "dmStgColName", "ATTRIBUTE_4"));
		dmgDataAttrrFour.set("dmTypeValue",attributeFourValue);
		dmgDataAttrrFour.create();
	}
	GenericValue dmgDataAttrFive = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_ACCOUNT", "dmStgColName", "ATTRIBUTE_5"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrFive))
	{
		dmgDataAttrFive.set("dmTypeValue",attributeFiveValue);
		dmgDataAttrFive.store();
	}else{
		dmgDataAttrFive = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_ACCOUNT", "dmStgColName", "ATTRIBUTE_5"));
		dmgDataAttrFive.set("dmTypeValue",attributeFiveValue);
		dmgDataAttrFive.create();
	}        	
	Debug.log("Success");
    }
    
    
    catch(GenericEntityException e)
    {
        Debug.logError(validate(e.getMessage()), module);
    }
    
}

public static void UpdateDmgdataMappingContact(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
	PrintWriter out = response.getWriter();
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    try{
    HttpSession session = request.getSession(true);
    String attributeOneValue = (String)request.getParameter("ATTRIBUTE_1");
	String attributeTwoValue =(String)request.getParameter("ATTRIBUTE_2");
	String attributeThreeValue = (String)request.getParameter("ATTRIBUTE_3");
	String attributeFourValue = (String)request.getParameter("ATTRIBUTE_4");
	String attributeFiveValue = (String)request.getParameter("ATTRIBUTE_5");
	String etlModelId = (String)request.getParameter("modelId");

	GenericValue dmgDataAttrOne = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CONTACT", "dmStgColName", "ATTRIBUTE_1"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrOne))
	{
		dmgDataAttrOne.set("dmTypeValue",attributeOneValue);
		dmgDataAttrOne.store();
	}else{
		dmgDataAttrOne = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CONTACT", "dmStgColName", "ATTRIBUTE_1"));
		dmgDataAttrOne.set("dmTypeValue",attributeOneValue);
		dmgDataAttrOne.create();
	}
	
	GenericValue dmgDataAttrTwo = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CONTACT", "dmStgColName", "ATTRIBUTE_2"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrTwo))
	{
		dmgDataAttrTwo.set("dmTypeValue",attributeTwoValue);
		dmgDataAttrTwo.store();
	}else{
		dmgDataAttrTwo = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CONTACT", "dmStgColName", "ATTRIBUTE_2"));
		dmgDataAttrTwo.set("dmTypeValue",attributeTwoValue);
		dmgDataAttrTwo.create();
	}
	GenericValue dmgDataAttrThree = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CONTACT", "dmStgColName", "ATTRIBUTE_3"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrThree))
	{
		dmgDataAttrThree.set("dmTypeValue",attributeThreeValue);
		dmgDataAttrThree.store();
	}else{
		dmgDataAttrThree = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CONTACT", "dmStgColName", "ATTRIBUTE_3"));
		dmgDataAttrThree.set("dmTypeValue",attributeThreeValue);
		dmgDataAttrThree.create();	
	}
	
	GenericValue dmgDataAttrrFour = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CONTACT", "dmStgColName", "ATTRIBUTE_4"),false);
	if(UtilValidate.isNotEmpty(attributeFourValue))
	{
		dmgDataAttrrFour.set("dmTypeValue",attributeFourValue);
		dmgDataAttrrFour.store();
	}else{
		dmgDataAttrrFour = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CONTACT", "dmStgColName", "ATTRIBUTE_4"));
		dmgDataAttrrFour.set("dmTypeValue",attributeFourValue);
		dmgDataAttrrFour.create();
	}
	GenericValue dmgDataAttrFive = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CONTACT", "dmStgColName", "ATTRIBUTE_5"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrFive))
	{
		dmgDataAttrFive.set("dmTypeValue",attributeFiveValue);
		dmgDataAttrFive.store();
	}else{
		dmgDataAttrFive = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CONTACT", "dmStgColName", "ATTRIBUTE_5"));
		dmgDataAttrFive.set("dmTypeValue",attributeFiveValue);
		dmgDataAttrFive.create();
	}        	
	Debug.log("Success");
    }
    
    
    catch(GenericEntityException e)
    {
        Debug.logError(validate(e.getMessage()), module);
    }
}

public static void UpdateDmgdataMappingCustomer(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
	PrintWriter out = response.getWriter();
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    try{
    HttpSession session = request.getSession(true);
    String attributeOneValue = (String)request.getParameter("ATTRIBUTE_1");
	String attributeTwoValue =(String)request.getParameter("ATTRIBUTE_2");
	String attributeThreeValue = (String)request.getParameter("ATTRIBUTE_3");
	String attributeFourValue = (String)request.getParameter("ATTRIBUTE_4");
	String attributeFiveValue = (String)request.getParameter("ATTRIBUTE_5");
	String etlModelId = (String)request.getParameter("modelId");

	GenericValue dmgDataAttrOne = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CUSTOMER", "dmStgColName", "ATTRIBUTE_1"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrOne))
	{
		dmgDataAttrOne.set("dmTypeValue",attributeOneValue);
		dmgDataAttrOne.store();
	}else{
		dmgDataAttrOne = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CUSTOMER", "dmStgColName", "ATTRIBUTE_1"));
		dmgDataAttrOne.set("dmTypeValue",attributeOneValue);
		dmgDataAttrOne.create();
	}
	
	GenericValue dmgDataAttrTwo = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CUSTOMER", "dmStgColName", "ATTRIBUTE_2"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrTwo))
	{
		dmgDataAttrTwo.set("dmTypeValue",attributeTwoValue);
		dmgDataAttrTwo.store();
	}else{
		dmgDataAttrTwo = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CUSTOMER", "dmStgColName", "ATTRIBUTE_2"));
		dmgDataAttrTwo.set("dmTypeValue",attributeTwoValue);
		dmgDataAttrTwo.create();
	}
	GenericValue dmgDataAttrThree = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CUSTOMER", "dmStgColName", "ATTRIBUTE_3"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrThree))
	{
		dmgDataAttrThree.set("dmTypeValue",attributeThreeValue);
		dmgDataAttrThree.store();
	}else{
		dmgDataAttrThree = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CUSTOMER", "dmStgColName", "ATTRIBUTE_3"));
		dmgDataAttrThree.set("dmTypeValue",attributeThreeValue);
		dmgDataAttrThree.create();	
	}
	
	GenericValue dmgDataAttrrFour = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CUSTOMER", "dmStgColName", "ATTRIBUTE_4"),false);
	if(UtilValidate.isNotEmpty(attributeFourValue))
	{
		dmgDataAttrrFour.set("dmTypeValue",attributeFourValue);
		dmgDataAttrrFour.store();
	}else{
		dmgDataAttrrFour = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CUSTOMER", "dmStgColName", "ATTRIBUTE_4"));
		dmgDataAttrrFour.set("dmTypeValue",attributeFourValue);
		dmgDataAttrrFour.create();
	}
	GenericValue dmgDataAttrFive = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CUSTOMER", "dmStgColName", "ATTRIBUTE_5"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrFive))
	{
		dmgDataAttrFive.set("dmTypeValue",attributeFiveValue);
		dmgDataAttrFive.store();
	}else{
			dmgDataAttrFive = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_CUSTOMER", "dmStgColName", "ATTRIBUTE_5"));
			dmgDataAttrFive.set("dmTypeValue",attributeFiveValue);
			dmgDataAttrFive.create();
	}        	
	Debug.log("Success");
    }
    
    
    catch(GenericEntityException e)
    {
        Debug.logError(validate(e.getMessage()), module);
    }
    
}

public static void UpdateDmgdataMappingLead(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
	PrintWriter out = response.getWriter();
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    try{
    HttpSession session = request.getSession(true);
    String attributeOneValue = (String)request.getParameter("ATTRIBUTE_1");
	String attributeTwoValue =(String)request.getParameter("ATTRIBUTE_2");
	String attributeThreeValue = (String)request.getParameter("ATTRIBUTE_3");
	String attributeFourValue = (String)request.getParameter("ATTRIBUTE_4");
	String attributeFiveValue = (String)request.getParameter("ATTRIBUTE_5");
	String etlModelId = (String)request.getParameter("modelId");

	GenericValue dmgDataAttrOne = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_LEAD", "dmStgColName", "ATTRIBUTE_1"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrOne))
	{
		dmgDataAttrOne.set("dmTypeValue",attributeOneValue);
		dmgDataAttrOne.store();
	}else{
		dmgDataAttrOne = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_LEAD", "dmStgColName", "ATTRIBUTE_1"));
		dmgDataAttrOne.set("dmTypeValue",attributeOneValue);
		dmgDataAttrOne.create();
	}
	
	GenericValue dmgDataAttrTwo = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_LEAD", "dmStgColName", "ATTRIBUTE_2"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrTwo))
	{
		dmgDataAttrTwo.set("dmTypeValue",attributeTwoValue);
		dmgDataAttrTwo.store();
	}else{
		dmgDataAttrTwo = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_LEAD", "dmStgColName", "ATTRIBUTE_2"));
		dmgDataAttrTwo.set("dmTypeValue",attributeTwoValue);
		dmgDataAttrTwo.create();
	}
	GenericValue dmgDataAttrThree = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_LEAD", "dmStgColName", "ATTRIBUTE_3"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrThree))
	{
		dmgDataAttrThree.set("dmTypeValue",attributeThreeValue);
		dmgDataAttrThree.store();
	}else{
		dmgDataAttrThree = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_LEAD", "dmStgColName", "ATTRIBUTE_3"));
		dmgDataAttrThree.set("dmTypeValue",attributeThreeValue);
		dmgDataAttrThree.create();
	}
	
	GenericValue dmgDataAttrrFour = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_LEAD", "dmStgColName", "ATTRIBUTE_4"),false);
	if(UtilValidate.isNotEmpty(attributeFourValue))
	{
		dmgDataAttrrFour.set("dmTypeValue",attributeFourValue);
		dmgDataAttrrFour.store();
	}else{
		dmgDataAttrrFour = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_LEAD", "dmStgColName", "ATTRIBUTE_4"));
		dmgDataAttrrFour.set("dmTypeValue",attributeFourValue);
		dmgDataAttrrFour.create();
	}
	GenericValue dmgDataAttrFive = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_LEAD", "dmStgColName", "ATTRIBUTE_5"),false);
	if(UtilValidate.isNotEmpty(dmgDataAttrFive))
	{
		dmgDataAttrFive.set("dmTypeValue",attributeFiveValue);
		dmgDataAttrFive.store();
	}else{
		dmgDataAttrFive = delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_LEAD", "dmStgColName", "ATTRIBUTE_5"));
		dmgDataAttrFive.set("dmTypeValue",attributeFiveValue);
		dmgDataAttrFive.create();
	}        	
	Debug.log("Success");
    }
    
    
    catch(GenericEntityException e)
    {
        Debug.logError(validate(e.getMessage()), module);
    }
    
}


// Logic For Updating Product Other Mapping 
public static void updateProductOtherMapping(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
	PrintWriter out = response.getWriter();
    try{
    HttpSession session = request.getSession(true);
    //String univesal = (String)request.getParameter("univesal");
    //String groupId = (String)request.getParameter("groupId");
   // String purposeId = (String)request.getParameter("purposeId");
    String uomId = (String)request.getParameter("uomId");
	String locale1 = (String)request.getParameter("locale1");
	String locale2 = (String)request.getParameter("locale2");
	String etlModelId = (String)request.getParameter("modelId");
   /* GenericValue univesalgv=(GenericValue)delegator.findOne("EtlDataMapping",UtilMisc.toMap("dmTypeName", "DMG_PRODUCT","dmStgColName","UNIVERSAL"));
    if(UtilValidate.isNotEmpty(univesalgv))
    {
    	univesalgv.set("dmTypeValue",univesal);
    	univesalgv.store();
    }
    GenericValue groupIdgv=(GenericValue)delegator.findOne("EtlDataMapping",UtilMisc.toMap("dmTypeName", "DMG_PRODUCT","dmStgColName","PRODUCT_STORE_GROUP_ID"));
    if(UtilValidate.isNotEmpty(groupIdgv))
    {
    	groupIdgv.set("dmTypeValue",groupId);
    	groupIdgv.store();
    }
    GenericValue purposeIdgv=(GenericValue)delegator.findOne("EtlDataMapping",UtilMisc.toMap("dmTypeName", "DMG_PRODUCT","dmStgColName","PRODUCT_PRICE_PURPOSE_ID"));
    if(UtilValidate.isNotEmpty(purposeIdgv))
    {
    	purposeIdgv.set("dmTypeValue",purposeId);
    	purposeIdgv.store();
    }*/
    GenericValue uomIdgv=(GenericValue)delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PRODUCT","dmStgColName","CURRENCY_UOM_ID"),false);
    if(UtilValidate.isNotEmpty(uomIdgv))
    {
    	uomIdgv.set("dmTypeValue",uomId);
    	uomIdgv.store();
    }else{
    	uomIdgv=(GenericValue)delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PRODUCT","dmStgColName","CURRENCY_UOM_ID"));
    	uomIdgv.set("dmTypeValue",uomId);
    	uomIdgv.create();
    }
	
	GenericValue local1Gen=(GenericValue)delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "GENERAL","dmStgColName","LOCALE_PRIMARY"),false);
    if(UtilValidate.isNotEmpty(local1Gen))
    {
    	local1Gen.set("dmTypeValue",locale1);
    	local1Gen.store();
    }else{
    	local1Gen=(GenericValue)delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "GENERAL","dmStgColName","LOCALE_PRIMARY"));
    	local1Gen.set("dmTypeValue",locale1);
    	local1Gen.create();
    }
	
	GenericValue local2Gen=(GenericValue)delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "GENERAL","dmStgColName","LOCALE_SECONDARY"),false);
    if(UtilValidate.isNotEmpty(local2Gen))
    {
    	local2Gen.set("dmTypeValue",locale2);
    	local2Gen.store();
    }else{
    	local2Gen=(GenericValue)delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "GENERAL","dmStgColName","LOCALE_SECONDARY"));
    	local2Gen.set("dmTypeValue",locale2);
    	local2Gen.create();
    }
    
    Debug.log("Success");
    }
    
    catch(GenericEntityException e)
    {
        Debug.logError(validate(e.getMessage()), module);
    }
    
}

// Logic For Updating Supplier Attribute Mapping 
	public static void updateSupplyMapping(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
    	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        PrintWriter out = response.getWriter();
        try{
        HttpSession session = request.getSession(true);
        String ATTRIBUTE_1 = (String)request.getParameter("ATTRIBUTE_1");
        String ATTRIBUTE_2 = (String)request.getParameter("ATTRIBUTE_2");
        String ATTRIBUTE_3 = (String)request.getParameter("ATTRIBUTE_3");
        String ATTRIBUTE_4 = (String)request.getParameter("ATTRIBUTE_4");
        String ATTRIBUTE_5 = (String)request.getParameter("ATTRIBUTE_5");
        String etlModelId = (String)request.getParameter("modelId");
        String attrs[]={ATTRIBUTE_1,ATTRIBUTE_2,ATTRIBUTE_3,ATTRIBUTE_4,ATTRIBUTE_5};
        for(int i=1;i<=5;i++)
        {
        	GenericValue gv=(GenericValue)delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_SUPPLIER","dmStgColName","ATTRIBUTE_"+i),false);
        	if(UtilValidate.isNotEmpty(gv)){
            	gv.set("dmTypeValue", attrs[i-1]);
            	gv.store();	
        	}else{
        		gv=(GenericValue)delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PARTY_SUPPLIER","dmStgColName","ATTRIBUTE_"+i));
        		gv.set("dmTypeValue", attrs[i-1]);
            	gv.create();	
        	}

        }
       Debug.log("Success");
        }
        catch(GenericEntityException e)
        {
            Debug.logError(validate(e.getMessage()), module);
        }
        
    }

	//Logic for updating Product Atribute Mapping 
	public static void updateProductAttributeMapping(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		PrintWriter out = response.getWriter();
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String etlModelId = (String)request.getParameter("modelId");
		try{
		HttpSession session = request.getSession(true);
		
		int count=12;
		String attrs[]=new String[count];
		
		for(int i=0;i<count;i++)
		{
			attrs[i]=(String)request.getParameter("ATTRIBUTE_"+(i+1));
		}
		
		for(int i=1;i<=count;i++)
		{
			
			GenericValue gv=(GenericValue)delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PRODUCT_ADDITIONAL","dmStgColName","ATTRIBUTE_"+i),false);
			if(UtilValidate.isNotEmpty(gv))
			{

			gv.set("dmTypeValue", attrs[i-1]);
			gv.store();
			}else{
				gv=(GenericValue)delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName", "DMG_PRODUCT_ADDITIONAL","dmStgColName","ATTRIBUTE_"+i));
				gv.set("dmTypeValue", attrs[i-1]);
				gv.create();
			}
		}
	   Debug.log("Success");
		}

	  
	   
		
		catch(GenericEntityException e)
		{
		    Debug.logError(validate(e.getMessage()), module);
		}
		
	}
	

public static void UpdateDmgdataMappingAccountCustom(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
	PrintWriter out = response.getWriter();
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    try{
    HttpSession session = request.getSession(true);
    String custField1 = (String)request.getParameter("custField1");
    String custField2 = (String)request.getParameter("custField2");
    String custField3 = (String)request.getParameter("custField3");
    String custField4 = (String)request.getParameter("custField4");
	String custField5 = (String)request.getParameter("custField5");
	String etlModelId = (String)request.getParameter("modelId");
	
    GenericValue updateCustField1 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_ACCOUNT","dmStgColName","CUSTOM_FIELD_1"),false);
	if(UtilValidate.isNotEmpty(updateCustField1)){
		updateCustField1.put("dmTypeValue",custField1);
		updateCustField1.store();
	}else{
		updateCustField1=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_ACCOUNT","dmStgColName","CUSTOM_FIELD_1"));
		updateCustField1.set("dmTypeValue",custField1);
		updateCustField1.create();
	}
	
	
    GenericValue updateCustField2 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_ACCOUNT","dmStgColName","CUSTOM_FIELD_2"),false);
	if(UtilValidate.isNotEmpty(updateCustField2)){
		updateCustField2.put("dmTypeValue",custField2);
		updateCustField2.store();
	}else{
		updateCustField2=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_ACCOUNT","dmStgColName","CUSTOM_FIELD_2"));
		updateCustField2.set("dmTypeValue",custField2);
		updateCustField2.create();
	}
	
	
    GenericValue updateCustField3 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_ACCOUNT","dmStgColName","CUSTOM_FIELD_3"),false);
	if(UtilValidate.isNotEmpty(updateCustField3)){
		updateCustField3.put("dmTypeValue",custField3);
		updateCustField3.store();
	}else{
		updateCustField3=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_ACCOUNT","dmStgColName","CUSTOM_FIELD_3"));
		updateCustField3.set("dmTypeValue",custField3);
		updateCustField3.create();
	}
	
	//custField4 = request.getParameter("custField4");
	GenericValue updateCustField4 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_ACCOUNT","dmStgColName","CUSTOM_FIELD_4"),false);
	if(UtilValidate.isNotEmpty(updateCustField4)){
		updateCustField4.put("dmTypeValue",custField4);
		updateCustField4.store();
	}else{
		updateCustField4=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_ACCOUNT","dmStgColName","CUSTOM_FIELD_4"));
		updateCustField4.set("dmTypeValue",custField4);
		updateCustField4.create();
	}
	
	
    //custField5 = request.getParameter("custField5");
	GenericValue updateCustField5 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_ACCOUNT","dmStgColName","CUSTOM_FIELD_5"),false);
	if(UtilValidate.isNotEmpty(updateCustField5)){
		updateCustField5.put("dmTypeValue",custField5);
		updateCustField5.store();
	}else{
		updateCustField5=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_ACCOUNT","dmStgColName","CUSTOM_FIELD_5"));
		updateCustField5.set("dmTypeValue",custField5);
		updateCustField5.create();
	}
    
    Debug.log("Success");
    }
    catch(GenericEntityException e)
    {
        Debug.logError(validate(e.getMessage()), module);
    }
    
}


public static void UpdateDmgdataMappingContactCustom(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
	PrintWriter out = response.getWriter();
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    try{
    HttpSession session = request.getSession(true);
    String custField1 = (String)request.getParameter("custField1");
    String custField2 = (String)request.getParameter("custField2");
    String custField3 = (String)request.getParameter("custField3");
    String custField4 = (String)request.getParameter("custField4");
	String custField5 = (String)request.getParameter("custField5");
	String etlModelId = (String)request.getParameter("modelId");
    GenericValue updateCustField1 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CONTACT","dmStgColName","CUSTOM_FIELD_1"),false);
	if(UtilValidate.isNotEmpty(updateCustField1)){
		updateCustField1.put("dmTypeValue",custField1);
		updateCustField1.store();
	}else{
		updateCustField1=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CONTACT","dmStgColName","CUSTOM_FIELD_1"));
		updateCustField1.set("dmTypeValue",custField1);
		updateCustField1.create();
	}
	
	
    GenericValue updateCustField2 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CONTACT","dmStgColName","CUSTOM_FIELD_2"),false);
	if(UtilValidate.isNotEmpty(updateCustField2)){
		updateCustField2.put("dmTypeValue",custField2);
		updateCustField2.store();
	}else{
		updateCustField2=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CONTACT","dmStgColName","CUSTOM_FIELD_2"));
		updateCustField2.set("dmTypeValue",custField2);
		updateCustField2.create();
	}
	
	
    GenericValue updateCustField3 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CONTACT","dmStgColName","CUSTOM_FIELD_3"),false);
	if(UtilValidate.isNotEmpty(updateCustField3)){
		updateCustField3.put("dmTypeValue",custField3);
		updateCustField3.store();
	}else{
		updateCustField3=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CONTACT","dmStgColName","CUSTOM_FIELD_3"));
		updateCustField3.set("dmTypeValue",custField3);
		updateCustField3.create();
	}
	
	//custField4 = request.getParameter("custField4");
	GenericValue updateCustField4 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CONTACT","dmStgColName","CUSTOM_FIELD_4"),false);
	if(UtilValidate.isNotEmpty(updateCustField4)){
		updateCustField4.put("dmTypeValue",custField4);
		updateCustField4.store();
	}else{
		updateCustField4=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CONTACT","dmStgColName","CUSTOM_FIELD_4"));
		updateCustField4.set("dmTypeValue",custField4);
		updateCustField4.create();
	}
	
	
    //custField5 = request.getParameter("custField5");
	GenericValue updateCustField5 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CONTACT","dmStgColName","CUSTOM_FIELD_5"),false);
	if(UtilValidate.isNotEmpty(updateCustField5)){
		updateCustField5.put("dmTypeValue",custField5);
		updateCustField5.store();
	}else{
		updateCustField5=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CONTACT","dmStgColName","CUSTOM_FIELD_5"));
		updateCustField5.set("dmTypeValue",custField5);
		updateCustField5.create();
	}
    
    Debug.log("Success");
    }
    catch(GenericEntityException e)
    {
        Debug.logError(validate(e.getMessage()), module);
    }
    
}

public static void UpdateDmgdataMappingCustomerCustom(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
	PrintWriter out = response.getWriter();
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    try{
    HttpSession session = request.getSession(true);
    String custField1 = (String)request.getParameter("custField1");
    String custField2 = (String)request.getParameter("custField2");
    String custField3 = (String)request.getParameter("custField3");
    String custField4 = (String)request.getParameter("custField4");
	String custField5 = (String)request.getParameter("custField5");
	String etlModelId = (String)request.getParameter("modelId");
	
    GenericValue updateCustField1 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CUSTOMER","dmStgColName","CUSTOM_FIELD_1"),false);
	if(UtilValidate.isNotEmpty(updateCustField1)){
		updateCustField1.put("dmTypeValue",custField1);
		updateCustField1.store();
	}else{
		updateCustField1=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CUSTOMER","dmStgColName","CUSTOM_FIELD_1"));
		updateCustField1.set("dmTypeValue",custField1);
		updateCustField1.create();
	}
	
	
    GenericValue updateCustField2 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CUSTOMER","dmStgColName","CUSTOM_FIELD_2"),false);
	if(UtilValidate.isNotEmpty(updateCustField2)){
		updateCustField2.put("dmTypeValue",custField2);
		updateCustField2.store();
	}else{
		updateCustField2=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CUSTOMER","dmStgColName","CUSTOM_FIELD_2"));
		updateCustField2.set("dmTypeValue",custField2);
		updateCustField2.create();
	}
	
	
    GenericValue updateCustField3 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CUSTOMER","dmStgColName","CUSTOM_FIELD_3"),false);
	if(UtilValidate.isNotEmpty(updateCustField3)){
		updateCustField3.put("dmTypeValue",custField3);
		updateCustField3.store();
	}else{
		updateCustField3=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CUSTOMER","dmStgColName","CUSTOM_FIELD_3"));
		updateCustField3.set("dmTypeValue",custField3);
		updateCustField3.create();
	}
	
	//custField4 = request.getParameter("custField4");
	GenericValue updateCustField4 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CUSTOMER","dmStgColName","CUSTOM_FIELD_4"),false);
	if(UtilValidate.isNotEmpty(updateCustField4)){
		updateCustField4.put("dmTypeValue",custField4);
		updateCustField4.store();
	}else{
		updateCustField4=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CUSTOMER","dmStgColName","CUSTOM_FIELD_4"));
		updateCustField4.set("dmTypeValue",custField4);
		updateCustField4.create();
	}
	
	
    //custField5 = request.getParameter("custField5");
	GenericValue updateCustField5 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CUSTOMER","dmStgColName","CUSTOM_FIELD_5"),false);
	if(UtilValidate.isNotEmpty(updateCustField5)){
		updateCustField5.put("dmTypeValue",custField5);
		updateCustField5.store();
	}else{
		updateCustField5=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_CUSTOMER","dmStgColName","CUSTOM_FIELD_5"));
		updateCustField5.set("dmTypeValue",custField5);
		updateCustField5.create();
	}
    
    Debug.log("Success");
    }
    catch(GenericEntityException e)
    {
        Debug.logError(validate(e.getMessage()), module);
    }
    
}

public static void UpdateDmgdataMappingLeadCustom(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
	PrintWriter out = response.getWriter();
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    try{
    HttpSession session = request.getSession(true);
    String custField1 = (String)request.getParameter("custField1");
    String custField2 = (String)request.getParameter("custField2");
    String custField3 = (String)request.getParameter("custField3");
    String custField4 = (String)request.getParameter("custField4");
	String custField5 = (String)request.getParameter("custField5");
	String etlModelId = (String)request.getParameter("modelId");
	
    GenericValue updateCustField1 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_LEAD","dmStgColName","CUSTOM_FIELD_1"),false);
	if(UtilValidate.isNotEmpty(updateCustField1)){
		updateCustField1.put("dmTypeValue",custField1);
		updateCustField1.store();
	}else{
		updateCustField1=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_LEAD","dmStgColName","CUSTOM_FIELD_1"));
		updateCustField1.set("dmTypeValue",custField1);
		updateCustField1.create();
	}
	
	
    GenericValue updateCustField2 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_LEAD","dmStgColName","CUSTOM_FIELD_2"),false);
	if(UtilValidate.isNotEmpty(updateCustField2)){
		updateCustField2.put("dmTypeValue",custField2);
		updateCustField2.store();
	}else{
		updateCustField2=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_LEAD","dmStgColName","CUSTOM_FIELD_2"));
		updateCustField2.set("dmTypeValue",custField2);
		updateCustField2.create();
	}
	
	
    GenericValue updateCustField3 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_LEAD","dmStgColName","CUSTOM_FIELD_3"),false);
	if(UtilValidate.isNotEmpty(updateCustField3)){
		updateCustField3.put("dmTypeValue",custField3);
		updateCustField3.store();
	}else{
		updateCustField3=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_LEAD","dmStgColName","CUSTOM_FIELD_3"));
		updateCustField3.set("dmTypeValue",custField3);
		updateCustField3.create();
	}
	
	//custField4 = request.getParameter("custField4");
	GenericValue updateCustField4 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_LEAD","dmStgColName","CUSTOM_FIELD_4"),false);
	if(UtilValidate.isNotEmpty(updateCustField4)){
		updateCustField4.put("dmTypeValue",custField4);
		updateCustField4.store();
	}else{
		updateCustField4=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_LEAD","dmStgColName","CUSTOM_FIELD_4"));
		updateCustField4.set("dmTypeValue",custField4);
		updateCustField4.create();
	}
	
	
    //custField5 = request.getParameter("custField5");
	GenericValue updateCustField5 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_LEAD","dmStgColName","CUSTOM_FIELD_5"),false);
	if(UtilValidate.isNotEmpty(updateCustField5)){
		updateCustField5.put("dmTypeValue",custField5);
		updateCustField5.store();
	}else{
		updateCustField5=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_LEAD","dmStgColName","CUSTOM_FIELD_5"));
		updateCustField5.set("dmTypeValue",custField5);
		updateCustField5.create();
	}
    
    Debug.log("Success");
    }
    catch(GenericEntityException e)
    {
        Debug.logError(validate(e.getMessage()), module);
    }
    
}

public static void UpdateDmgdataMappingSupplierCustom(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
	PrintWriter out = response.getWriter();
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
    try{
    HttpSession session = request.getSession(true);
    String custField1 = (String)request.getParameter("custField1");
    String custField2 = (String)request.getParameter("custField2");
    String custField3 = (String)request.getParameter("custField3");
    String custField4 = (String)request.getParameter("custField4");
	String custField5 = (String)request.getParameter("custField5");
	String etlModelId = (String)request.getParameter("modelId");
	
    GenericValue updateCustField1 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_SUPPLIER","dmStgColName","CUSTOM_FIELD_1"),false);
	if(UtilValidate.isNotEmpty(updateCustField1)){
		updateCustField1.put("dmTypeValue",custField1);
		updateCustField1.store();
	}else{
		updateCustField1=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_SUPPLIER","dmStgColName","CUSTOM_FIELD_1"));
		updateCustField1.set("dmTypeValue",custField1);
		updateCustField1.create();
	}
	
	
    GenericValue updateCustField2 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_SUPPLIER","dmStgColName","CUSTOM_FIELD_2"),false);
	if(UtilValidate.isNotEmpty(updateCustField2)){
		updateCustField2.put("dmTypeValue",custField2);
		updateCustField2.store();
	}else{
		updateCustField2=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_SUPPLIER","dmStgColName","CUSTOM_FIELD_2"));
		updateCustField2.set("dmTypeValue",custField2);
		updateCustField2.create();
	}
	
	
    GenericValue updateCustField3 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_SUPPLIER","dmStgColName","CUSTOM_FIELD_3"),false);
	if(UtilValidate.isNotEmpty(updateCustField3)){
		updateCustField3.put("dmTypeValue",custField3);
		updateCustField3.store();
	}else{
		updateCustField3=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_SUPPLIER","dmStgColName","CUSTOM_FIELD_3"));
		updateCustField3.set("dmTypeValue",custField3);
		updateCustField3.create();
	}
	
	//custField4 = request.getParameter("custField4");
	GenericValue updateCustField4 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_SUPPLIER","dmStgColName","CUSTOM_FIELD_4"),false);
	if(UtilValidate.isNotEmpty(updateCustField4)){
		updateCustField4.put("dmTypeValue",custField4);
		updateCustField4.store();
	}else{
		updateCustField4=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_SUPPLIER","dmStgColName","CUSTOM_FIELD_4"));
		updateCustField4.set("dmTypeValue",custField4);
		updateCustField4.create();
	}
	
	
    //custField5 = request.getParameter("custField5");
	GenericValue updateCustField5 = delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_SUPPLIER","dmStgColName","CUSTOM_FIELD_5"),false);
	if(UtilValidate.isNotEmpty(updateCustField5)){
		updateCustField5.put("dmTypeValue",custField5);
		updateCustField5.store();
	}else{
		updateCustField5=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_PARTY_SUPPLIER","dmStgColName","CUSTOM_FIELD_5"));
		updateCustField5.set("dmTypeValue",custField5);
		updateCustField5.create();
	}
    
    Debug.log("Success");
    }
    catch(GenericEntityException e)
    {
        Debug.logError(validate(e.getMessage()), module);
    }
    
}

/**
 * @author Prabhakar , Desc : Update the Supplier Product Store Group Mapping
 * @since  04 Dec 14
 * @param request
 * @param response
 * @throws IOException
 */
public static void updateSupplierProductStoreGroupMapping(HttpServletRequest request, HttpServletResponse response) throws IOException {

	PrintWriter out = response.getWriter();
	GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
	try{
		String firstGroupId = (String)request.getParameter("firstGroupId");
		String secondGroupId = (String)request.getParameter("secondGroupId");
		String thirdGroupId = (String)request.getParameter("thirdGroupId");
		String fourthGroupId = (String)request.getParameter("fourthGroupId");
		String fifthGroupId = (String)request.getParameter("fifthGroupId");
		String etlModelId = (String)request.getParameter("modelId");
		GenericValue dmgDataMapping=delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_SUPPLIER_PRODUCT","dmStgColName","FIRST_PRODUCT_STORE_GROUP_ID"),false);
		if(UtilValidate.isNotEmpty(dmgDataMapping))
		{
			dmgDataMapping.set("dmTypeValue",firstGroupId);
			dmgDataMapping.store();
		}else{
			dmgDataMapping=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_SUPPLIER_PRODUCT","dmStgColName","FIRST_PRODUCT_STORE_GROUP_ID"));
			dmgDataMapping.set("dmTypeValue",firstGroupId);
			dmgDataMapping.create();
		}

		dmgDataMapping=delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_SUPPLIER_PRODUCT","dmStgColName","SECOND_PRODUCT_STORE_GROUP_ID"),false);
		if(UtilValidate.isNotEmpty(dmgDataMapping))
		{
			dmgDataMapping.set("dmTypeValue",secondGroupId);
			dmgDataMapping.store();
		}else{
			dmgDataMapping=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_SUPPLIER_PRODUCT","dmStgColName","SECOND_PRODUCT_STORE_GROUP_ID"));
			dmgDataMapping.set("dmTypeValue",secondGroupId);
			dmgDataMapping.create();
		}

		dmgDataMapping=delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_SUPPLIER_PRODUCT","dmStgColName","THIRD_PRODUCT_STORE_GROUP_ID"),false);
		if(UtilValidate.isNotEmpty(dmgDataMapping))
		{
			dmgDataMapping.set("dmTypeValue",thirdGroupId);
			dmgDataMapping.store();
		}else{
			dmgDataMapping=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_SUPPLIER_PRODUCT","dmStgColName","THIRD_PRODUCT_STORE_GROUP_ID"));
			dmgDataMapping.set("dmTypeValue",thirdGroupId);
			dmgDataMapping.create();
		}

		dmgDataMapping=delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_SUPPLIER_PRODUCT","dmStgColName","FOURTH_PRODUCT_STORE_GROUP_ID"),false);
		if(UtilValidate.isNotEmpty(dmgDataMapping))
		{
			dmgDataMapping.set("dmTypeValue",fourthGroupId);
			dmgDataMapping.store();
		}else{
			dmgDataMapping=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_SUPPLIER_PRODUCT","dmStgColName","FOURTH_PRODUCT_STORE_GROUP_ID"));
			dmgDataMapping.set("dmTypeValue",fourthGroupId);
			dmgDataMapping.create();
		}

		dmgDataMapping=delegator.findOne("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_SUPPLIER_PRODUCT","dmStgColName","FIFTH_PRODUCT_STORE_GROUP_ID"),false);
		if(UtilValidate.isNotEmpty(dmgDataMapping))
		{
			dmgDataMapping.set("dmTypeValue",fifthGroupId);
			dmgDataMapping.store();
		}else{
			dmgDataMapping=delegator.makeValue("EtlDataMapping",UtilMisc.toMap("etlModelId",etlModelId,"dmTypeName","DMG_SUPPLIER_PRODUCT","dmStgColName","FIFTH_PRODUCT_STORE_GROUP_ID"));
			dmgDataMapping.set("dmTypeValue",fifthGroupId);
			dmgDataMapping.create();
		}


		Debug.log("Success");
	}
	catch(GenericEntityException e)
	{
        Debug.logError(validate(e.getMessage()), module);
	}

}

/*public static String getEtlImportTypeData(HttpServletRequest request, HttpServletResponse response) {

	GenericDelegator delegator = (GenericDelegator)request.getAttribute("delegator");
	String processId = request.getParameter("processId");

	Map<String, Object> resp = new HashMap<String, Object>();

	try {
		GenericValue process = delegator.findOne("EtlProcess", UtilMisc.toMap("processId", processId),false);
		if (UtilValidate.isNotEmpty(process)) 
		{
			String table = process.getString("tableName");
			GenericValue checkUploadRequest = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("status","RUNNING"),null,false));
			if(UtilValidate.isNotEmpty(checkUploadRequest)){
				resp.put("result","lock");
			}
			if("DmgProduct".equals(table)){
				GenericValue checkImportType = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("importType","PRODUCT","status","RUNNING")));
				if(UtilValidate.isNotEmpty(checkImportType)){
					resp.put("result","lock");
				}
				
			}else if("DmgCategory".equals(table)){
				GenericValue checkImportType = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("importType","CATEGORY","status","RUNNING")));
				if(UtilValidate.isNotEmpty(checkImportType)){
					resp.put("result","lock");
				}
				
			}else if("DmgPartySupplier".equals(table)){
				GenericValue checkImportType = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("importType","SUUPLIER","status","RUNNING")));
				if(UtilValidate.isNotEmpty(checkImportType)){
					resp.put("result","lock");
				}
				
			}else if("DmgSupplierProduct".equals(table)){
				GenericValue checkImportType = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("importType","SUPPLIERPRODUCT","status","RUNNING")));
				if(UtilValidate.isNotEmpty(checkImportType)){
					resp.put("result","lock");
				}
				
			}else if("DmgKitProductAssociate".equals(table)){
				GenericValue checkImportType = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("importType","PRODUCTASSOC","status","RUNNING")));
				if(UtilValidate.isNotEmpty(checkImportType)){
					resp.put("result","lock");
				}
				
			}else if("DmgPartyLead".equals(table)){
				GenericValue checkImportType = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("importType","LEAD","status","RUNNING")));
				if(UtilValidate.isNotEmpty(checkImportType)){
					resp.put("result","lock");
				}
				
			}else if("DmgPartyCustomer".equals(table)){
				GenericValue checkImportType = EntityUtil.getFirst(delegator.findByAnd("EtlUploadRequest",UtilMisc.toMap("importType","CUSTOMER","status","RUNNING")));
				if(UtilValidate.isNotEmpty(checkImportType)){
					resp.put("result","lock");
				}
				
			}
		}
		else
		{
			resp.put("result","unLock");
		}

	} catch (GenericEntityException e) {
		return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, UtilMisc.toMap("name", ""));
	}

	return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, resp);
}*/

/*public static String getStorePayments(HttpServletRequest request, HttpServletResponse response) {
	Delegator delegator = (Delegator) request.getAttribute("delegator");
	String selGroup="org.ofbiz";
	GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(selGroup);
	SQLProcessor sqlProcessor = new SQLProcessor(helperInfo);
	ResultSet rs = null;
	List<Map<String, String>> descriptionList = new ArrayList<Map<String, String>>();

	try {
		String storeId = request.getParameter("storeId");
		String sqlQuery = "SELECT * FROM Product_Store_Payment_Setting WHERE product_store_id='"+storeId+"'";	
		rs = sqlProcessor.executeQuery(sqlQuery);
		if(rs != null) {
			while(rs.next()) {
				Map<String, String> descMap = FastMap.newInstance();
				descMap.put("paymentMethodTypeId", rs.getString("payment_Method_Type_Id"));
				GenericValue paymentDesc = delegator.findOne("PaymentMethodType",UtilMisc.toMap("paymentMethodTypeId",rs.getString("payment_Method_Type_Id")),false);
				descMap.put("description", paymentDesc.getString("description"));
				descriptionList.add(descMap);

			}
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
		return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, descriptionList);
	}
	return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, descriptionList);
}*/
/*public static String getStoreShipments(HttpServletRequest request, HttpServletResponse response) {
	Delegator delegator = (Delegator) request.getAttribute("delegator");
	String selGroup="org.ofbiz";
	GenericHelperInfo helperInfo = delegator.getGroupHelperInfo(selGroup);
	SQLProcessor sqlProcessor = new SQLProcessor(helperInfo);
	ResultSet rs = null;
	List<Map<String, String>> descriptionList = new ArrayList<Map<String, String>>();

	try {
		String storeId = request.getParameter("storeId");
		String sqlQuery = "SELECT * FROM Product_Store_Shipment_Meth WHERE product_store_id='"+storeId+"'";	
		rs = sqlProcessor.executeQuery(sqlQuery);
		if(rs != null) {
			while(rs.next()) {
				Map<String, String> descMap = FastMap.newInstance();
				descMap.put("shipmentMethodTypeId", rs.getString("shipment_Method_Type_Id"));
				GenericValue shipmentDesc = delegator.findOne("ShipmentMethodType",UtilMisc.toMap("shipmentMethodTypeId",rs.getString("shipment_Method_Type_Id")));
				descMap.put("description", shipmentDesc.getString("description"));
				descriptionList.add(descMap);

			}
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
		return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, descriptionList);
	}
	return org.opentaps.common.event.AjaxEvents.doJSONResponse(response, descriptionList);
}*/
public static String assignExpModel(HttpServletRequest request, HttpServletResponse response) {
	Delegator delegator = (Delegator) request.getAttribute("delegator");

	try {
		String impModelId = request.getParameter("impModelId");
		request.setAttribute("model",impModelId);
		String expModelId = request.getParameter("expModelId");
		
		if(UtilValidate.isNotEmpty(impModelId) && UtilValidate.isNotEmpty(impModelId)){
			GenericValue assignExpModel = EntityUtil.getFirst(delegator.findByAnd("EtlModel",UtilMisc.toMap("modelName",impModelId),null,false));
			assignExpModel.put("expModelId", expModelId);
			assignExpModel.store();
			
			GenericValue removeExpModel = EntityUtil.getFirst(delegator.findByAnd("EtlModel",UtilMisc.toMap("expModelId",assignExpModel.getString("modelId")),null,false));
			if(UtilValidate.isNotEmpty(removeExpModel)){
				removeExpModel.put("expModelId", "");
				removeExpModel.store();	
			}
			
			GenericValue assignImpModel = delegator.findOne("EtlModel",UtilMisc.toMap("modelId",expModelId),false);
			assignImpModel.put("expModelId", assignExpModel.getString("modelId"));
			assignImpModel.store();
			
			request.setAttribute("_EVENT_MESSAGE_",expModelId+" Model assigned successfully.");
		}
	}
	catch(Exception e)
	{
		/*e.printStackTrace();*/
		request.setAttribute("_ERROR_MESSAGE_",e.toString());
		return "error";
	}

	return "success";
}

	public static String validate(String str)
	{
		return str;
	}

}

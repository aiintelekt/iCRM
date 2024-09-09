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

import javolution.util.FastList;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil
//import org.opentaps.base.constants.StatusItemConstants;

customersProcessed = 0;
customersNotProcessed = 0;
accountsProcessed = 0;
accountsNotProcessed = 0;
leadsProcessed = 0;
leadsNotProcessed = 0;
contactsProcessed = 0;
contactsNotProcessed = 0;
productSupplementaryProcessed = 0;
itmProcessed = 0;
activityProcessed = 0;

productsProcessed = 0;
productsNotProcessed = 0;
inventoryProcessed = 0;
inventoryNotProcessed = 0;
orderHeadersProcessed = 0;
orderHeadersNotProcessed = 0;
orderItemsProcessed = 0;
orderItemsNotProcessed = 0;
InvoiceHeaderProcessed =0;
InvoiceItemProcessed = 0;
glAccountsProcessed = 0;
glAccountsNotProcessed = 0;
categoriesProcessed = 0;
categoriesNotProcessed = 0;
productSupplementaryNotProcessed = 0;
itmNotProcessed = 0;
activityNotProcessed = 0;

/*
  GET PROCESSED
*/
//searchConditions = FastList.newInstance();
//searchConditions.add(new EntityExpr("importStatusId", EntityOperator.EQUALS, "DATAIMP_IMPORTED"));
//allConditions = new EntityConditionList(searchConditions, EntityOperator.AND);

//By Arshiya :: Processed records

EntityFieldMap ecl = EntityCondition.makeConditionMap("importStatusId" , "DATAIMP_IMPORTED");

customersProcessed = delegator.findCountByCondition("DataImportCustomer", ecl, null,null);
accountsProcessed = delegator.findCountByCondition("DataImportAccount", ecl, null,null);
leadsProcessed = delegator.findCountByCondition("DataImportLead", ecl, null,null);
contactsProcessed = delegator.findCountByCondition("DataImportContact", ecl, null,null);
productSupplementaryProcessed = delegator.findCountByCondition("DataImportProductSupplementary", ecl, null,null);
itmProcessed = delegator.findCountByCondition("DataImportItm", ecl, null,null);
activityProcessed = delegator.findCountByCondition("DataImportActivity", ecl, null,null);

/*
suppliersProcessed = delegator.findCountByCondition("DataImportSupplier", ecl, null,null);
productsProcessed = delegator.findCountByCondition("DataImportProduct", ecl, null,null);
inventoryProcessed = delegator.findCountByCondition("DataImportInventory", ecl, null,null);
orderHeadersProcessed = delegator.findCountByCondition("DataImportOrderHeader", ecl, null,null);
orderItemsProcessed = delegator.findCountByCondition("DataImportOrderItem", ecl, null,null); 
InvoiceHeaderProcessed = delegator.findCountByCondition("DataImportInvoiceHeader", ecl, null,null);
InvoiceItemProcessed = delegator.findCountByCondition("DataImportInvoiceItem", ecl, null,null);
//glAccountsProcessed = delegator.findCountByCondition("DataImportGlAccount", allConditions, null,null);
categoriesProcessed = delegator.findCountByCondition("DataImportCategory", ecl, null,null);
lockboxBatchProcessed = delegator.findCountByCondition("FioLockboxBatchStaging", ecl, null,null);
lockboxBatchItemProcessed = delegator.findCountByCondition("FioLockboxBatchItemStaging", ecl, null,null);
walletProcessed = delegator.findCountByCondition("DataImportWallet", ecl, null,null);
*/

context.put("customersProcessed", customersProcessed);
context.put("accountsProcessed", accountsProcessed);
context.put("leadsProcessed", leadsProcessed);
context.put("contactsProcessed", contactsProcessed);
context.put("productSupplementaryProcessed", productSupplementaryProcessed);
context.put("itmProcessed", itmProcessed);
context.put("activityProcessed", activityProcessed);

/*
context.put("suppliersProcessed", suppliersProcessed);
context.put("orderHeadersProcessed", orderHeadersProcessed);
context.put("orderItemsProcessed", orderItemsProcessed);
context.put("InvoiceHeaderProcessed", InvoiceHeaderProcessed);
context.put("InvoiceItemProcessed", InvoiceItemProcessed);
context.put("categoriesProcessed", categoriesProcessed);
context.put("productsProcessed", productsProcessed);
context.put("lockboxBatchProcessed", lockboxBatchProcessed);
context.put("lockboxBatchItemProcessed", lockboxBatchItemProcessed);
context.put("walletProcessed", walletProcessed);
*/
System.out.println("++++++++++++++++categoriesProcessed+++++++++++++++"+categoriesProcessed);

//Not Processed field display By Arshiya

EntityCondition eclNot = EntityCondition.makeCondition(EntityOperator.OR, 
							EntityCondition.makeCondition("importStatusId", "DATAIMP_FAILED"),
                            EntityCondition.makeCondition("importStatusId","DATAIMP_NOT_PROC" ),
                            EntityCondition.makeCondition("importStatusId","DATAIMP_ERROR" ),
                            EntityCondition.makeCondition("importStatusId", null),
                            EntityCondition.makeCondition("importStatusId", "LBIMP_READY"),
                            );

customersNotProcessed = delegator.findCountByCondition("DataImportCustomer", eclNot, null,null);
accountsNotProcessed = delegator.findCountByCondition("DataImportAccount", eclNot, null,null);
leadsNotProcessed = delegator.findCountByCondition("DataImportLead", eclNot, null,null);
contactsNotProcessed = delegator.findCountByCondition("DataImportContact", eclNot, null,null);
productSupplementaryNotProcessed = delegator.findCountByCondition("DataImportProductSupplementary", eclNot, null,null);
itmNotProcessed = delegator.findCountByCondition("DataImportItm", eclNot, null,null);
activityNotProcessed = delegator.findCountByCondition("DataImportActivity", eclNot, null,null);

/*
suppliersNotProcessed = delegator.findCountByCondition("DataImportSupplier", eclNot, null,null);
orderHeadersNotProcessed = delegator.findCountByCondition("DataImportOrderHeader", eclNot, null,null);
orderItemsNotProcessed = delegator.findCountByCondition("DataImportOrderItem", eclNot, null,null);
InvoiceHeaderNotProcessed = delegator.findCountByCondition("DataImportInvoiceHeader", eclNot, null,null);
InvoiceItemNotProcessed = delegator.findCountByCondition("DataImportInvoiceItem", eclNot, null,null);
categoriesNotProcessed = delegator.findCountByCondition("DataImportCategory", eclNot, null,null);
productNotProcessed = delegator.findCountByCondition("DataImportProduct", eclNot, null,null);
lockboxBatchNotProcessed = delegator.findCountByCondition("FioLockboxBatchStaging", eclNot, null,null);
lockboxBatchItemNotProcessed = delegator.findCountByCondition("FioLockboxBatchItemStaging", eclNot, null,null);
walletNotProcessed = delegator.findCountByCondition("DataImportWallet", eclNot, null,null);
*/

context.put("customersNotProcessed", customersNotProcessed);
context.put("accountsNotProcessed", accountsNotProcessed);
context.put("leadsNotProcessed", leadsNotProcessed);
context.put("contactsNotProcessed", contactsNotProcessed);
context.put("productSupplementaryNotProcessed", productSupplementaryNotProcessed);
context.put("itmNotProcessed", itmNotProcessed);
context.put("activityNotProcessed", activityNotProcessed);

/*
context.put("suppliersNotProcessed", suppliersNotProcessed);
context.put("orderHeadersNotProcessed", orderHeadersNotProcessed);
context.put("orderItemsNotProcessed", orderItemsNotProcessed);
context.put("InvoiceHeaderNotProcessed", InvoiceHeaderNotProcessed);
context.put("InvoiceItemNotProcessed", InvoiceItemNotProcessed);
context.put("categoriesNotProcessed", categoriesNotProcessed);
context.put("productNotProcessed", productNotProcessed);
context.put("lockboxBatchNotProcessed", lockboxBatchNotProcessed);
context.put("lockboxBatchItemNotProcessed", lockboxBatchItemNotProcessed);
context.put("walletNotProcessed", walletNotProcessed);
*/

/*
  GET NOT-PROCESSED
*/

/*EntityCondition statusCond = EntityCondition.makeCondition(EntityOperator.OR,
         EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_NOT_PROC"),
         EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, "DATAIMP_FAILED"),
         EntityCondition.makeCondition("importStatusId", EntityOperator.EQUALS, null));

suppliersNotProcessed = delegator.findCountByCondition("DataImportSupplier", statusCond, null);
customersNotProcessed = delegator.findCountByCondition("DataImportCustomer", statusCond, null);
productsNotProcessed = delegator.findCountByCondition("DataImportProduct", statusCond, null);
inventoryNotProcessed = delegator.findCountByCondition("DataImportInventory", statusCond, null);
orderHeadersNotProcessed = delegator.findCountByCondition("DataImportOrderHeader", statusCond, null);
orderItemsNotProcessed = delegator.findCountByCondition("DataImportOrderItem", statusCond, null);*/
//glAccountsNotProcessed = delegator.findCountByCondition("DataImportGlAccount", statusCond, null);

/*context.put("suppliersProcessed", suppliersProcessed);
context.put("suppliersNotProcessed", suppliersNotProcessed);
context.put("customersProcessed", customersProcessed);
context.put("customersNotProcessed", customersNotProcessed);
context.put("productsProcessed", productsProcessed);
context.put("productsNotProcessed", productsNotProcessed);
context.put("inventoryProcessed", inventoryProcessed);
context.put("inventoryNotProcessed", inventoryNotProcessed);
context.put("orderHeadersProcessed", orderHeadersProcessed);
context.put("orderHeadersNotProcessed", orderHeadersNotProcessed);
context.put("orderItemsProcessed", orderItemsProcessed);
context.put("orderItemsNotProcessed", orderItemsNotProcessed);
context.put("glAccountsProcessed", glAccountsProcessed);
context.put("glAccountsNotProcessed", glAccountsNotProcessed);*/

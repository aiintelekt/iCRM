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
 *  $Id:$
 *
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
package org.fio.crm.activities;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javolution.util.FastList;

/**
 * Activity utility methods.
 */
public final class UtilActivity {

    private static final String MODULE = UtilActivity.class.getName();

    private UtilActivity() { }

    /**
     * gets all unexpired parties related to the work effort. The result is a list of WorkEffortPartyAssignments containing
     * the partyIds we need.
     */
    public static List<GenericValue> getActivityParties(Delegator delegator, String workEffortId, List<String> partyRoles) throws GenericEntityException {
        // add each role type id (ACCOUNT, CONTACT, etc) to an OR condition list
        List<EntityCondition> roleCondList = new ArrayList<EntityCondition>();
        for (String roleTypeId : partyRoles) {
            roleCondList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
        }
        EntityCondition roleEntityCondList = EntityCondition.makeCondition(roleCondList, EntityOperator.OR);

        // roleEntityCondList AND workEffortId = ${workEffortId} AND filterByDateExpr
        EntityCondition mainCondList = EntityCondition.makeCondition(EntityOperator.AND,
                    roleEntityCondList,
                    EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
                    EntityUtil.getFilterByDateExpr());

        EntityListIterator partiesIt = delegator.find("WorkEffortPartyAssignment", mainCondList, null,
                null,
                null, // fields to order by (unimportant here)
                new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));
        List<GenericValue> parties = partiesIt.getCompleteList();
        partiesIt.close();

        return parties;
    }

    /**
     * Gets owner party id of activity.
     */
    public static GenericValue getActivityOwner(String workEffortId, Delegator delegator) throws GenericEntityException {
        List<GenericValue> ownerParties = EntityUtil.filterByDate(getActivityParties(delegator, workEffortId, UtilMisc.toList("CAL_OWNER")));
        if (UtilValidate.isEmpty(ownerParties)) {
            Debug.logWarning("No owner parties found for activity [" + workEffortId + "]", MODULE);
            return null;
        } else if (ownerParties.size() > 1) {
            Debug.logWarning("More than one owner party found for activity [" + workEffortId + "].  Only the first party will be returned, but the parties are " + EntityUtil.getFieldListFromEntityList(ownerParties, "partyId", false), MODULE);
        }

        return EntityUtil.getFirst(ownerParties);

    }
}

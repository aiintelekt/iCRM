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

package org.fio.dataimport.events;

import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.fio.dataimport.CommissionRatesDecoder;
import org.fio.dataimport.ContactDecoder;
import org.fio.dataimport.CustomerDecoder;
import org.fio.dataimport.ItmDataDecoder;
import org.fio.dataimport.OpentapsImporter;
import org.fio.dataimport.UtilMessage;
import org.fio.dataimport.ProductSupplementaryDataDecoder;

/**
 * @author Sharif
 *
 */
public class OrderEvents {

    public static String module = OrderEvents.class.getName();
    
    public static Map<String, Object> importItms(DispatchContext dctx, Map<String, ?> context) {
        int imported = 0;
        OpentapsImporter importer = null;
        try {
            importer = new OpentapsImporter("DataImportItm", dctx, new ItmDataDecoder(context));
            imported += importer.runImport(context);
        } catch (GeneralException e) {
            return UtilMessage.createAndLogServiceError(e, module);
        }
        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("itmImported", imported);
        result.put("importedDataList", importer.importedDataList);
        return result;
    }   
}


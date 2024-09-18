package com.groupfio.ofbiz.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.entity.GenericEntity;

class TransformServiceResponse {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> transformResult(Map<String, Object> resultMap) {
		for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
			if (entry.getValue() instanceof Map && !(entry.getValue() instanceof GenericEntity)) {
				entry.setValue(transformResult((Map<String, Object>) entry.getValue()));
			} else if (entry.getValue() instanceof List || entry.getValue() instanceof Set) {
				entry.setValue(transform(entry.getValue()));
			} else if (entry.getValue() instanceof GenericEntity) {
				entry.setValue(transformGenericEntity(entry.getValue()));
			}
		}
		return resultMap;
	}

	private static Object transform(Object obj) {
		if (obj instanceof Collection) {
			Object[] nobj = ((Collection<?>) obj).toArray();
			for (int i = 0; i < nobj.length; i++) {
				Object o = nobj[i];
				if (o instanceof GenericEntity) {
					// replace GenericValue, GenericPK and GenericEntity with
					// GenericEntityDetail
					nobj[i] = transformGenericEntity(nobj[i]);
				}
			}
			obj = nobj;
		} else if (obj instanceof GenericEntity) {
			obj = transformGenericEntity(obj);
		}
		return obj;
	}

//	private static JsonGenericEntityDetail transformGenericEntity(Object obj) {
//		GenericEntity ge = (GenericEntity) obj;
//		return new JsonGenericEntityDetail(ge.getEntityName(), ge.getAllFields());
//	}

	private static Map<String, Object> transformGenericEntity(Object obj) {
		GenericEntity ge = (GenericEntity) obj;
		return ge.getAllFields();
	}

}

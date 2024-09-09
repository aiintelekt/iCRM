package org.groupfio.dyna.screen;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityOperator;

/**
 * @author Sharif Ul Islam
 *
 */
public class DynaScreenConstants {

	// Resource bundles	
    public static final String configResource = "dyna-screen";
    public static final String uiLabelMap = "dyna-screenUiLabels";
    
	public static final class SourceInvoked {
        private SourceInvoked() { }
        public static final String API = "API";
        public static final String PORTAL = "PORTAL";
        public static final String UNKNOWN = "UNKNOWN";
    }
	
	public static final class LookupType {
        private LookupType() { }
        public static final String STATIC_DATA = "STATIC_DATA";
        public static final String PICKER_DATA = "PICKER_DATA";
        public static final String DYNAMIC_DATA = "DYNAMIC_DATA";
    }
	
	public static final class FieldType {
        private FieldType() { }
        public static final String TEXT = "TEXT";
        public static final String DATE = "DATE";
        public static final String DROPDOWN = "DROPDOWN";
        public static final String PICKER = "PICKER";
        public static final String NUMBER = "NUMBER";
        public static final String DATE_TIME = "DATE_TIME";
        public static final String RADIO = "RADIO";
        public static final String TEXT_AREA = "TEXT_AREA";
    }
	
	public static final class LayoutType {
        private LayoutType() { }
        public static final String ONE_COLUMN = "1C";
        public static final String TWO_COLUMN = "2C";
        public static final String THREE_COLUMN = "3C";
    }
	
	public static final class OperatorType {
        private OperatorType() { }
        public static String OPERATOR_EQUAL = "eq";
    	public static String OPERATOR_NOT_EQUAL = "ne";
    	public static String OPERATOR_LESS = "lt";
    	public static String OPERATOR_LESS_OR_EQUAL = "le";
    	public static String OPERATOR_GREATER = "gt";
    	public static String OPERATOR_GREATER_OR_EQUAL = "ge";
    	public static String OPERATOR_IS_IN = "in";
    	public static String OPERATOR_IS_NOT_IN = "ni";
    	public static String OPERATOR_IS_LIKE = "like";
    	public static String OPERATOR_IS_NOT_LIKE = "nlike";
    }
	
	public static final Map<String, EntityComparisonOperator> ENTITY_OPERATOR_BY_NAME = 
	   		 Collections.unmodifiableMap(new HashMap<String, EntityComparisonOperator>() {{ 
	    	        put(OperatorType.OPERATOR_EQUAL, EntityOperator.EQUALS);
	    	        put(OperatorType.OPERATOR_NOT_EQUAL, EntityOperator.NOT_EQUAL);
	    	        put(OperatorType.OPERATOR_LESS, EntityOperator.LESS_THAN);
	    	        put(OperatorType.OPERATOR_LESS_OR_EQUAL, EntityOperator.LESS_THAN_EQUAL_TO);
	    	        put(OperatorType.OPERATOR_GREATER, EntityOperator.GREATER_THAN);
	    	        put(OperatorType.OPERATOR_GREATER_OR_EQUAL, EntityOperator.GREATER_THAN_EQUAL_TO);
	    	        put(OperatorType.OPERATOR_IS_IN, EntityOperator.IN);
	    	        put(OperatorType.OPERATOR_IS_NOT_IN, EntityOperator.NOT_IN);
	    	        put(OperatorType.OPERATOR_IS_LIKE, EntityOperator.LIKE);
	    	        put(OperatorType.OPERATOR_IS_NOT_LIKE, EntityOperator.NOT_LIKE);
	   		 }});
	
}

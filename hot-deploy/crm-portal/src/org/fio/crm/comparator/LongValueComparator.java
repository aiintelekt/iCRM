/**
 * 
 */
package org.fio.crm.comparator;

import java.util.Comparator;
import java.util.Map;

/**
 * @author Sharif
 *
 */
public class LongValueComparator implements Comparator<String> {
    Map<String, Long> base;

    public LongValueComparator(Map<String, Long> base) {
        this.base = base;
    }

    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}

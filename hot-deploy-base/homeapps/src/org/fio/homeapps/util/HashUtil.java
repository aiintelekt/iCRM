package org.fio.homeapps.util;

import org.ofbiz.base.crypto.HashCrypt;

/**
 * @author Sharif
 *
 */
public final class HashUtil {
    public static final String HASH_TYPE = "SHA";

    public static String getDigestHash(String str) {
        return HashCrypt.getDigestHash(str, HashUtil.HASH_TYPE).replaceAll("\\{" + HashUtil.HASH_TYPE + "\\}", "");
    }

}

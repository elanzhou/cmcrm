package com.elanzone.cmcrm.utils;

import org.ofbiz.entity.Delegator;

import java.util.Map;

/**
 * http://www.elanzone.com/.
 * User: elanzhou@126.com
 */
public interface PartyIdGenerator {

    /**
     * 根据Party名称自动生成Id
     *
     * @param delegator
     * @param seqName
     * @param context
     * @return
     */
    String generatePartyId(Delegator delegator, String seqName, Map<String, ? extends Object> context);
}

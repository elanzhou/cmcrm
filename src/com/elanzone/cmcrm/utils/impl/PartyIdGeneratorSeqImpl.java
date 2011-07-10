package com.elanzone.cmcrm.utils.impl;

import com.elanzone.cmcrm.utils.PartyIdGenerator;
import org.ofbiz.entity.Delegator;

import java.util.Map;

/**
 * 从数据库中获取一个序列值
 *
 * http://www.elanzone.com/.
 * User: elanzhou@126.com
 * Date: 11-7-10
 * Time: 下午2:59
 */
public class PartyIdGeneratorSeqImpl implements PartyIdGenerator{
    public String generatePartyId(Delegator delegator, String seqName,Map<String, ? extends Object> context) {
        return delegator.getNextSeqId("Party");
    }
}

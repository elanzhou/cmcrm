package com.elanzone.cmcrm.utils.impl;

import com.elanzone.cmcrm.utils.PartyIdGenerator;
import org.ofbiz.entity.Delegator;

import java.util.Map;

/**
 *
 * http://www.elanzone.com/.
 * User: elanzhou@126.com
 * Date: 11-7-10
 * Time: 下午3:01
 */
public class PartyIdGeneratorPinyinImpl implements PartyIdGenerator{

    public String generatePartyId(Delegator delegator, String seqName, Map<String, ? extends Object> context) {
        // todo: 根据PartyName的拼音生成对应的id ( 如何避免重复？）
        return null;
    }
}

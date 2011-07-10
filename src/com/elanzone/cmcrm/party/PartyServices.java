package com.elanzone.cmcrm.party;

import org.ofbiz.service.DispatchContext;

import java.util.Map;

public class PartyServices {

    public static final String module = PartyServices.class.getName();
    public static final String resource = "PartyUiLabels";
    public static final String resourceError = "PartyErrorUiLabels";

    /**
     * Creates a Company
     *
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> createCompany(DispatchContext ctx, Map<String, ? extends Object> context) {
        context.put("partyId", "COMPANY");
        context.put("partyTypeId", "CORPORATION");
        return org.ofbiz.party.party.PartyServices.createPartyGroup(ctx, context);
    }

    /**
     * Updates the Company.
     *
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> updateCompany(DispatchContext ctx, Map<String, ? extends Object> context) {
        return org.ofbiz.party.party.PartyServices.updatePartyGroup(ctx, context);
    }
}

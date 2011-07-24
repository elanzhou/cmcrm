package com.elanzone.cmcrm.party;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.*;

import java.util.Map;

public class PartyServices {

    public static final String module = PartyServices.class.getName();
    public static final String resource = "PartyUiLabels";
    public static final String resourceError = "PartyErrorUiLabels";

    /**
     * Create or Update the Company
     *
     * @param dctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map<String, Object> createOrUpdateCompany(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String companyPartyId = "COMPANY";
        String partygroupServiceName;
        try {
            GenericValue company = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", companyPartyId));
            if (company == null) {
                partygroupServiceName = "createPartyGroup";
            }
            else {
                partygroupServiceName = "updatePartyGroup";
            }

            ModelService createAccountService = dctx.getModelService(partygroupServiceName);
            Map<String, Object> partygroupContext = createAccountService.makeValid(context, ModelService.IN_PARAM);
            createAccountService.put("partyId", companyPartyId);
            createAccountService.put("partyTypeId", "CORPORATION");

            Map<String, Object> pgOpResult = dispatcher.runSync(partygroupServiceName, partygroupContext);
            if (ServiceUtil.isError(pgOpResult) || ServiceUtil.isFailure(pgOpResult)) {
                return pgOpResult;
            }

        } catch (GenericServiceException gse) {
            return ServiceUtil.returnError(gse.getMessage());
        } catch (GenericEntityException gee) {
            return ServiceUtil.returnError(gee.getMessage());
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("partyId", companyPartyId);
        return result;
    }

}

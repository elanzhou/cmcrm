package com.elanzone.cmcrm.tools.geo;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.authz.Authorization;
import org.ofbiz.service.LocalDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class GeoImporter {

    public static String importService(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Authorization authz = (Authorization) request.getAttribute("authz");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);

        Map<String, Object> params = UtilHttp.getParameterMap(request);
        // get the schedule parameters
//        String jobName = (String) params.remove("JOB_NAME");

        // the frequency map
//        Map<String, Integer> freqMap = FastMap.newInstance();

//        freqMap.put("SECONDLY", Integer.valueOf(1));
//        String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.service_scheduled", locale);
//        request.setAttribute("_EVENT_MESSAGE_", errMsg);
//        if (null!=syncServiceResult) {
//            request.getSession().setAttribute("_RUN_SYNC_RESULT_", syncServiceResult);
//            return "sync_success";
//        }
        return "success";
    }


    /**
     * a
     *
     * @param args
     *          --filename
     *          --country
     *          --format
     */
    public static void main(String args[]) {
    }
}

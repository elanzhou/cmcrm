package com.elanzone.cmcrm.tools.geo;

import javolution.util.FastMap;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.authz.Authorization;
import org.ofbiz.service.LocalDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.sql.*;
import java.util.*;

public class GeoImporter {

    public static final String module = GeoImporter.class.getName();
    public static final String err_resource = "cmcrmToolsUiLabels";

    public static String importCountryData(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Authorization authz = (Authorization) request.getAttribute("authz");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);

        File uploadFileDir = FileUtil.getFile("runtime/tmp");
        File uploadFile = null;
        ServletFileUpload dfu = new ServletFileUpload(new DiskFileItemFactory(10240, uploadFileDir));
        java.util.List lst = null;
        try {
            lst = dfu.parseRequest(request);
        } catch (FileUploadException e4) {
            request.setAttribute("_ERROR_MESSAGE_", e4.getMessage());
            Debug.logError("[GeoImporter.importCountryData] " + e4.getMessage(), module);
            return "error";
        }
        //if (Debug.infoOn()) Debug.logInfo("[UploadContentAndImage]lst " + lst, module);

        if (lst.size() == 0) {
            String errMsg = UtilProperties.getMessage(GeoImporter.err_resource, "geoimport.no_files_uploaded", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            Debug.logWarning("[GeoImporter.importCountryData] No files uploaded", module);
            return "error";
        }

        Map passedParams = FastMap.newInstance();
        FileItem fi = null;
        for (Object aLst : lst) {
            fi = (FileItem) aLst;
            //String fn = fi.getName();
            String fieldName = fi.getFieldName();
            if (fi.isFormField()) {
                String fieldStr = fi.getString();
                passedParams.put(fieldName, fieldStr);
            } else if (fieldName.equals("geoData")) {
                try {
                    uploadFile = File.createTempFile("geoimporter_", ".tmp", uploadFileDir);
                    fi.write(uploadFile);
                } catch (Exception e) {
                    String errMsg = UtilProperties.getMessage(GeoImporter.err_resource, "geoimport.no_upload_data", locale);
                    request.setAttribute("_ERROR_MESSAGE_", errMsg + "\n" + "upload file path: " + uploadFileDir.getAbsolutePath() + e.getMessage());
                    Debug.logInfo("[GeoImporter.importCountryData] not specify country", module);
                    if (uploadFile != null) {
                        uploadFile.deleteOnExit();
                    }
                    return "error";
                }
            }
        }
        if (Debug.infoOn()) Debug.logInfo("[UploadContentAndImage]passedParams: " + passedParams, module);
        

        String country = "";
        if (passedParams.containsKey("country")) {
            country = (String)passedParams.get("country");
        }
        if (country.isEmpty()) {
            String errMsg = UtilProperties.getMessage(GeoImporter.err_resource, "geoimport.not_specify_country", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            Debug.logInfo("[GeoImporter.importCountryData] not specify country", module);
            uploadFileDir.deleteOnExit();
            uploadFile.deleteOnExit();
            return "error";
        }
        // todo: check country is valid

        GeoDealerAccessImp geoDealer = new GeoDealerAccessImp(delegator);
        geoDealer.setFileName(uploadFile.getAbsolutePath());
        String analyzeResult = null;
        try {
            analyzeResult = geoDealer.analyzeData();
            if (!analyzeResult.isEmpty()) {
                String errMsg = UtilProperties.getMessage(GeoImporter.err_resource, analyzeResult, locale);
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                Debug.logError("[GeoImporter.importCountryData] not specify country", module);
                return "error";
            }
        } catch (Exception e) {
            String errMsg = UtilProperties.getMessage(GeoImporter.err_resource, "geoimport.exception_caught", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg + "\n" + e.getMessage());
            Debug.logError("[GeoImporter.importCountryData] not specify country", module);
            return "error";
        } finally {
            uploadFileDir.deleteOnExit();
            uploadFile.deleteOnExit();
        }

        String sucMsg = "province count: " + geoDealer.getProvinces().size();
        request.setAttribute("_ERROR_MESSAGE_", sucMsg);

//        String errMsg = "", sucMsg= "";
//        try {
//            geoDealer.createGeoItems(country);
//        } catch (GenericEntityException e) {
//            errMsg = "GenericEntityException "+ UtilMisc.toMap("errMessage", e.toString());
//            Debug.logError(e, errMsg, module);
//            e.printStackTrace();
//            return "error";
//        }

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
        return "error";
//        return "success";
    }

    private static void createGeoItem(Delegator delegator, String parentGeoId,
                            String geoId, String geoName, String type, Timestamp now) throws GenericEntityException {
        GenericValue provinceItem = delegator.makeValue("Geo");
        provinceItem.set("geoId",    geoId);
        provinceItem.set("geoTypeId",type);
        provinceItem.set("geoName",  geoName);
        provinceItem.set("lastUpdatedStamp",     now);
        provinceItem.set("lastUpdatedTxStamp",   now);
        provinceItem.set("createdStamp",         now);
        provinceItem.set("createdTxStamp",       now);

        GenericValue provinceAssocItem = delegator.makeValue("GeoAssoc");
        provinceAssocItem.set("geoId",   geoId);
        provinceAssocItem.set("geoIdTo", parentGeoId);
        provinceAssocItem.set("geoAssocTypeId",  "GROUP_MEMBER");
        provinceAssocItem.set("lastUpdatedStamp",     now);
        provinceAssocItem.set("lastUpdatedTxStamp",   now);
        provinceAssocItem.set("createdStamp",         now);
        provinceAssocItem.set("createdTxStamp",       now);

        delegator.create(provinceItem);
        delegator.create(provinceAssocItem);
    }

}

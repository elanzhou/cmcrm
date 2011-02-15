package com.elanzone.cmcrm.tools.geo;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.authz.Authorization;
import org.ofbiz.service.LocalDispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
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

        File uploadFile = FileUtil.getFile("runtime/tmp");
        ServletFileUpload dfu = new ServletFileUpload(new DiskFileItemFactory(10240, uploadFile));
        java.util.List lst = null;
        try {
            lst = dfu.parseRequest(request);
        } catch (FileUploadException e4) {
            request.setAttribute("_ERROR_MESSAGE_", e4.getMessage());
            Debug.logError("[UploadContentAndImage.uploadContentAndImage] " + e4.getMessage(), module);
            return "error";
        }
        //if (Debug.infoOn()) Debug.logInfo("[UploadContentAndImage]lst " + lst, module);

        if (lst.size() == 0) {
            String errMsg = UtilProperties.getMessage(UploadContentAndImage.err_resource, "uploadContentAndImage.no_files_uploaded", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            Debug.logWarning("[DataEvents.uploadImage] No files uploaded", module);
            return "error";
        }

        Map passedParams = FastMap.newInstance();
        FileItem fi = null;
        FileItem imageFi = null;
        byte[] imageBytes = {};
        for (int i = 0; i < lst.size(); i++) {
            fi = (FileItem) lst.get(i);
            //String fn = fi.getName();
            String fieldName = fi.getFieldName();
            if (fi.isFormField()) {
                String fieldStr = fi.getString();
                passedParams.put(fieldName, fieldStr);
            } else if (fieldName.equals("imageData")) {
                imageFi = fi;
                imageBytes = imageFi.get();
            }
        }
        if (Debug.infoOn()) Debug.logInfo("[UploadContentAndImage]passedParams: " + passedParams, module);

        String countryId = passedParams.get("country");

        Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + uploadFile.getAbsolutePath()  + ";DriverID=22;READONLY=true}";
        Connection connection = java.sql.DriverManager.getConnection(database, "", "");
        Statement statement = connection.createStatement();
        String provinceSql = "select * from province";
        ResultSet rs = statement.execute(provincSql);



        return "success";
    }

}

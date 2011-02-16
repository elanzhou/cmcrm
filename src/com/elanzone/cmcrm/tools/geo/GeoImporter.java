package com.elanzone.cmcrm.tools.geo;

import com.elanzone.cmcrm.model.City;
import com.elanzone.cmcrm.model.County;
import com.elanzone.cmcrm.model.Province;
import javolution.util.FastMap;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.FileUtil;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
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

        final File uploadFile = FileUtil.getFile("runtime/tmp");
        ServletFileUpload dfu = new ServletFileUpload(new DiskFileItemFactory(10240, uploadFile));
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
        FileItem imageFi = null;
        byte[] imageBytes = {};
        for (Object aLst : lst) {
            fi = (FileItem) aLst;
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

        String country = "";
        if (passedParams.containsKey("country")) {
            country = (String)passedParams.get("country");
        }
        if (country.isEmpty()) {
            String errMsg = UtilProperties.getMessage(GeoImporter.err_resource, "geoimport.not_specify_country", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            Debug.logInfo("[GeoImporter.importCountryData] not specify country", module);
            return "error";
        }
        // todo: check country is valid

        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        } catch (ClassNotFoundException e) {
            String errMsg = UtilProperties.getMessage(GeoImporter.err_resource, "geoimport.no_jdbc_driver", locale);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            Debug.logError("[GeoImporter.importCountryData] not specify country", module);
            return "error";
        }

        Map<String, Province> provinces = new LinkedHashMap<String, Province>();
        Map<String, City> citys = new LinkedHashMap<String, City>();
        Map<String, County> countys = new LinkedHashMap<String, County>();

        String jdbcUrl ="jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ=" + uploadFile.getAbsolutePath();
        String provinceSql = "select * from province";
        String citySql = "select * from city";
        String countySql = "select * from area";
        Connection conn = null;
        try {
            conn= DriverManager.getConnection(jdbcUrl);
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(provinceSql);
            while (rs.next()) {
                String provinceId = rs.getString("provinceID");
                String provinceName = rs.getString("province");
                Province province = new Province(provinceId, provinceName);
                provinces.put(provinceId, province);
            }

            rs = stat.executeQuery(citySql);
            while (rs.next()) {
                String id = rs.getString("cityID");
                String name = rs.getString("city");
                String provinceId = rs.getString("father");
                Province province = provinces.get(provinceId);
                City city = new City(id, name, province);
                citys.put(id, city);
            }

            rs = stat.executeQuery(countySql);
            while (rs.next()) {
                String id = rs.getString("areaID");
                String name = rs.getString("area");
                String cityId = rs.getString("father");
                City city = citys.get(cityId);
                County county = new County(id, name, city);
                countys.put(id, county);
            }
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (conn!=null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }

        for (Map.Entry<String, Province> item : provinces.entrySet()) {
            Province province = item.getValue();
            String geoId = country + "_" + province.getId();

            Map<String, Object> data = FastMap.newInstance();
            
            data.put("geoId", geoId);
//            delegator.create("Geo", )
        }

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

}

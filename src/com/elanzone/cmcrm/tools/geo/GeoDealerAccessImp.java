package com.elanzone.cmcrm.tools.geo;

import com.elanzone.cmcrm.model.City;
import com.elanzone.cmcrm.model.County;
import com.elanzone.cmcrm.model.Province;
import org.ofbiz.entity.Delegator;

import java.io.File;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class GeoDealerAccessImp extends GeoDealer {

    private String fileName;

    public GeoDealerAccessImp(Delegator delegator) {
        super(delegator);
    }

    @Override
    public String analyzeData() throws Exception {
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        } catch (ClassNotFoundException e) {
            return "geoimport.no_jdbc_driver";
        }

        File mdbFile = new File(fileName);

        String jdbcUrl ="jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ=" + mdbFile.getAbsolutePath();
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
                List<City> pcItem = provinceCitys.get(provinceId);
                if (pcItem == null) {
                    pcItem = new LinkedList<City>();
                    provinceCitys.put(provinceId, pcItem);
                }
                provinceCitys.get(provinceId).add(city);
                citys.put(id, city);
            }

            rs = stat.executeQuery(countySql);
            while (rs.next()) {
                String id = rs.getString("areaID");
                String name = rs.getString("area");
                String cityId = rs.getString("father");
                City city = citys.get(cityId);
                County county = new County(id, name, city);
                List<County> ccItem = cityCountys.get(cityId);
                if (ccItem == null) {
                    ccItem = new LinkedList<County>();
                    cityCountys.put(cityId, ccItem);
                }
                cityCountys.get(cityId).add(county);
                countys.put(id, county);
            }
//        } catch (SQLException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            return "geoimport.exception_caught";
        } finally {
            if (conn!=null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        return "";
    }


    //////////////////////////////////////////////////////

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}

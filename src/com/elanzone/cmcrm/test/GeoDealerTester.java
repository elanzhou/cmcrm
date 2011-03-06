package com.elanzone.cmcrm.test;

import com.elanzone.cmcrm.model.City;
import com.elanzone.cmcrm.model.County;
import com.elanzone.cmcrm.model.Province;
import com.elanzone.cmcrm.tools.geo.GeoDealerAccessImp;
import junit.framework.TestCase;

import java.util.List;


public class GeoDealerTester extends TestCase {

    public void testAnalyzeData() {
        GeoDealerAccessImp geoDealer = new GeoDealerAccessImp(null);
        
        geoDealer.setFileName("docs/geo_data/zh_CN.mdb");
        geoDealer.analyzeData();
        System.out.println("province count: " + geoDealer.getProvinces().size());
        System.out.println("city count: " + geoDealer.getCitys().size());
        System.out.println("county count: " + geoDealer.getCountys().size());
        
        for (String provinceId : geoDealer.getProvinces().keySet()) {
            Province province = geoDealer.getProvinces().get(provinceId);
            List<City> citys = geoDealer.getProvinceCitys().get(provinceId);
            if (citys==null) {
                System.out.println("province id: " + provinceId + ", name: " + province.getName() + ", no citys.");
                continue;
            }
            System.out.println("province id: " + provinceId + ", name: " + province.getName() + ", city count: " + citys.size());

            for (City city : citys) {
                List<County> countys = geoDealer.getCityCountys().get(city.getId());
                if (countys == null) {
                    System.out.println("  city id: " + city.getId() + ", name: " + city.getName() + ", no county.");
                }
                else {
                    System.out.println("  city id: " + city.getId() + ", name: " + city.getName() + ", county count: " + countys.size());
                }
            }
        }
    }
}

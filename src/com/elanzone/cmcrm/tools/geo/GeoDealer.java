package com.elanzone.cmcrm.tools.geo;

import com.elanzone.cmcrm.model.City;
import com.elanzone.cmcrm.model.County;
import com.elanzone.cmcrm.model.Province;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class GeoDealer {

    private Delegator delegator;

    protected Map<String, Province> provinces;
    protected Map<String, City> citys;
    protected Map<String, List<City>> provinceCitys;
    protected Map<String, County> countys;
    protected Map<String, List<County>> cityCountys;


    public GeoDealer(Delegator delegator) {
        this.delegator = delegator;

        provinces = new LinkedHashMap<String, Province>();
        citys = new LinkedHashMap<String, City>();
        provinceCitys = new LinkedHashMap<String, List<City>>();
        countys = new LinkedHashMap<String, County>();
        cityCountys = new LinkedHashMap<String, List<County>>();
    }

    public abstract String analyzeData() throws Exception;

    public void createGeoItems(String country) throws GenericEntityException {
        Timestamp now = UtilDateTime.nowTimestamp();
        String countryGeoId = "CHN";
        for (Map.Entry<String, Province> item : provinces.entrySet()) {
            Province province = item.getValue();
            String provinceGeoId = country + "_" + province.getId();

            createGeoItem(countryGeoId, provinceGeoId, province.getName(), "PROVINCE", now);

            List<City> theCitys = provinceCitys.get(province.getId());
            if (theCitys == null) continue;

            for (City city : theCitys) {
                String cityGeoId = provinceGeoId + "_" + city.getId();
                createGeoItem(provinceGeoId, cityGeoId, city.getName(), "CITY", now);
                List<County> theCountys = cityCountys.get(city.getId());
                if (theCountys == null) continue;

                for (County county : theCountys) {
                    String countyGeoId = cityGeoId + "_" + county.getId();

                    createGeoItem(cityGeoId, countyGeoId, county.getName(), "COUNTY", now);
                }
            }
        }
    }


    private void createGeoItem(String parentGeoId,
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

    //////////////////////////////////////////////////////////////////////////////////////

    public Map<String, Province> getProvinces() {
        return provinces;
    }

    public Map<String, City> getCitys() {
        return citys;
    }

    public Map<String, List<City>> getProvinceCitys() {
        return provinceCitys;
    }

    public Map<String, County> getCountys() {
        return countys;
    }

    public Map<String, List<County>> getCityCountys() {
        return cityCountys;
    }
}

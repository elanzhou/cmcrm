package com.elanzone.cmcrm.model;

public class City {
    private String id;
    private String name;
    private Province province;

    public City() {
    }

    public City(String id, String name, Province province) {
        this.id = id;
        this.name = name;
        this.province = province;
    }

//////////////////////////////////////////

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }
}

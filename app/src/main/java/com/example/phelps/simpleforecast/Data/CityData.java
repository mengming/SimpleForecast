package com.example.phelps.simpleforecast.Data;

import java.io.Serializable;

/**
 * Created by Phelps on 2016/8/20.
 */
public class CityData implements Serializable{
    /**
     * city : 北京
     * cnty : 中国
     * id : CN101010100
     * lat : 39.904000
     * lon : 116.391000
     * prov : 直辖市
     */

    private String city;
    private String cnty;
    private String id;
    private double lat;
    private double lon;
    private String prov;
    public String getProv() {
        return this.prov;
    }
    public void setProv(String prov) {
        this.prov = prov;
    }
    public double getLon() {
        return this.lon;
    }
    public void setLon(double lon) {
        this.lon = lon;
    }
    public double getLat() {
        return this.lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCnty() {
        return this.cnty;
    }
    public void setCnty(String cnty) {
        this.cnty = cnty;
    }
    public String getCity() {
        return this.city;
    }
    public void setCity(String city) {
        this.city = city;
    }
}

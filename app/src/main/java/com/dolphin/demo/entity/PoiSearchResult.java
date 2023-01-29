package com.dolphin.demo.entity;

/**
 * 定位搜索后返回的结果类
 * @author liusixiang
 * @date 2022.9.22
 */
public class PoiSearchResult {

    /** 地址类型 */
    private String types;
    /** 周边信息 */
    private String periphery;
    /** 地名 */
    private String adress;
    /** 经度 */
    private Double latitude;
    /** 纬度 */
    private Double longitudel;

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitudel() {
        return longitudel;
    }

    public void setLongitudel(Double longitudel) {
        this.longitudel = longitudel;
    }

    public String getPeriphery() {
        return periphery;
    }

    public void setPeriphery(String periphery) {
        this.periphery = periphery;
    }
    public PoiSearchResult(double lng, double lat, String name, String type, String periphery) {
        this.latitude = lat;
        this.longitudel = lng;
        this.adress = name;
        this.types = type;
        this.periphery = periphery;
    }
}

package com.pma.chat.pmaChat.model;

/**
 * Created by Bojan on 6/15/2017.
 */

public class MapModel {
    private String latitude;
    private String longitude;

    public MapModel() {}

    public MapModel(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {this.latitude = latitude;}
    public String getLatitude() {return latitude;}

    public void setLongitude(String longitude) {this.longitude = longitude;}
    public String getLongitude() {return longitude;}
}

package com.example.coalert;

import androidx.lifecycle.ViewModel;

import java.io.BufferedOutputStream;

public class SharedViewModel extends ViewModel {
    private String largeloc = "";
    private String smallloc = "";
    private int time = 0;
    private boolean here = false;
    private boolean settin = false;
    private boolean first = true;
    private double latitude = 37.56000;
    private double longitude = 126.97000;
    private float zoomLevel = 15;

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public double getLatitude(){
        return latitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
    public double getLongitude(){
        return longitude;
    }
    public void setZoomLevel(float zoomLevel){
        this.zoomLevel = zoomLevel;
    }
    public float getZoomLevel(){
        return zoomLevel;
    }


    public boolean getFirst() {
        return first;
    }

    public void setFirst(boolean first){
        this.first = first;
    }

    public void setSettin(Boolean settin){
        this.settin = settin;
    }

    public Boolean getSettin(){
        return settin;
    }

    public int getTime() {
        return time;
    }

    public String getLargeloc() {
        return largeloc;
    }

    public String getSmallloc() {
        return smallloc;
    }

    public boolean getHere(){
        return here;
    }

    public void setLargeloc(String largeloc){
        this.largeloc = largeloc;
    }


    public void setSmallloc(String smallloc){
        this.smallloc = smallloc;
    }

    public void setTime(int time){
        this.time = time;
    }

    public void setHere(boolean here){
        this.here = here;
    }
}

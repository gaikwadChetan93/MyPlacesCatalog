package bean;

import java.io.Serializable;

/**
 * Created by chegaikw on 5/31/2016.
 */
public class LocationDTO implements Serializable{
    private String mLocationName;
    private double mLat;
    private double mLong;
    private int mLocationImage;
    private int mLocationID;
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getmLocationID() {
        return mLocationID;
    }

    public void setmLocationID(int mLocationID) {
        this.mLocationID = mLocationID;
    }

    public int getmLocationImage() {
        return mLocationImage;
    }

    public void setmLocationImage(int mLocationImage) {
        this.mLocationImage = mLocationImage;
    }

    public String getmLocationName() {
        return mLocationName;
    }

    public void setmLocationName(String mLocationName) {
        this.mLocationName = mLocationName;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLong() {
        return mLong;
    }

    public void setmLong(double mLong) {
        this.mLong = mLong;
    }
}

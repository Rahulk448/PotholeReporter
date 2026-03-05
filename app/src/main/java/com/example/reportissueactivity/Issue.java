package com.example.reportissueactivity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Issue implements Parcelable {

    // Fields received from backend JSON
    private String title;
    private String description;
    private double latitude;
    private double longitude;
    private String issue_type;
    private String status;

    // Fields used in Android UI
    private Bitmap image;
    private LatLng location;

    // Constructor used when reporting issue locally
    public Issue(String issueType, String description, Bitmap image, LatLng location) {
        this.issue_type = issueType;
        this.description = description;
        this.image = image;
        this.location = location;

        if (location != null) {
            this.latitude = location.latitude;
            this.longitude = location.longitude;
        }

        this.status = "Pending";
        this.title = issueType;
    }

    // Constructor used by Retrofit when loading from backend
    public Issue(String title, String description, double latitude, double longitude, String issue_type, String status) {
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.issue_type = issue_type;
        this.status = status;

        this.location = new LatLng(latitude, longitude);
    }

    protected Issue(Parcel in) {
        title = in.readString();
        description = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        issue_type = in.readString();
        status = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
        location = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<Issue> CREATOR = new Creator<Issue>() {
        @Override
        public Issue createFromParcel(Parcel in) {
            return new Issue(in);
        }

        @Override
        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getIssueType() {
        return issue_type;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getImage() {
        return image;
    }

    public LatLng getLocation() {
        if (location == null) {
            location = new LatLng(latitude, longitude);
        }
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(issue_type);
        dest.writeString(status);
        dest.writeParcelable(image, flags);
        dest.writeParcelable(location, flags);
    }
}
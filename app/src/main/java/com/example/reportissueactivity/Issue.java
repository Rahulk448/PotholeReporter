package com.example.reportissueactivity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Issue implements Parcelable {
    private String issueType;
    private String description;
    private Bitmap image;
    private LatLng location;
    private String status;

    public Issue(String issueType, String description, Bitmap image, LatLng location) {
        this.issueType = issueType;
        this.description = description;
        this.image = image;
        this.location = location;
        this.status = "Pending"; // Default status
    }

    protected Issue(Parcel in) {
        issueType = in.readString();
        description = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
        location = in.readParcelable(LatLng.class.getClassLoader());
        status = in.readString();
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

    public String getIssueType() {
        return issueType;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getImage() {
        return image;
    }

    public LatLng getLocation() {
        return location;
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
        dest.writeString(issueType);
        dest.writeString(description);
        dest.writeParcelable(image, flags);
        dest.writeParcelable(location, flags);
        dest.writeString(status);
    }
}

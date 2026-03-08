package com.example.reportissueactivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Issue implements Parcelable {
    @SerializedName("_id")
    private String mongoId;

    @SerializedName("id")
    private String simpleId;

    @SerializedName("issue_type")
    private String issueType;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("image_url")
    private String imageUrl; // This stores the Base64 string from backend
    
    private Bitmap image;
    
    @SerializedName("latitude")
    private double latitude;
    
    @SerializedName("longitude")
    private double longitude;

    @SerializedName("location_name")
    private String locationName;
    
    @SerializedName("status")
    private String status;

    public Issue(String issueType, String description, Bitmap image, LatLng location) {
        this.issueType = issueType;
        this.description = description;
        this.image = image;
        if (location != null) {
            this.latitude = location.latitude;
            this.longitude = location.longitude;
        }
        this.status = "Pending";
    }

    protected Issue(Parcel in) {
        mongoId = in.readString();
        simpleId = in.readString();
        issueType = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        image = in.readParcelable(Bitmap.class.getClassLoader());
        latitude = in.readDouble();
        longitude = in.readDouble();
        locationName = in.readString();
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

    public String getId() {
        return mongoId != null ? mongoId : simpleId;
    }

    public String getIssueType() {
        return issueType;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getImage() {
        if (image != null) return image;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(imageUrl, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }

    public String getLocationName() {
        return locationName;
    }

    public String getStatus() {
        return status != null ? status : "Pending";
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
        dest.writeString(mongoId);
        dest.writeString(simpleId);
        dest.writeString(issueType);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeParcelable(image, flags);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(locationName);
        dest.writeString(status);
    }
}

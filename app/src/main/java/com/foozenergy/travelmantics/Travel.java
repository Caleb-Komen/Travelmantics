package com.foozenergy.travelmantics;

import android.os.Parcel;
import android.os.Parcelable;

public class Travel implements Parcelable {

    String id;
    String title;
    String price;
    String description;
    String photoUrl;
    String photoName;

    public Travel() {
    }

    public Travel(String title, String price, String description, String photoUrl, String photoName) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.photoUrl = photoUrl;
        this.photoName = photoName;
    }

    protected Travel(Parcel in) {
        id = in.readString();
        title = in.readString();
        price = in.readString();
        description = in.readString();
        photoUrl = in.readString();
    }

    public static final Creator<Travel> CREATOR = new Creator<Travel>() {
        @Override
        public Travel createFromParcel(Parcel in) {
            return new Travel(in);
        }

        @Override
        public Travel[] newArray(int size) {
            return new Travel[size];
        }
    };

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(price);
        parcel.writeString(description);
        parcel.writeString(photoUrl);
    }
}

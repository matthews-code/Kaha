package com.example.kahaapplication;

public class SpaceUpload {
    private String mType;
    private String mLocation;
    private String mImageUrl;

    public SpaceUpload() {

    }

    public SpaceUpload(String type, String location, String imageUrl) {
        mType = type;
        mLocation = location;
        mImageUrl = imageUrl;
    }

    public String getSpaceType() { return mType; }
    public String getSpaceLocation() { return mLocation; }
    public String getSpaceImageUrl() { return mImageUrl; }

    public void setSpaceType(String type) { mType = type; }
    public void setSpaceLocation(String location) { mLocation = location; }
    public void setSpaceImageUrl(String imageUrl) { mImageUrl = imageUrl; }
}

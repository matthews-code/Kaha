package com.example.kahaapplication;

public class SpaceUpload {
    private String mType;
    private String mLength;
    private String mWidth;
    private String mHeight;
    private String mLocation;
    private String mMonthly;
    private String mDescription;
    private String mImageUrl;

    public SpaceUpload() {

    }

    public SpaceUpload(String type, String length,
                       String width, String height,
                       String location, String monthly,
                       String description, String imageUrl) {
        mType = type;
        mLength = length;
        mWidth = width;
        mHeight = height;
        mLocation = location;
        mMonthly = monthly;
        mDescription = description;
        mImageUrl = imageUrl;
    }

    public String getSpaceType() { return mType; }
    public String getSpaceLength() { return mLength; }
    public String getSpaceWidth() { return mWidth; }
    public String getSpaceHeight() { return mHeight; }
    public String getSpaceLocation() { return mLocation; }
    public String getSpaceMonthly() { return mMonthly; }
    public String getSpaceDescription() { return mDescription; }
    public String getSpaceImageUrl() { return mImageUrl; }

    public void setSpaceType(String type) { mType = type; }
    public void setSpaceLength(String length) { mLength = length; }
    public void setSpaceWidth(String width) { mWidth = width; }
    public void setSpaceHeight(String height) { mHeight = height; }
    public void setSpaceLocation(String location) { mLocation = location; }
    public void setSpaceMonthly(String monthly) { mMonthly = monthly; }
    public void setSpaceDescription(String description) { mDescription = description; }
    public void setSpaceImageUrl(String imageUrl) { mImageUrl = imageUrl; }
}

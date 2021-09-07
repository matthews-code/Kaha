package com.example.kahaapplication;

public class SpaceUpload {
    private String spaceType;
    private String spaceLength;
    private String spaceWidth;
    private String spaceHeight;
    private String spaceLocation;
    private String spaceMonthly;
    private String spaceDescription;
    private String spaceImageUrl;
    private String spaceHost;
    private String spaceHostId;
    private String spaceUploadId;

    public SpaceUpload(String type, String length,
                       String width, String height,
                       String location, String monthly,
                       String description, String imageUrl,
                       String host, String id, String uploadId) {

        spaceType = type;
        spaceLength = length;
        spaceWidth = width;
        spaceHeight = height;
        spaceLocation = location;
        spaceMonthly = monthly;
        spaceDescription = description;

        spaceImageUrl = imageUrl;
        spaceHost = host;
        spaceHostId = id;
        spaceUploadId = uploadId;
    }

    public String getSpaceType() { return spaceType; }
    public String getSpaceLength() { return spaceLength; }
    public String getSpaceWidth() { return spaceWidth; }
    public String getSpaceHeight() { return spaceHeight; }
    public String getSpaceLocation() { return spaceLocation; }
    public String getSpaceMonthly() { return spaceMonthly; }
    public String getSpaceDescription() { return spaceDescription; }
    public String getSpaceImageUrl() { return spaceImageUrl; }
    public String getSpaceHost() { return spaceHost; }
    public String getSpaceHostId() { return spaceHostId; }
    public String getSpaceUploadId() { return spaceUploadId; }
}

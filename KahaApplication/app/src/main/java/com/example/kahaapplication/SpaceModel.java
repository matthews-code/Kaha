package com.example.kahaapplication;

public class SpaceModel {
    private int spaceImage;
    private float length, width, height, price;
    private String location, host, type;

    public SpaceModel(int spaceImage, float length, float width, float height,
                      float price, String host, String type, String location) {
        this.spaceImage = spaceImage;
        this.length = length;
        this.width = width;
        this.height = height;
        this.price = price;
        this.host = host;
        this.type = type;
        this.location = location;
    }

    public int getSpaceImage() {
        return spaceImage;
    }

    public float getLength() { return length; }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    public String getHost() {
        return host;
    }

    public String getType() {
        return type;
    }
}

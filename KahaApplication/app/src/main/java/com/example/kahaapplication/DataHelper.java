package com.example.kahaapplication;

import java.util.ArrayList;

public class DataHelper {

    public static ArrayList<SpaceUpload> initData() {
        ArrayList<SpaceUpload> data = new ArrayList<>();
        data.add(new SpaceUpload(
                "Garage",
                "12",
                "120",
                "5",
                "Las Pinas",
                "15,000",
                "A nice storage space",
                "Image",
                "Matt",
                "SAMPLE ID",
                "SAMPLE UPLOAD ID",
                "public",
                "0",
                "0"

        ));
        data.add(new SpaceUpload(
                "Shed",
                "14",
                "15",
                "3",
                "Makati",
                "5,000",
                "Perfect for your small items",
                "Image",
                "Matt",
                "SAMPLE ID",
                "SAMPLE UPLOAD ID",
                "public",
                "0",
                "0"
        ));
        return data;
    }

    public static ArrayList<String> initData2() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Beaverly");
        data.add("Matt Buensalida");
        data.add("Beaverly");
        data.add("Matt Buensalida");
        data.add("Beaverly");
        data.add("Matt Buensalida");
        data.add("Beaverly");
        data.add("Matt Buensalida");
        return data;
    }

}

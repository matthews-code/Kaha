package com.example.kahaapplication;

import java.util.ArrayList;

public class DataHelper {

    public static ArrayList<SpaceModel> initData() {
        ArrayList<SpaceModel> data = new ArrayList<>();
        data.add(new SpaceModel(
                R.drawable.sample_garage,
                12,
                20,
                8,
                20000,
                "Matthew Buensalida",
                "Garage",
                "Muntinlupa"
        ));
        data.add(new SpaceModel(
                R.drawable.sample_garage2,
                8,
                8,
                5,
                12500,
                "Jacob Darvin",
                "Garage",
                "Quezon"
        ));
        data.add(new SpaceModel(
                R.drawable.sample_shed,
                5,
                5,
                10,
                3000,
                "Joshua Ranjo",
                "Shed",
                "Makati"
        ));
        data.add(new SpaceModel(
                R.drawable.sample_warehouse,
                100,
                200,
                60,
                65000,
                "Clark Kent",
                "Warehouse",
                "Pasig"
        ));

        return data;
    }

}

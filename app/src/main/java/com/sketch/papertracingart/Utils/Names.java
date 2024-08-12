package com.sketch.papertracingart.Utils;

import java.util.ArrayList;
import java.util.List;

public class Names {

    public static String Class_Animals = "png_Animals";
    public static String Class_birds = "png_Birds";
    public static String Class_butterfly = "png_Butterfly";
    public static String Class_cafe = "png_Cafe";
    public static String Class_cartoons = "png_Cartoons";
    public static String Class_christmas = "png_Christmas";
    public static String Class_festivals = "png_Festivals";
    public static String Class_flowers = "png_Flowers";
    public static String Class_fruits = "png_Fruits";
    public static String Class_general = "png_General";

    public static String Class_mehndi = "png_Mehndi";
    public static String Class_nature = "png_Nature";
    public static String Class_properties = "png_Properties";
    public static String Class_rangoli = "png_Rangoli";
    public static String Class_sports = "png_Sports";
    public static String Class_vehicles = "png_Vehicles";

    public static List<String> getAllDir() {
        ArrayList<String> list = new ArrayList<>();
        list.add(Class_Animals);
        list.add(Class_birds);
        list.add(Class_butterfly);
        list.add(Class_cafe);
        list.add(Class_cartoons);
        list.add(Class_christmas);
        list.add(Class_festivals);
        list.add(Class_flowers);
        list.add(Class_fruits);
        list.add(Class_general);
        list.add(Class_mehndi);
        list.add(Class_nature);
        list.add(Class_properties);
        list.add(Class_rangoli);
        list.add(Class_sports);
        list.add(Class_vehicles);
        return list;


    }

}

package com.one.apperz.playandshine.helperLord;

import java.util.ArrayList;
import java.util.Arrays;

public class HelperLordConstant {
    public static int DISPLAY_PROFILE_SEARCH = 0;
    public static int DISPLAY_PROFILE_REQUEST = 1;
    public static int DISPLAY_PROFILE_CHAT = 2;

    public static ArrayList<String> TYPE_OF_PROFESSIONAL = new ArrayList<>(Arrays.asList("coach",
            "physical trainer","nutritionist","dietician","psychologist","psychotherapist","mentor","other"));
    public static ArrayList<String> LIST_OF_SPORTS = new ArrayList<>(Arrays.asList("Acrobatic Gymnastics",
            "Archery","Artistic Gymnastics","Athletics","Badminton","Basketball","Boxing","Diving","Fencing",
            "Football","Futsal","Golf","Handball","Hockey","Judo","Karate","Mountain Biking","Paralympic",
            "Rhythmic Gymnastics","Road Cycling","Roller Skating","Shooting","Short Track Speed Skating",
            "Ski Mountaineering","Swimming","Table Tennis","Taekwondo","Tennis","Track Cycling","Volleyball","Waterpolo","WeightLifting","Wrestling"));

    public static ArrayList<String> LEVEL_OF_ATHLETE = new ArrayList<>(Arrays.asList("Select your level in the Sport" +
                    "","School level",
            "College level","City level","District level","State level","National level",
            "International level"));
    public static ArrayList<String> YEARS_OF_EXPERIENCE = new ArrayList<>(Arrays.asList("Select the Years of Experience","less than 1 year","1-2 year",
            "3-5 years","5-10 years","10+ years"));

}
package com.parse.starter;

/**
 * Created by Rebecca_2 on 4/15/2015.
 * This class is a holder for each SpecialsItem.
 */
public class SpecialsItem {
    String title;
    String desc;
    String endDate;
    String truckName;

    //constructor
    public SpecialsItem(String title, String desc, String endDate, String truckName) {
        this.title = title;
        this.desc = desc;
        this.endDate = endDate;
        this.truckName = truckName;
    }

}

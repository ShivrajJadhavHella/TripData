package com.example.tripsbarchart;

public class DataClass {

    String date;
    String event_name;
    String value;

    public DataClass(String date,String event_name,String value){
        this.date = date;
        this.event_name = event_name;
        this.value = value;
    }

    public String getDate(){return date;}
    public String getEvent_name(){return event_name;}
    public String getValue(){return value;}
}

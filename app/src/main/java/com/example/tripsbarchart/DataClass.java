package com.example.tripsbarchart;

public class DataClass {

    private String date;
    private String event_name;
    private String value;
    private String trip_no;

    public DataClass(String date,String event_name,String value, String trip_no){
        this.date = date;
        this.event_name = event_name;
        this.value = value;
        this.trip_no = trip_no;
    }

    public String getDate(){return date;}
    public String getEvent_name(){return event_name;}
    public String getValue(){return value;}
    public String getTrip_no(){return trip_no;}
}

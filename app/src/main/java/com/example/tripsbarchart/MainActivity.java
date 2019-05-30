package com.example.tripsbarchart;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private ArrayList<DataClass> data_array =  new ArrayList<>();

    private HashMap<Integer,Float> score_trips_array = new HashMap<>();

    private HashMap<Integer,Float> hb = new HashMap<>();

    private HashMap<Integer,Float> ha = new HashMap<>();

    private HashMap<Integer,Float> dos = new HashMap<>();

    private HashMap<Integer,Float> os = new HashMap<>();

    private String current_date;

    final String x_Dates[] = new String[10];

    //ArrayList<String> x_label = new ArrayList<>();

    private String TAG ="Main Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        current_date = getCurrentTime();

        String value = substract_required_days(10);
        Log.i("Main Activity", current_date);
        //Log.i("Main Activity", current_date.substring(0,6));
        //Log.i("Main Activity", value);

        new GetData().execute();




    }



    public String getCurrentTime() {
        String dateFormat = "yyyy-MM-dd";
        String ourdate;

        try {

            Date value = new Date();
            TimeZone timeZone = TimeZone.getTimeZone("Asia/Kolkata");
            SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.UK); //this format changeable
            dateFormatter.setTimeZone(timeZone);
            ourdate = dateFormatter.format(value);
            //ourdate = ourdate.substring(11,19);
            //Log.d("OurDate", OurDate);
        } catch (Exception e) {
            ourdate = "00/00/0000 00:00:00";
        }
        return ourdate;
    }


    public String substract_required_days(int n){

        String dateFormat = "ddMMyy";
        String ourdate;

        /*Date dateBefore30Days = DateUtils.addDays(new Date(),-30);
        Date daysAgo = new DateTime(new Date()).minusDays(300).toDate();*/

        Calendar cal = GregorianCalendar.getInstance();
        cal.add( Calendar.DAY_OF_YEAR, -n);
        Date past_date = cal.getTime();

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Kolkata");
        SimpleDateFormat dateFormatter = new SimpleDateFormat(dateFormat, Locale.UK); //this format changeable
        dateFormatter.setTimeZone(timeZone);
        ourdate = dateFormatter.format(past_date);

        Log.i("Main Activity", "Date:" + ourdate);

        return ourdate;


    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        BarEntry be =(BarEntry)e;

        Log.i("Inside Click Value","Label:" + e.getX());

        extract_day_values(x_Dates[Math.round(e.getX())]);

    }

    @Override
    public void onNothingSelected() {

    }


    private class GetData extends AsyncTask<Void, Void, String> {

        private String request_url_base = "https://version2-0.azurewebsites.net/api/Data-Last7Days?code=SRBSZmAzPMRkrSWIuvPYmu/0pB8b8MwOMFSNTYajfyQQ/F00LNdboQ==&name=";
        private String request_url = request_url_base + current_date + "T18:30:00.000Z";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            //progressBar.setVisibility(View.VISIBLE);
            //Toast.makeText(getContext(), "Loading", Toast.LENGTH_SHORT).show();
            //progressBar.setVisibility(View.VISIBLE);
            //progressOverlay.setVisibility(View.VISIBLE);
            //swipeRefreshLayout.setRefreshing(true);
            Log.i("Main Activity","Loading");


        }

        @Override
        protected String doInBackground(Void... arg0) {

            Log.i("Main Activity","Loading in Background");

            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String json_str = sh.makeServiceCall(request_url);

            //Log.e("Main Activity", "Response from url: " + json_str);


            return json_str;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog

            //swipeRefreshLayout.setRefreshing(false);
            //progressBar.setVisibility(View.GONE);

            //progressOverlay.setVisibility(View.INVISIBLE);

            if (result != null) {

                try {

                    //Toast.makeText(MainNavigationDrawer.this, "Inside try", Toast.LENGTH_LONG).show();
                    //JSONObject jsonObject = new JSONArray(result).getJSONObject(0);
                    JSONArray jsonArray = new JSONArray(result);

                    Log.i("Main Activity", "Length:" + jsonArray.length());
                    //Toast.makeText(MainNavigationDrawer.this, "Value" + jsonArray.length(), Toast.LENGTH_LONG).show();

                    for(int i=0;i<jsonArray.length();i++){

                        String value_1 = jsonArray.getJSONObject(i).getJSONObject("Value1").getString("_");

                        String value_2 = jsonArray.getJSONObject(i).getJSONObject("Value3").getString("_");

                        String value_3 = jsonArray.getJSONObject(i).getJSONObject("Value4").getString("_");

                        String value_4 = jsonArray.getJSONObject(i).getJSONObject("Value5").getString("_");

                        //Log.i("Main Activity","Object:" + jsonObject);
                        data_array.add(new DataClass(value_1,value_2,value_3,value_4));

                        Log.i("Main Activity","Value1:" + value_1 + "Value2:" + value_2 + "Value3:" + value_3 + "Value4:" + value_4);
                    }

                    plotbarcharts();
                } catch (final JSONException e) {
                    Log.e("Main Activity", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e("Main Activity", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

        }
    }

    private void plotbarcharts(){

        BarChart barChart = findViewById(R.id.barChart);

        ArrayList<BarEntry> dataVals = new ArrayList<>();



        Log.i("Main Activity","Size:" + data_array.size());

        for(int i=7;i>0;i--){

            String past_Date = substract_required_days(i);

            Log.i(TAG,"Past Date:" + past_Date);


            float value =0;
            int index_no = -(i-7);
            x_Dates[index_no] = past_Date;
            for(int j=0;j<data_array.size();j++){


                if(data_array.get(j).getDate().equals(past_Date)){



                    if(data_array.get(j).getEvent_name().equals("Total_Score")) {



                        value = Float.parseFloat(data_array.get(j).getValue());
                        Log.i("Inside Score_Trip","Index:" + index_no + "Value:" + data_array.get(j).getValue());
                        dataVals.add(new BarEntry(index_no,value));
                    }


                }

            }

            if(value == 0){
                value = 0;
                Log.i("Outside Score_Trip","Index:" + index_no + "Value:" + value);
                dataVals.add(new BarEntry(index_no,value));
            }
        }

        BarDataSet dataSet = new BarDataSet(dataVals, "Daily Score");
        dataSet.setColor(Color.rgb(15,35,100));
        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);

        barData.setBarWidth(0.6f);

        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setXOffset(0);
        xAxis.setYOffset(0);

        xAxis.setValueFormatter(new IAxisValueFormatter() {

            //private String[] qualities = new String[]{"1","2","3","4","5","6","7"};
            @Override

            public String getFormattedValue(float value, AxisBase axis) {
                return x_Dates[(int)value % x_Dates.length];
            }
        });

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setAxisLineWidth(3f);

        YAxis yl = barChart.getAxisLeft();
        yl.setAxisMinimum(-4);
        barChart.getLegend().setEnabled(false);
        barChart.setOnChartValueSelectedListener(this);
        barchart_properties(barChart);
    }


    private void  extract_day_values(String value){


        Log.i("Inside extract day","value:" + data_array.get(0).getValue());
        Log.i("Date_Value", value);
        ha.clear();hb.clear();dos.clear();os.clear();score_trips_array.clear();
        for(int j=0;j<data_array.size();j++){


            if(data_array.get(j).getDate().equals(value)){

                Log.i("Inside extract day","value:" + data_array.get(j).getValue());
                Log.i("Value of j","J:" + j);
                    switch(data_array.get(j).getEvent_name()){

                        case "Score_Trip":

                            score_trips_array.put(Integer.parseInt(data_array.get(j).getTrip_no()),Float.parseFloat(data_array.get(j).getValue()));
                            break;

                        case "Harsh_Braking":
                            Log.i("HB","Value:" +  Integer.parseInt(data_array.get(j).getValue()));
                            hb.put(Integer.parseInt(data_array.get(j).getTrip_no()), Float.parseFloat(data_array.get(j).getValue()));
                            break;

                        case "Harsh_Acc":
                            Log.i("HA","Value:" +  Float.parseFloat(data_array.get(j).getValue()));
                            ha.put(Integer.parseInt(data_array.get(j).getTrip_no()), Float.parseFloat(data_array.get(j).getValue()));
                            break;

                        case "Dangerous_Overspeeding":
                            Log.i("DOS","Value:" +  Float.parseFloat(data_array.get(j).getValue()));
                            dos.put(Integer.parseInt(data_array.get(j).getTrip_no()), Float.parseFloat(data_array.get(j).getValue()));
                            break;

                        case "Overspeeding":
                            Log.i("OS","Value:" +  Float.parseFloat(data_array.get(j).getValue()));
                            os.put(Integer.parseInt(data_array.get(j).getTrip_no()), Float.parseFloat(data_array.get(j).getValue()));
                            break;

                        default:
                            break;

                    }

                }

        }

        Log.i(TAG,"Size Score_trips" + score_trips_array.size());

        BarChart barChart_trips = findViewById(R.id.barChart_1);

        ArrayList<BarEntry> data_vals_trips = new ArrayList<>();

        final String[] trip_labels = new String[score_trips_array.size()];
        for(int i=0;i<score_trips_array.size();i++){

            Log.i("Plot Trips","Value" + hb.get(i) + " "  + ha.get(i) +  " " + dos.get(i) + " " +os.get(i));
            data_vals_trips.add(new BarEntry(i,new float[]{hb.get(i),ha.get(i),dos.get(i),os.get(i)}));

            trip_labels[i] = "Trip" + i;

        }

        int[] color_array = new int[]{Color.BLACK,Color.RED,Color.GREEN,Color.DKGRAY};

        BarDataSet dataSet = new BarDataSet(data_vals_trips, "Trip Data");
        dataSet.setColors(color_array);


        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);

        barData.setBarWidth(0.6f);

        barChart_trips.setData(barData);

        XAxis xAxis = barChart_trips.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setXOffset(0);
        xAxis.setYOffset(0);

        xAxis.setValueFormatter(new IAxisValueFormatter() {

            //private String[] qualities = new String[]{"1","2","3","4","5","6","7"};
            @Override

            public String getFormattedValue(float value, AxisBase axis) {
                return trip_labels[(int)value % trip_labels.length];
            }
        });

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setAxisLineWidth(3f);

        YAxis yl = barChart_trips.getAxisLeft();
        //yl.setAxisMinValue(0);
        yl.setAxisMinimum(0);

        Legend l = barChart_trips.getLegend();
        String[] label_array = new String[]{"Harsh Braking", "Harsh Acceleration","Dangerous Overspeeding","Overspeeding"};
        ;

        barchart_properties(barChart_trips);

    }



    private void barchart_properties(BarChart mbarChart){

        mbarChart.setBackgroundColor(Color.rgb(255,255,255));
        mbarChart.getDescription().setEnabled(false);
        //mbarChart.getLegend().setEnabled(false);
        mbarChart.getAxisRight().setEnabled(false);
        mbarChart.setDrawGridBackground(false);
        mbarChart.getAxisLeft().setTextSize(13f);

        mbarChart.getAxisLeft().setTextColor(Color.BLACK);
        mbarChart.getAxisLeft().setAxisLineColor(Color.BLACK);
        mbarChart.getAxisLeft().setAxisLineWidth(2f);

        mbarChart.getAxisRight().setDrawGridLines(false);
        mbarChart.getAxisLeft().setDrawGridLines(false);
        mbarChart.getXAxis().setDrawGridLines(false);
        mbarChart.setVisibleXRangeMaximum(30f);
        mbarChart.invalidate();

    }


}

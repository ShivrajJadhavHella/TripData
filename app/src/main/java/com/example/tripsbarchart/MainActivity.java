package com.example.tripsbarchart;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<DataClass> data_array =  new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String current_date = getCurrentTime();

        String value = substract_required_days(10);
        Log.i("Main Activity", current_date);
        Log.i("Main Activity", current_date.substring(0,6));
        Log.i("Main Activity", value);
        new GetData().execute();




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

    public String getCurrentTime() {
        String dateFormat = "ddMMyy HH:mm:ss";
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

    private class GetData extends AsyncTask<Void, Void, String> {

        private String request_url = "https://version2-0.azurewebsites.net/api/Data-Last7Days?code=SRBSZmAzPMRkrSWIuvPYmu/0pB8b8MwOMFSNTYajfyQQ/F00LNdboQ==&name=2019-05-29T18:30:00.000Z";
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

            Log.e("Main Activity", "Response from url: " + json_str);


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

                        String value1 = jsonArray.getJSONObject(i).getJSONObject("Value1").getString("_");

                        String value2 = jsonArray.getJSONObject(i).getJSONObject("Value3").getString("_");

                        String value3 = jsonArray.getJSONObject(i).getJSONObject("Value4").getString("_");

                        data_array.add(new DataClass(value1, value2, value3));
                        Log.i("Main Activity","Value1:" + value1 + "Value3:" + value2 + "Value3:" + value3);
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


            for(int j=0;j<data_array.size();j++){

                if(data_array.get(j).getDate().equals(past_Date)){

                    int index_no = -(i-7);

                    if(data_array.get(j).getEvent_name().equals("Score_Trip")) {

                        Log.i("Inside Score_Trip","Value:" + data_array.get(j).getValue());
                        dataVals.add(new BarEntry(index_no, Float.parseFloat(data_array.get(j).getValue())));
                    }

                }
            }
        }

        BarDataSet dataSet = new BarDataSet(dataVals, "This is a bar chart");
        dataSet.setColor(Color.rgb(0,0,255));
        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);

        barChart.setData(barData);

        barchart_properties(barChart);
    }



    private void barchart_properties(BarChart mbarChart){

        mbarChart.setBackgroundColor(Color.rgb(64,224,208));
        mbarChart.getDescription().setEnabled(false);
        mbarChart.getLegend().setEnabled(false);
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

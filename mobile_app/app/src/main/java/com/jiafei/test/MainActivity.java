package com.jiafei.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import info.hoang8f.android.segmented.SegmentedGroup;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
   // PostData postData = new PostData();
    private static final String DeviceID ="1074803160";
    private static final String ApiKey ="R=J4k2v8Im6z7mWDxx7ODep7tdQ=";
    private static final String key1 ="温度";
    private static final String key2 ="湿度";
    private static final String key3 ="光照";
    private static final String key4 ="可燃气体";
    private static final String key5 ="一氧化碳";

    private Handler handler = new Handler();
    BarChart barChart;
    ImageView imageView;
    PieChart pieChart;
    SegmentedGroup segmentedGroup;
    TextView data1,data2,data3,data4,data5;
    String value1,value2,value3,value4,value5;//,ledstate,fanstate,led_val,fan_val
    CardView card1,card2,card3,card4,card5;
    ImageView fanhui;
    ArrayList<BarEntry> barEntries = new ArrayList<>();
    ArrayList<PieEntry> pieEntries = new ArrayList<>();
    final String[] names = {"Temperature", "Humidity", "Light", "Ex", "Co"};
    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        card1=findViewById(R.id.wendu_view);
        card2=findViewById(R.id.shidu_view);
        card3=findViewById(R.id.light_view);
        card4=findViewById(R.id.keran_view);
        card5=findViewById(R.id.co_view);
        data1=findViewById(R.id.wendu_value);
        data2=findViewById(R.id.shidu_value);
        data3=findViewById(R.id.light_value);
        data4=findViewById(R.id.keran_value);
        data5=findViewById(R.id.co_value);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            requestPermissions(new String[]{"android.permission.INTERNET"},1);
        }
        barChart=findViewById(R.id.bar_chart);
        pieChart=findViewById(R.id.pie_chart);
        imageView=findViewById(R.id.imageView);
        segmentedGroup=findViewById(R.id.SegmentedGroup);

        //显示柱状图隐藏饼状图
        barChart.setVisibility(View.VISIBLE);
        pieChart.setVisibility(View.GONE);

        //调用创建柱状图的方法
        createBarChart();
        createPieChart();

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(),MenuActivity2.class));
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                }
            });
            segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if (i == R.id.button_bar) {
                        // 如果选中了 "Bar"，则显示 barChart并隐藏 pieChart
                        barChart.setVisibility(View.VISIBLE);
                        pieChart.setVisibility(View.GONE);
                    } else {
                        // 否则，显示 pie_view 并隐藏 bar_view
                        barChart.setVisibility(View.GONE);
                       pieChart.setVisibility(View.VISIBLE);
                    }
                }
            });
        handler.post(runnable);
        }
    //封装一个方法，用来创建柱状图
    private void createBarChart() {
        for (int i = 1; i < 6; i++) {
            float value = (float) (i * 0.0);
            BarEntry barEntry = new BarEntry(i, value);
            barEntries.add(barEntry);
            //Initialize bar data set
            //Set animation
            barChart.animateY(5000);
            //Set description text and color
            barChart.getDescription().setText("");
            barChart.getDescription().setTextColor(Color.BLACK);
            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return names[(int) (value - 1)];
                }
            });
            xAxis.setLabelCount(names.length, false);
            barChart.getLegend().setEnabled(false);
            xAxis.setTextSize(12f);
            barChart.getAxisLeft().setDrawZeroLine(true);
            //去掉左侧Y轴刻度
            barChart.getAxisLeft().setDrawLabels(false);
            //去掉左侧Y轴
            barChart.getAxisLeft().setDrawAxisLine(false);
            //去掉中间竖线
            barChart.getXAxis().setDrawGridLines(false);
            //去掉中间横线
            barChart.getAxisLeft().setDrawGridLines(false);
            //barChart.getAxisRight().setDrawGridLines(false);
        }
        BarDataSet barDataSet1 = new BarDataSet(barEntries, "data");
        //Set bar data
        barChart.setData(new BarData(barDataSet1));
        //Set colors
        barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
        //Hide draw value
        barDataSet1.setDrawValues(false);
        barChart.invalidate(); // 刷新图表
    }
    //封装一个方法，用来创建饼状图
    private void createPieChart(){
        //为每个名字创建一个 PieEntry，并添加到列表中
        for(int i=1;i<6;i++)
        {
            float value=(float) (i*10.0);
         //   PieEntry pieEntry = new PieEntry(value,names[i]);
         //   pieEntries.add(pieEntry);
        }
        //Initialize pie data set
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        //Set colors
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        //显示数据标签
        pieDataSet.setDrawValues(true);
        //Set pie date
        pieChart.setData(new PieData(pieDataSet));
        //Set animation
        pieChart.animateXY(5000, 5000);
        //隐藏描述文本
        pieChart.getDescription().setEnabled(false);
        //显示图例
        pieChart.getLegend().setEnabled(false);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(10f);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);
    }
    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        OkHttpClient client=new OkHttpClient();
                        Request request=new Request.Builder().url("http://api.heclouds.com/devices/"+DeviceID+"/datapoints?datastream_id="+key1).header("api-key",ApiKey).build();
                        Response response=client.newCall(request).execute();
                        String responseData =response.body().string();
                        parseJSONWithGSON(responseData);

                        JsonRootBean app =new Gson().fromJson(responseData,JsonRootBean.class);
                        List<Datastreams> streams=app.getData().getDatastreams();
                        List<Datapoints> points=streams.get(0).getDatapoints();
                        value1=points.get(0).getValue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        OkHttpClient client=new OkHttpClient();
                        Request request=new Request.Builder().url("http://api.heclouds.com/devices/"+DeviceID+"/datapoints?datastream_id="+key2).header("api-key",ApiKey).build();
                        Response response=client.newCall(request).execute();
                        String responseData =response.body().string();
                        parseJSONWithGSON(responseData);

                        JsonRootBean app =new Gson().fromJson(responseData,JsonRootBean.class);
                        List<Datastreams> streams=app.getData().getDatastreams();
                        List<Datapoints> points=streams.get(0).getDatapoints();
                        value2=points.get(0).getValue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        OkHttpClient client=new OkHttpClient();
                        Request request=new Request.Builder().url("http://api.heclouds.com/devices/"+DeviceID+"/datapoints?datastream_id="+key3).header("api-key",ApiKey).build();
                        Response response=client.newCall(request).execute();
                        String responseData =response.body().string();
                        parseJSONWithGSON(responseData);

                        JsonRootBean app =new Gson().fromJson(responseData,JsonRootBean.class);
                        List<Datastreams> streams=app.getData().getDatastreams();
                        List<Datapoints> points=streams.get(0).getDatapoints();
                        value3=points.get(0).getValue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        OkHttpClient client=new OkHttpClient();
                        Request request=new Request.Builder().url("http://api.heclouds.com/devices/"+DeviceID+"/datapoints?datastream_id="+key4).header("api-key",ApiKey).build();
                        Response response=client.newCall(request).execute();
                        String responseData =response.body().string();
                        parseJSONWithGSON(responseData);

                        JsonRootBean app =new Gson().fromJson(responseData,JsonRootBean.class);
                        List<Datastreams> streams=app.getData().getDatastreams();
                        List<Datapoints> points=streams.get(0).getDatapoints();
                        value4=points.get(0).getValue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        OkHttpClient client=new OkHttpClient();
                        Request request=new Request.Builder().url("http://api.heclouds.com/devices/"+DeviceID+"/datapoints?datastream_id="+key5).header("api-key",ApiKey).build();
                        Response response=client.newCall(request).execute();
                        String responseData =response.body().string();
                        parseJSONWithGSON(responseData);

                        JsonRootBean app =new Gson().fromJson(responseData,JsonRootBean.class);
                        List<Datastreams> streams=app.getData().getDatastreams();
                        List<Datapoints> points=streams.get(0).getDatapoints();
                        value5=points.get(0).getValue();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            data1.setText(String.format("%s",value1));
                            data2.setText(String.format("%s",value2));
                            data3.setText(String.format("%s",value3));
                            data4.setText(String.format("%s",value4));
                            data5.setText(String.format("%s",value5));
                            barEntries.clear();
                            barEntries.add(new BarEntry(1, Float.parseFloat(value1)));
                            barEntries.add(new BarEntry(2, Float.parseFloat(value2)));
                            barEntries.add(new BarEntry(3, Float.parseFloat(value3)));
                            barEntries.add(new BarEntry(4, Float.parseFloat(value4)));
                            barEntries.add(new BarEntry(5, Float.parseFloat(value5)));

                            pieEntries.clear();
                            pieEntries.add(new PieEntry(Float.parseFloat(value1),names[0]));
                            pieEntries.add(new PieEntry(Float.parseFloat(value2),names[1]));
                            pieEntries.add(new PieEntry(Float.parseFloat(value3),names[2]));
                            pieEntries.add(new PieEntry(Float.parseFloat(value4),names[3]));
                            pieEntries.add(new PieEntry(Float.parseFloat(value5),names[4]));

                                //柱状图更新数据
                                BarDataSet set = new BarDataSet(barEntries, names[names.length-1]);
                                set.setColors(ColorTemplate.MATERIAL_COLORS);
                                set.setDrawValues(true);
                                BarData data = new BarData(set);
                                barChart.setData(data);
                                barChart.invalidate(); //刷新柱状图
                            //饼状图更新数据
                            PieDataSet set1 =new PieDataSet(pieEntries,names[names.length-1]);
                            set1.setColors(ColorTemplate.COLORFUL_COLORS);
                            set1.setDrawValues(true);
                            PieData data1 = new PieData(set1);
                            pieChart.setData(data1);
                            pieChart.invalidate();//刷新饼状图
                        }
                    });
                }
            }).start();
            handler.postDelayed(this,1000);
        }
    };
    private void parseJSONWithGSON(String jsonData){
        JsonRootBean app =new Gson().fromJson(jsonData,JsonRootBean.class);
        List<Datastreams> streams =app.getData().getDatastreams();
        List<Datapoints> points =streams.get(0).getDatapoints();
        int count=app.getData().getCount();
        for (int i=0;i< points.size();i++){
            String time=points.get(i).getAt();
            String value =points.get(i).getValue();
            Log.w("www","time=" +time);
            Log.w("www","value=" +value);
        }
    }
}
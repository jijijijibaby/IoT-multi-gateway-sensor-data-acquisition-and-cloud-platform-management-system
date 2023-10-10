package com.jiafei.test;

import static com.jiafei.test.LogUtil.e;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataActivity extends AppCompatActivity {
    private static final String DeviceID ="1074803160";
    private static final String ApiKey ="R=J4k2v8Im6z7mWDxx7ODep7tdQ=";
    private static final String key1 ="温度";
    private static final String key2 ="湿度";
    private static final String key3 ="光照";
    private static final String key4 ="可燃气体";
    private static final String key5 ="一氧化碳";
    private Calendar startDateData=Calendar.getInstance();
    private NewDatePickerDialog datePickerDialog_start;
    private TextView startDate;
    private TabLayout tabLayout;
    private String time1,time2,time3,time4,time5;
    private  ListView listView1,listView2,listView3,listView4,listView5;
    private ViewPager viewpager;
    ImageView imageView;
    private final String[] mDisplayMonths = {"1", "2", "3","4", "5", "6","7", "8", "9","10", "11", "12"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        startDate= findViewById(R.id.start_date_S);
        listView1= findViewById(R.id.listView1);
        listView2= findViewById(R.id.listView2);
        listView3= findViewById(R.id.listView3);
        listView4= findViewById(R.id.listView4);
        listView5= findViewById(R.id.listView5);
        imageView=findViewById(R.id.imageView);
        viewpager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        listView1.setVisibility(View.VISIBLE);
        listView2.setVisibility(View.GONE);
        listView3.setVisibility(View.GONE);
        listView4.setVisibility(View.GONE);
        listView5.setVisibility(View.GONE);
        InitDate();
        Get();
        LayoutInflater layoutInflater = getLayoutInflater();

        View view1 = layoutInflater.inflate(R.layout.activity_zhexian1, null);
        View view2 = layoutInflater.inflate(R.layout.activity_zhexian2, null);
        View view3 = layoutInflater.inflate(R.layout.activity_zhexian3, null);
        View view4 = layoutInflater.inflate(R.layout.activity_zhexian4, null);
        View view5 = layoutInflater.inflate(R.layout.activity_zhexian5, null);
        List<View> viewList = new ArrayList<>();

        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        viewList.add(view4);
        viewList.add(view5);
        List<String> listTabTitle = new ArrayList<>();
        listTabTitle.add("Temp");
        listTabTitle.add("Humi");
        listTabTitle.add("Light");
        listTabTitle.add("Ex");
        listTabTitle.add("Co");
        MyAdapter myAdapter = new MyAdapter(viewList,listTabTitle,this);
        viewpager.setAdapter(myAdapter);
        tabLayout.setupWithViewPager(viewpager);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MenuActivity2.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        listView1.setVisibility(View.VISIBLE);
                        listView2.setVisibility(View.GONE);
                        listView3.setVisibility(View.GONE);
                        listView4.setVisibility(View.GONE);
                        listView5.setVisibility(View.GONE);

                        break;
                    case 1:
                        listView1.setVisibility(View.GONE);
                        listView2.setVisibility(View.VISIBLE);
                        listView3.setVisibility(View.GONE);
                        listView4.setVisibility(View.GONE);
                        listView5.setVisibility(View.GONE);

                        break;
                    case 2:
                        listView1.setVisibility(View.GONE);
                        listView2.setVisibility(View.GONE);
                        listView3.setVisibility(View.VISIBLE);
                        listView4.setVisibility(View.GONE);
                        listView5.setVisibility(View.GONE);

                        break;
                    case 3:
                        listView1.setVisibility(View.GONE);
                        listView2.setVisibility(View.GONE);
                        listView3.setVisibility(View.GONE);
                        listView4.setVisibility(View.VISIBLE);
                        listView5.setVisibility(View.GONE);

                        break;
                    case 4:
                        listView1.setVisibility(View.GONE);
                        listView2.setVisibility(View.GONE);
                        listView3.setVisibility(View.GONE);
                        listView4.setVisibility(View.GONE);
                        listView5.setVisibility(View.VISIBLE);

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        startDate.setOnClickListener(this::onClick);

    }
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_date_S:
                getStartDate();
                break;
        }
    }
    public void Get(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("http://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key1+"&start="+time1+"&limit=100").header("api-key", ApiKey).build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);
                    JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
                    List<Datastreams> streams = app.getData().getDatastreams();
                    List<Datapoints> points = streams.get(0).getDatapoints();
                    String[]ss=new String[points.size()];
                    for (int i=0;i< points.size();i++) {
                        String time=points.get(i).getAt();
                        String value =points.get(i).getValue();
                        ss[i]=time+"  Temp  "+value;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    DataActivity.this, android.R.layout.simple_list_item_1,ss);
                            listView1.setAdapter(adapter);
                        }
                    });
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("http://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key2+"&start="+time2+"&limit=100").header("api-key", ApiKey).build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);
                    JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
                    List<Datastreams> streams = app.getData().getDatastreams();
                    List<Datapoints> points = streams.get(0).getDatapoints();
                    String[]ss=new String[points.size()];
                    for (int i=0;i< points.size();i++) {
                        String time=points.get(i).getAt();
                        String value =points.get(i).getValue();
                        ss[i]=time+"  Humi  "+value;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    DataActivity.this, android.R.layout.simple_list_item_1,ss);
                            listView2.setAdapter(adapter);
                        }
                    });
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("http://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key3+"&start="+time3+"&limit=100").header("api-key", ApiKey).build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);
                    JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
                    List<Datastreams> streams = app.getData().getDatastreams();
                    List<Datapoints> points = streams.get(0).getDatapoints();
                    String[]ss=new String[points.size()];
                    for (int i=0;i< points.size();i++) {
                        String time=points.get(i).getAt();
                        String value =points.get(i).getValue();
                        ss[i]=time+"  LIGHT  "+value;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    DataActivity.this, android.R.layout.simple_list_item_1,ss);
                            listView3.setAdapter(adapter);
                        }
                    });
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("http://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key4+"&start="+time4+"&limit=100").header("api-key", ApiKey).build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);
                    JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
                    List<Datastreams> streams = app.getData().getDatastreams();
                    List<Datapoints> points = streams.get(0).getDatapoints();
                    String[]ss=new String[points.size()];
                    for (int i=0;i< points.size();i++) {
                        String time=points.get(i).getAt();
                        String value =points.get(i).getValue();
                        ss[i]=time+"  EX  "+value;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    DataActivity.this, android.R.layout.simple_list_item_1,ss);
                            listView4.setAdapter(adapter);
                        }
                    });
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
//
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url("http://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key5+"&start="+time5+"&limit=100").header("api-key", ApiKey).build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);
                    JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
                    List<Datastreams> streams = app.getData().getDatastreams();
                    List<Datapoints> points = streams.get(0).getDatapoints();
                    String[]ss=new String[points.size()];
                    for (int i=0;i< points.size();i++) {
                        String time=points.get(i).getAt();
                        String value =points.get(i).getValue();
                        ss[i]=time+"  CO  "+value;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    DataActivity.this, android.R.layout.simple_list_item_1,ss);
                            listView5.setAdapter(adapter);
                        }
                    });
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void InitDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        startDate.setText(date);
        Values.startDateInfor=date+"T"+ systemTime.getNowTime();
        e("时间初始化","现在的时间是："+date);
        time1 = date+"T"+"00:00:00";
        time2 = date+"T"+"00:00:00";
        time3 = date+"T"+"00:00:00";
        time4 = date+"T"+"00:00:00";
        time5 = date+"T"+"00:00:00";
    }
    private void getStartDate() {
        Calendar calendar=Calendar.getInstance();
        datePickerDialog_start=new NewDatePickerDialog(DataActivity.this,
                AlertDialog.THEME_HOLO_LIGHT,
                (datePicker, i, i1, i2) -> {
                    if ((i2+1)<10){
                        String desc1=String.format("%d-0%d-0%dT00:00:00",i,i1+1,i2);
                        String desc2=String.format("%d-0%d-0%dT00:00:00",i,i1+1,i2);
                        String desc3=String.format("%d-0%d-0%dT00:00:00",i,i1+1,i2);
                        String desc4=String.format("%d-0%d-0%dT00:00:00",i,i1+1,i2);
                        String desc5=String.format("%d-0%d-0%dT00:00:00",i,i1+1,i2);
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder().url("http://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key1+"&start="+desc1+"&limit=100").header("api-key", ApiKey).build();
                                    Response response = client.newCall(request).execute();
                                    String responseData = response.body().string();
                                    parseJSONWithGSON(responseData);
                                    JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
                                    List<Datastreams> streams = app.getData().getDatastreams();
                                    List<Datapoints> points = streams.get(0).getDatapoints();
                                    String[]bb=new String[points.size()];
                                    for (int i=0;i< points.size();i++) {
                                        String time=points.get(i).getAt();
                                        String value =points.get(i).getValue();
                                        bb[i]=time+"  Temp  "+value;
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                    DataActivity.this, android.R.layout.simple_list_item_1,bb);
                                            listView1.setAdapter(adapter);
                                        }
                                    });
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }
//
                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder().url("http://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key2+"&start="+desc2+"&limit=100").header("api-key", ApiKey).build();
                                    Response response = client.newCall(request).execute();
                                    String responseData = response.body().string();
                                    parseJSONWithGSON(responseData);
                                    JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
                                    List<Datastreams> streams = app.getData().getDatastreams();
                                    List<Datapoints> points = streams.get(0).getDatapoints();
                                    String[]bb=new String[points.size()];
                                    for (int i=0;i< points.size();i++) {
                                        String time=points.get(i).getAt();
                                        String value =points.get(i).getValue();
                                        bb[i]=time+"  Humi  "+value;
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                    DataActivity.this, android.R.layout.simple_list_item_1,bb);
                                            listView2.setAdapter(adapter);
                                        }
                                    });
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder().url("http://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key3+"&start="+desc3+"&limit=100").header("api-key", ApiKey).build();
                                    Response response = client.newCall(request).execute();
                                    String responseData = response.body().string();
                                    parseJSONWithGSON(responseData);
                                    JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
                                    List<Datastreams> streams = app.getData().getDatastreams();
                                    List<Datapoints> points = streams.get(0).getDatapoints();
                                    String[]bb=new String[points.size()];
                                    for (int i=0;i< points.size();i++) {
                                        String time=points.get(i).getAt();
                                        String value =points.get(i).getValue();
                                        bb[i]=time+"  LIGHT  "+value;
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                    DataActivity.this, android.R.layout.simple_list_item_1,bb);
                                            listView3.setAdapter(adapter);
                                        }
                                    });
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder().url("http://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key4+"&start="+desc4+"&limit=100").header("api-key", ApiKey).build();
                                    Response response = client.newCall(request).execute();
                                    String responseData = response.body().string();
                                    parseJSONWithGSON(responseData);
                                    JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
                                    List<Datastreams> streams = app.getData().getDatastreams();
                                    List<Datapoints> points = streams.get(0).getDatapoints();
                                    String[]bb=new String[points.size()];
                                    for (int i=0;i< points.size();i++) {
                                        String time=points.get(i).getAt();
                                        String value =points.get(i).getValue();
                                        bb[i]=time+"  EX  "+value;
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                    DataActivity.this, android.R.layout.simple_list_item_1,bb);
                                            listView4.setAdapter(adapter);
                                        }
                                    });
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder().url("http://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key5+"&start="+desc5+"&limit=100").header("api-key", ApiKey).build();
                                    Response response = client.newCall(request).execute();
                                    String responseData = response.body().string();
                                    parseJSONWithGSON(responseData);
                                    JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
                                    List<Datastreams> streams = app.getData().getDatastreams();
                                    List<Datapoints> points = streams.get(0).getDatapoints();
                                    String[]bb=new String[points.size()];
                                    for (int i=0;i< points.size();i++) {
                                        String time=points.get(i).getAt();
                                        String value =points.get(i).getValue();
                                        bb[i]=time+"  CO  "+value;
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                    DataActivity.this, android.R.layout.simple_list_item_1,bb);
                                            listView5.setAdapter(adapter);
                                        }
                                    });
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }else {
                        String desc1=String.format("%d-0%d-%dT00:00:00",i,i1+1,i2);
                        String desc2=String.format("%d-0%d-0%dT00:00:00",i,i1+1,i2);
                        String desc3=String.format("%d-0%d-0%dT00:00:00",i,i1+1,i2);
                        String desc4=String.format("%d-0%d-0%dT00:00:00",i,i1+1,i2);
                        String desc5=String.format("%d-0%d-0%dT00:00:00",i,i1+1,i2);
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder().url("http://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key1+"&start="+desc1+"&limit=100").header("api-key", ApiKey).build();
                                    Response response = client.newCall(request).execute();
                                    String responseData = response.body().string();
                                    parseJSONWithGSON(responseData);
                                    JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
                                    List<Datastreams> streams = app.getData().getDatastreams();
                                    List<Datapoints> points = streams.get(0).getDatapoints();
                                    String[]bb=new String[points.size()];
                                    for (int i=0;i< points.size();i++) {
                                        String time=points.get(i).getAt();
                                        String value =points.get(i).getValue();
                                        bb[i]=time+"  Temp  "+value;
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                    DataActivity.this, android.R.layout.simple_list_item_1,bb);
                                            listView1.setAdapter(adapter);
                                        }
                                    });
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder().url("http://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key2+"&start="+desc2+"&limit=100").header("api-key", ApiKey).build();
                                    Response response = client.newCall(request).execute();
                                    String responseData = response.body().string();
                                    parseJSONWithGSON(responseData);
                                    JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
                                    List<Datastreams> streams = app.getData().getDatastreams();
                                    List<Datapoints> points = streams.get(0).getDatapoints();
                                    String[]bb=new String[points.size()];
                                    for (int i=0;i< points.size();i++) {
                                        String time=points.get(i).getAt();
                                        String value =points.get(i).getValue();
                                        bb[i]=time+"  Humi  "+value;
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                    DataActivity.this, android.R.layout.simple_list_item_1,bb);
                                            listView2.setAdapter(adapter);
                                        }
                                    });
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder().url("http://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key3+"&start="+desc3+"&limit=100").header("api-key", ApiKey).build();
                                    Response response = client.newCall(request).execute();
                                    String responseData = response.body().string();
                                    parseJSONWithGSON(responseData);
                                    JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
                                    List<Datastreams> streams = app.getData().getDatastreams();
                                    List<Datapoints> points = streams.get(0).getDatapoints();
                                    String[]bb=new String[points.size()];
                                    for (int i=0;i< points.size();i++) {
                                        String time=points.get(i).getAt();
                                        String value =points.get(i).getValue();
                                        bb[i]=time+"  Light  "+value;
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                    DataActivity.this, android.R.layout.simple_list_item_1,bb);
                                            listView3.setAdapter(adapter);
                                        }
                                    });
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder().url("http://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key4+"&start="+desc4+"&limit=100").header("api-key", ApiKey).build();
                                    Response response = client.newCall(request).execute();
                                    String responseData = response.body().string();
                                    parseJSONWithGSON(responseData);
                                    JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
                                    List<Datastreams> streams = app.getData().getDatastreams();
                                    List<Datapoints> points = streams.get(0).getDatapoints();
                                    String[]bb=new String[points.size()];
                                    for (int i=0;i< points.size();i++) {
                                        String time=points.get(i).getAt();
                                        String value =points.get(i).getValue();
                                        bb[i]=time+"  EX  "+value;
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                    DataActivity.this, android.R.layout.simple_list_item_1,bb);
                                            listView4.setAdapter(adapter);
                                        }
                                    });
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder().url("http://api.heclouds.com/devices/" + DeviceID + "/datapoints?datastream_id=" + key5+"&start="+desc5+"&limit=100").header("api-key", ApiKey).build();
                                    Response response = client.newCall(request).execute();
                                    String responseData = response.body().string();
                                    parseJSONWithGSON(responseData);
                                    JsonRootBean app = new Gson().fromJson(responseData, JsonRootBean.class);
                                    List<Datastreams> streams = app.getData().getDatastreams();
                                    List<Datapoints> points = streams.get(0).getDatapoints();
                                    String[]bb=new String[points.size()];
                                    for (int i=0;i< points.size();i++) {
                                        String time=points.get(i).getAt();
                                        String value =points.get(i).getValue();
                                        bb[i]=time+"  CO  "+value;
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                                    DataActivity.this, android.R.layout.simple_list_item_1,bb);
                                            listView5.setAdapter(adapter);
                                        }
                                    });
                                }
                                catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                    }
                    String desc=String.format("%d年%d月%d日",i,i1+1,i2);
                    startDate.setText(desc);
                    startDateData.set(i,i1,i2);
                    if ((i1+1)<10){
                        Values.startDateInfor=String.format("%d-0%d-0%dT00:00:00",i,i1+1,i2);
                    }else {
                        Values.startDateInfor=String.format("%d-0%d-%dT00:00:00",i,i1+1,i2);
                    }
                    e("日期转变","开始日期为"+ Values.startDateInfor);
                    time1 = Values.startDateInfor;
                    time2 = Values.startDateInfor;
                    time3 = Values.startDateInfor;
                    time4 = Values.startDateInfor;
                    time5 = Values.startDateInfor;
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog_start.setButton(DialogInterface.BUTTON_POSITIVE,"Confirm",datePickerDialog_start);
        datePickerDialog_start.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel",datePickerDialog_start);
        datePickerDialog_start.show();
        DatePicker dp = findDatePicker( (ViewGroup) datePickerDialog_start.getWindow().getDecorView());
        if (dp != null) {
            ((NumberPicker) ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(1)).setDisplayedValues(mDisplayMonths);
        }

    }
    private DatePicker findDatePicker(ViewGroup group) {
        if  (group  !=   null ) {
            for  ( int  i  =   0 , j  =  group.getChildCount(); i  <  j; i ++ ) {
                View child  =  group.getChildAt(i);
                if  (child  instanceof DatePicker) {
                    return  (DatePicker) child;
                }  else   if  (child  instanceof ViewGroup) {
                    DatePicker result  =  findDatePicker((ViewGroup) child);
                    if  (result  !=   null )
                        return  result;
                }
            }
        }
        return   null ;
    }
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
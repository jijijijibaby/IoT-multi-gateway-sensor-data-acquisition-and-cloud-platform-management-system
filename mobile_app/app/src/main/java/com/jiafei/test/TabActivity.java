package com.jiafei.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.abel533.echarts.style.ControlStyle;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TabActivity extends AppCompatActivity {
    private String url = "http://api.heclouds.com/devices/1074803160/datastreams";

    ArrayList<Float> StoreValue1 = new ArrayList<>();
    ArrayList<Float> StoreValue2 = new ArrayList<>();
    ArrayList<Float> StoreValue3 = new ArrayList<>();
    ArrayList<Float> StoreValue4 = new ArrayList<>();
    ArrayList<Float> StoreValue5 = new ArrayList<>();
    private TabLayout tabLayout;
    private ViewPager viewpager;
    LineChart lineChart1,lineChart2,lineChart3,lineChart4,lineChart5;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        viewpager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        imageView=findViewById(R.id.imageView);

        lineChart1=findViewById(R.id.wenduchart);
        lineChart2=findViewById(R.id.shiduchart);
        lineChart3=findViewById(R.id.lighthart);
        lineChart4=findViewById(R.id.exchart);
        lineChart5=findViewById(R.id.cochart);

        lineChart1.setVisibility(View.VISIBLE);
        lineChart2.setVisibility(View.GONE);
        lineChart3.setVisibility(View.GONE);
        lineChart4.setVisibility(View.GONE);
        lineChart5.setVisibility(View.GONE);
        init();//运行初始化函数
        feedMultiple();//线程

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
                      lineChart1.setVisibility(View.VISIBLE);
                      lineChart2.setVisibility(View.GONE);
                      lineChart3.setVisibility(View.GONE);
                      lineChart4.setVisibility(View.GONE);
                      lineChart5.setVisibility(View.GONE);
                      break;
                  case 1:
                      lineChart1.setVisibility(View.GONE);
                      lineChart2.setVisibility(View.VISIBLE);
                      lineChart3.setVisibility(View.GONE);
                      lineChart4.setVisibility(View.GONE);
                      lineChart5.setVisibility(View.GONE);
                      break;
                  case 2:
                      lineChart1.setVisibility(View.GONE);
                      lineChart2.setVisibility(View.GONE);
                      lineChart3.setVisibility(View.VISIBLE);
                      lineChart4.setVisibility(View.GONE);
                      lineChart5.setVisibility(View.GONE);
                      break;
                  case 3:
                      lineChart1.setVisibility(View.GONE);
                      lineChart2.setVisibility(View.GONE);
                      lineChart3.setVisibility(View.GONE);
                      lineChart4.setVisibility(View.VISIBLE);
                      lineChart5.setVisibility(View.GONE);
                      break;
                  case 4:
                      lineChart1.setVisibility(View.GONE);
                      lineChart2.setVisibility(View.GONE);
                      lineChart3.setVisibility(View.GONE);
                      lineChart4.setVisibility(View.GONE);
                      lineChart5.setVisibility(View.VISIBLE);
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

    }
    /**
     * 初始化函数
     *
     * @author home
     * @time 2021/3/1 9:38
     */
    private void init() {
        for (int i = 0; i < 5; i++) {//初始化arrylist赋值 先增加值，再修改值
            StoreValue1.add(i, (float) i);
            StoreValue2.add(i, (float) i);
            StoreValue3.add(i, (float) i);
            StoreValue4.add(i, (float) i);
            StoreValue5.add(i, (float) i);
        }

        //设置数据部分（一定要为不同的传感器设置不同的LineData）
        LineData dataWendu = new LineData();
        dataWendu.setValueTextColor(Color.BLACK);
        lineChart1.setData(dataWendu);

        LineData dataShidu =new LineData();
        dataShidu.setValueTextColor(Color.BLACK);
        lineChart2.setData(dataShidu);

        LineData dataLight =new LineData();
        dataLight.setValueTextColor(Color.BLACK);
        lineChart3.setData(dataLight);

        LineData dataEx=new LineData();
        dataEx.setValueTextColor(Color.BLACK);
        lineChart4.setData(dataEx);

        LineData dataCo =new LineData();
        dataCo.setValueTextColor(Color.BLACK);
        lineChart5.setData(dataCo);

        getJsonFromOneNet();
        lineChartSetting();//设置表格样式
        lineChart1.invalidate();
        lineChart2.invalidate();
        lineChart3.invalidate();
        lineChart4.invalidate();
        lineChart5.invalidate();
    }
    /**
     * 设置表格的各种属性
     *
     * @author home
     * @time 2021/2/28 13:01
     */
    private void lineChartSetting() {
        Legend legendWenDu = lineChart1.getLegend();
        legendWenDu.setEnabled(true);
        legendWenDu.setTextColor(Color.BLACK);
        legendWenDu.setTextSize(12);
        legendWenDu.setForm(Legend.LegendForm.LINE);
        legendWenDu.setFormSize(18);
        legendWenDu.setXEntrySpace(15);
        legendWenDu.setFormToTextSpace(8);
        LegendEntry[] legendEntriesWenDu = new LegendEntry[1];
        LegendEntry entryWenDu = new LegendEntry();
        entryWenDu.formColor = Color.BLUE;//不设置颜色将不会出现
        legendEntriesWenDu[0] = entryWenDu;
        legendWenDu.setEnabled(false);
        legendWenDu.setCustom(legendEntriesWenDu);

        Legend legendShidu = lineChart2.getLegend();
        legendShidu.setEnabled(true);
        legendShidu.setTextColor(Color.BLACK);
        legendShidu.setTextSize(12);
        legendShidu.setForm(Legend.LegendForm.LINE);
        legendShidu.setFormSize(18);
        legendShidu.setXEntrySpace(15);
        legendShidu.setFormToTextSpace(8);
        LegendEntry[] legendEntriesShidu = new LegendEntry[1];
        LegendEntry entryShidu = new LegendEntry();
        entryShidu.formColor = Color.BLUE;//不设置颜色将不会出现
        legendEntriesShidu[0] = entryShidu;
        legendShidu.setEnabled(false);
        legendShidu.setCustom(legendEntriesShidu);

        Legend legendLight = lineChart3.getLegend();
        legendLight.setEnabled(true);
        legendLight.setTextColor(Color.BLACK);
        legendLight.setTextSize(12);
        legendLight.setForm(Legend.LegendForm.LINE);
        legendLight.setFormSize(18);
        legendLight.setXEntrySpace(15);
        legendLight.setFormToTextSpace(8);
        LegendEntry[] legendEntriesLight = new LegendEntry[1];
        LegendEntry entryLight = new LegendEntry();
        entryLight.formColor = Color.BLUE;//不设置颜色将不会出现
        legendEntriesLight[0] = entryLight;
        legendLight.setEnabled(false);
        legendLight.setCustom(legendEntriesLight);

        Legend legendEx = lineChart4.getLegend();
        legendEx.setEnabled(true);
        legendEx.setTextColor(Color.BLACK);
        legendEx.setTextSize(12);
        legendEx.setForm(Legend.LegendForm.LINE);
        legendEx.setFormSize(18);
        legendEx.setXEntrySpace(15);
        legendEx.setFormToTextSpace(8);
        LegendEntry[] legendEntriesEx = new LegendEntry[1];
        LegendEntry entryEx = new LegendEntry();
        entryEx.formColor = Color.BLUE;//不设置颜色将不会出现
        legendEntriesEx[0] = entryEx;
        legendEx.setEnabled(false);
        legendEx.setCustom(legendEntriesEx);

        Legend legendCo = lineChart5.getLegend();
        legendCo.setEnabled(true);
        legendCo.setTextColor(Color.BLACK);
        legendCo.setTextSize(12);
        legendCo.setForm(Legend.LegendForm.LINE);
        legendCo.setFormSize(18);
        legendCo.setXEntrySpace(15);
        legendCo.setFormToTextSpace(8);
        LegendEntry[] legendEntriesCo = new LegendEntry[1];
        LegendEntry entryCo = new LegendEntry();
        entryCo.formColor = Color.BLUE;//不设置颜色将不会出现
        legendEntriesCo[0] = entryCo;
        legendCo.setEnabled(false);
        legendCo.setCustom(legendEntriesCo);
    }

    /**
     * 添加不同传感器的数据
     *
     * @author home
     * @time 2021/2/28 16:56
     */
    private void addEntry() {

        LineData dataWenDu = lineChart1.getData();
        LineData dataShidu = lineChart2.getData();
        LineData dataLight = lineChart3.getData();
        LineData dataEx = lineChart4.getData();
        LineData dataCo = lineChart5.getData();

        //温度
        if (dataWenDu != null) {

            ILineDataSet setWenDu = dataWenDu.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (setWenDu == null) {
                setWenDu = createSet();
                dataWenDu.addDataSet(setWenDu);
            }

//            StoreValue1.set(0,(float) 0);//测试用 看下数据是否能显示
            dataWenDu.addEntry(new Entry(setWenDu.getEntryCount(), StoreValue1.get(0)), 0);
            dataWenDu.notifyDataChanged();

            // let the chart know it's data has changed
            lineChart1.notifyDataSetChanged();
            // limit the number of visible entries
            lineChart1.setVisibleXRangeMaximum(120);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            lineChart1.moveViewToX(dataWenDu.getEntryCount());
            //   X轴所在位置   默认为上面
            lineChart1.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            //隐藏右边的Y轴
            lineChart1.getAxisRight().setEnabled(false);
            lineChart1.getDescription().setText("");
            lineChart1.getXAxis().setDrawGridLines(false);  //是否绘制X轴上的网格线（背景里面的竖线）
            lineChart1.getAxisLeft().setDrawGridLines(false);  //是否绘制Y轴上的网格线（背景里面的横线）
            lineChart1.getXAxis().setAxisLineColor(Color.BLUE);
            lineChart1.getXAxis().setAxisLineWidth(4);
            lineChart1.getAxisLeft().setAxisLineWidth(4);
            lineChart1.getAxisLeft().setAxisLineColor(Color.BLUE);
        }

        //湿度
        if (dataShidu != null) {

            ILineDataSet setShidu = dataShidu.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (setShidu == null) {
                setShidu = createSet();
                dataShidu.addDataSet(setShidu);
            }

//            StoreValue1.set(0,(float) 0);//测试用 看下数据是否能显示
            dataShidu.addEntry(new Entry(setShidu.getEntryCount(), StoreValue2.get(0)), 0);
            dataShidu.notifyDataChanged();

            // let the chart know it's data has changed
            lineChart2.notifyDataSetChanged();
            // limit the number of visible entries
            lineChart2.setVisibleXRangeMaximum(120);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            lineChart2.moveViewToX(dataShidu.getEntryCount());
            //   X轴所在位置   默认为上面
            lineChart2.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            //隐藏右边的Y轴
            lineChart2.getAxisRight().setEnabled(false);
            lineChart2.getDescription().setText("");
            lineChart2.getXAxis().setDrawGridLines(false);  //是否绘制X轴上的网格线（背景里面的竖线）
            lineChart2.getAxisLeft().setDrawGridLines(false);  //是否绘制Y轴上的网格线（背景里面的横线）
            lineChart2.getXAxis().setAxisLineColor(Color.BLACK);
            lineChart2.getXAxis().setAxisLineWidth(4);
            lineChart2.getAxisLeft().setAxisLineWidth(4);
            lineChart2.getAxisLeft().setAxisLineColor(Color.BLACK);
        }
        //光照
        if (dataLight != null) {

            ILineDataSet setLight = dataLight.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (setLight == null) {
                setLight = createSet();
                dataLight.addDataSet(setLight);
            }

//            StoreValue1.set(0,(float) 0);//测试用 看下数据是否能显示
            dataLight.addEntry(new Entry(setLight.getEntryCount(), StoreValue3.get(0)), 0);
            dataLight.notifyDataChanged();

            // let the chart know it's data has changed
            lineChart3.notifyDataSetChanged();
            // limit the number of visible entries
            lineChart3.setVisibleXRangeMaximum(120);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            lineChart3.moveViewToX(dataShidu.getEntryCount());
            //   X轴所在位置   默认为上面
            lineChart3.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            //隐藏右边的Y轴
            lineChart3.getAxisRight().setEnabled(false);
            lineChart3.getDescription().setText("");
            lineChart3.getXAxis().setDrawGridLines(false);  //是否绘制X轴上的网格线（背景里面的竖线）
            lineChart3.getAxisLeft().setDrawGridLines(false);  //是否绘制Y轴上的网格线（背景里面的横线）
            lineChart3.getXAxis().setAxisLineColor(Color.MAGENTA);
            lineChart3.getXAxis().setAxisLineWidth(4);
            lineChart3.getAxisLeft().setAxisLineWidth(4);
            lineChart3.getAxisLeft().setAxisLineColor(Color.MAGENTA);
        }
        //可燃气体
        if (dataEx != null) {

            ILineDataSet setEx = dataEx.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (setEx == null) {
                setEx = createSet();
                dataEx.addDataSet(setEx);
            }

//            StoreValue1.set(0,(float) 0);//测试用 看下数据是否能显示
            dataEx.addEntry(new Entry(setEx.getEntryCount(), StoreValue4.get(0)), 0);
            dataEx.notifyDataChanged();

            // let the chart know it's data has changed
            lineChart4.notifyDataSetChanged();
            // limit the number of visible entries
            lineChart4.setVisibleXRangeMaximum(120);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            lineChart4.moveViewToX(dataShidu.getEntryCount());
            //   X轴所在位置   默认为上面
            lineChart4.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            //隐藏右边的Y轴
            lineChart4.getAxisRight().setEnabled(false);
            lineChart4.getDescription().setText("");
            lineChart4.getXAxis().setDrawGridLines(false);  //是否绘制X轴上的网格线（背景里面的竖线）
            lineChart4.getAxisLeft().setDrawGridLines(false);  //是否绘制Y轴上的网格线（背景里面的横线）
            lineChart4.getXAxis().setAxisLineColor(Color.CYAN);
            lineChart4.getXAxis().setAxisLineWidth(4);
            lineChart4.getAxisLeft().setAxisLineWidth(4);
            lineChart4.getAxisLeft().setAxisLineColor(Color.CYAN);
        }
        //一氧化碳
        if (dataCo != null) {

            ILineDataSet setCo = dataCo.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (setCo == null) {
                setCo = createSet();
                dataCo.addDataSet(setCo);
            }

//            StoreValue1.set(0,(float) 0);//测试用 看下数据是否能显示
            dataCo.addEntry(new Entry(setCo.getEntryCount(), StoreValue5.get(0)), 0);
            dataCo.notifyDataChanged();

            // let the chart know it's data has changed
            lineChart5.notifyDataSetChanged();
            // limit the number of visible entries
            lineChart5.setVisibleXRangeMaximum(120);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            lineChart5.moveViewToX(dataShidu.getEntryCount());
            //   X轴所在位置   默认为上面
            lineChart5.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            //隐藏右边的Y轴
            lineChart5.getAxisRight().setEnabled(false);
            lineChart5.getDescription().setText("");
            lineChart5.getXAxis().setDrawGridLines(false);  //是否绘制X轴上的网格线（背景里面的竖线）
            lineChart5.getAxisLeft().setDrawGridLines(false);  //是否绘制Y轴上的网格线（背景里面的横线）
            lineChart5.getXAxis().setAxisLineColor(Color.LTGRAY);
            lineChart5.getXAxis().setAxisLineWidth(4);
            lineChart5.getAxisLeft().setAxisLineWidth(4);
            lineChart5.getAxisLeft().setAxisLineColor(Color.LTGRAY);
        }
    }
//折线设置
    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.RED);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        set.setColor(Color.GREEN);  //折线的颜色
        set.setLineWidth(4);        //折线的粗细
        //是否画折线点上的空心圆  false表示直接画成实心圆
        set.setDrawCircleHole(false);

        return set;
    }

    private Thread thread;

    private void feedMultiple() {

        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                getJsonFromOneNet();
                addEntry();//更新数据
            }
        };

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {

                    // Don't generate garbage runnables inside the loop.
                    runOnUiThread(runnable);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkHttpUtils.getInstance().cancelTag(this);
    }
    /**
     * OkHttp3与FastJson结合，从OneNET云平台获取数据，并存入arraylist中
     *
     * @author home
     * @time 2021/2/28 14:44
     */
    private void getJsonFromOneNet() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("api-key", "R=J4k2v8Im6z7mWDxx7ODep7tdQ=")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 请求失败
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String value="",id="";
                // 请求成功
                JSONObject jsonObject = JSONObject.parseObject(response.body().string());
                JSONArray jsonArrayDatastreams = jsonObject.getJSONArray("data");//获取json数组即data
                for (int i = 0; i < jsonArrayDatastreams.size(); i++) {
                    JSONObject dataItem = jsonArrayDatastreams.getJSONObject(i);
                    id = dataItem.getString("id");
                    String currentValue = dataItem.getString("current_value");
                    switch (id) {
                        default:
                            break;
                        case "温度":
                            float a1 = Float.parseFloat(String.valueOf(currentValue));
                            StoreValue1.set(0, a1);
                            break;
                        case "湿度":
                            float a2 = Float.parseFloat(String.valueOf(currentValue));
                            StoreValue2.set(0, a2);
                            break;
                        case "光照":
                            float a3 = Float.parseFloat(String.valueOf(currentValue));
                            StoreValue3.set(0, a3);
                            break;
                        case "可燃气体":
                            float a4 = Float.parseFloat(String.valueOf(currentValue));
                            StoreValue4.set(0, a4);
                            break;
                        case "一氧化碳":
                            float a5 = Float.parseFloat(String.valueOf(currentValue));
                            StoreValue5.set(0, a5);
                            break;
                    }

                }
            }
        });
    }
}

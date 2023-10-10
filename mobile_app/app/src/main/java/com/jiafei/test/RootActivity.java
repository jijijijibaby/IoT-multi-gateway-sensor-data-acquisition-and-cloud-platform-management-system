package com.jiafei.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RootActivity extends AppCompatActivity {
    PostData postData = new PostData();
    ImageView imageView;
    Button button;
    EditText editText;
    String neirong;
    ListView listView1;

    class Item {
        String cmdUuid;
        String body;
        String confirmTime;

        Item(String cmdUuid, String body, String confirmTime) {
            this.cmdUuid = cmdUuid;
            this.body = body;
            this.confirmTime = confirmTime;
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.command);
        button = findViewById(R.id.Button1);
        listView1 = findViewById(R.id.listView1);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MenuActivity2.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                neirong = editText.getText().toString();
                postData.PostDataMethod(neirong);
                Toast.makeText(RootActivity.this, "正在下发命令", Toast.LENGTH_SHORT).show();
            }
        });
        handler.post(runnable);

    }

    private void getJsonFromOneNet() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://api.heclouds.com/cmds/history/1074803160")
                .addHeader("api-key", "R=J4k2v8Im6z7mWDxx7ODep7tdQ=")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 请求失败
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jsonObject = JSONObject.parseObject(response.body().string());
                JSONObject data = jsonObject.getJSONObject("data");
                JSONArray items = data.getJSONArray("items");
                List<Item> itemList = new ArrayList<>();
                for (int i = 0; i < items.size(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    String cmdUuid = item.getString("cmd_uuid");
                    String body = item.getString("body");
                    String confirmTime = item.getString("confirm_time");
                    itemList.add(new Item(cmdUuid, body, confirmTime));
                }
                String[] ss = new String[itemList.size()];
                for (int i = 0; i < itemList.size(); i++) {
                    Item item = itemList.get(i);
                    // 提取body字符串中所有偶数位置上的字符
                    StringBuilder sb = new StringBuilder();
                    for (int j = 1; j < item.body.length(); j += 2) {
                        sb.append(item.body.charAt(j));
                    }
                    String processedBody = sb.toString();

                    ss[i] ="confirm_time:" + item.confirmTime+"\n"+"body:" + processedBody+"\n"+"cmd_uuid:"+item.cmdUuid+"\n";
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> adapter =new ArrayAdapter<>(
                                RootActivity.this, android.R.layout.simple_list_item_1,ss);
                        listView1.setAdapter(adapter);
                    }
                });
            }
        });
    }
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getJsonFromOneNet();
            handler.postDelayed(this, 1000);
        }
    };

    // 在onDestroy方法中停止循环
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

}
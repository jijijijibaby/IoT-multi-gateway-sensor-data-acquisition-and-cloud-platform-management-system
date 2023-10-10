package com.jiafei.test;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.security.interfaces.DSAPrivateKey;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostData {
    public String PostDataMethod(String commd){
        OkHttpClient client=new OkHttpClient();
        String url=String.format("http://api.heclouds.com/cmds?device_id=1074803160");

        String body=String.format(commd);
        Log.e("url",url);
        Log.e("body",body);
        RequestBody bodyJson=RequestBody.create(MediaType.parse("application/json;charset=utf-8"),body);
        Request request = new Request.Builder()
                .url(url).headers(new Headers.Builder().add("api-key","R=J4k2v8Im6z7mWDxx7ODep7tdQ=")
                        .build()).post(bodyJson).build();
        Call call=client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("e","post请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
        Response response=null;
        String responseData="";
        try{
            response=client.newCall(request).execute();
            String jsonResStr=response.body().string();
            Log.e("onenet回应数据",jsonResStr);
            JSONObject jsonObject=new JSONObject(jsonResStr);
            responseData=jsonObject.getString("error");
            Log.e("val",responseData);
        }catch (Exception e){
            e.printStackTrace();
        }
        return responseData;
    }
}

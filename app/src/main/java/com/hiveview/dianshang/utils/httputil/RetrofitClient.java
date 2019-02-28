package com.hiveview.dianshang.utils.httputil;

import com.google.gson.Gson;
import com.hiveview.dianshang.entity.commodity.DevicesAPI;
import com.hiveview.dianshang.utils.httputil.Converter.AvatarConverter;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

import static com.hiveview.dianshang.utils.httputil.OkHttpHelper.addLogClient;


/**
 * Created by LiCola on  2016/04/16  0:08
 */
public class RetrofitClient {

    //所有的联网地址 统一成https
    public final static String mBaseUrl = "http://192.168.0.122:7978/launcher/api/";

    public static Gson gson = new Gson();

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();




    private static DevicesAPI devicesAPI;



    public static DevicesAPI getDevicesAPI(){
        if(devicesAPI ==null){
            Retrofit retrofit = builder
                    .client(addLogClient(httpClient).build())
                    .addConverterFactory(AvatarConverter.create(gson))
                    .build();
            devicesAPI = retrofit.create(DevicesAPI.class);
        }
        return devicesAPI;
    }



    public static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(mBaseUrl)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create());


    private void setHeader() {
        httpClient.addInterceptor(chain -> {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Accept", "*/*")
                    .addHeader("Cookie", "add cookies here")
                    .build();
            return chain.proceed(request);

        });
    }


}

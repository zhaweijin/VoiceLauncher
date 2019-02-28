package com.hiveview.dianshang.entity.commodity;


import com.hiveview.dianshang.entity.channel.ChannelMessage;
import com.hiveview.dianshang.entity.content.ContentMessage;
import com.hiveview.dianshang.entity.background.BackgroundMessage;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;


public interface DevicesAPI {



    //http://172.16.0.89:8080/api/tv/category/getRootCategory?params={%22pageIndex%22:1,%22pageSize%22:10}&token=2f1e344843b733af97e641e9a517347c_4055212
    @POST("speech/carousel/getSpeechCarouselData")
    Observable<BackgroundMessage> httpGetLauncherBackgroundData(@Body RequestBody requestBody);



    @POST("speech/channel/getSpeechChannelData")
    Observable<ChannelMessage> httpGetLauncherChannelData(@Body RequestBody requestBody);


    @POST("speech/content/getSpeechContentData")
    Observable<ContentMessage> httpGetLauncherContentData(@Body RequestBody requestBody);
}

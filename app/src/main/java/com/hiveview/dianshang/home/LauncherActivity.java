package com.hiveview.dianshang.home;



import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hiveview.dianshang.R;
import com.hiveview.dianshang.base.BaseActivity;
import com.hiveview.dianshang.constant.ConStant;
import com.hiveview.dianshang.entity.channel.ChannelData;
import com.hiveview.dianshang.entity.channel.ChannelMessage;
import com.hiveview.dianshang.entity.content.ContentData;
import com.hiveview.dianshang.entity.content.ContentMessage;
import com.hiveview.dianshang.entity.background.BackgroundData;
import com.hiveview.dianshang.entity.background.BackgroundMessage;
import com.hiveview.dianshang.utils.FrescoHelper;
import com.hiveview.dianshang.utils.ToastUtils;
import com.hiveview.dianshang.utils.Utils;
import com.hiveview.dianshang.utils.httputil.RetrofitClient;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LauncherActivity extends BaseActivity {

    private static final String TAG = "main";

    /**
     * 节目内容展示器
     */
    @BindView(R.id.banner_content)
    MZBannerView bannerContent;

    /**
     * 背景图
     */
    @BindView(R.id.background)
    SimpleDraweeView background;

    /**
     * 左上角时间
     */
    @BindView(R.id.time)
    TextView time;

    /**
     * 向导提示语
     */
    @BindView(R.id.guide)
    TextView guide;

    /**
     * 时间定时器，每间隔1s刷新一次
     */
    private final int TIMER_INTERNAL = 1;



    /**
     * 背景图接口最大请求的页数
     */
    private int maxBackgroundPage = 1;
    /**
     * 背景图当前页数
     */
    private int backgroundPage=1;
    /**
     * 背景图切换的时间间隔
     */
    private int backgroundIntervalTime = 30;

    /**
     * 背景图显示的数组位置
     */
    private int backgroundIndex = 0;

    /**
     * 背景数据数组
     */
    private List<BackgroundData> backgroundDatas = new ArrayList<>();




    /**
     * 最大频道页数
     */
    private int maxChannelPage=1;
    /**
     * 当前频道页数
     */
    private int channelPage=1;

    /**
     * 教育频道数据数组
     */
    private List<ChannelData> channelDatas = new ArrayList<>();



    /**
     * 教育频道节目数据数组
     */
    private List<ContentData> teachContentDatas = new ArrayList<>();
    /**
     * 教育频道以外节目数据数组
     */
    private List<ContentData> otherContentDatas = new ArrayList<>();


    private int contentIntervalTime=10;

    private Subscription timer_conent_subscription;

    private Subscription get_content_subscription;

    /**
     * while 循环最大的次数，避免线上死锁
     */
    private int MAX_TIMES = 100;


    private int totalSize=0;

    /**
     * 传入的设备码
     */
    private String deviceCode="";

    /**
     * 传入的设备用户名
     */
    private String deviceUserName="";

    /**
     * 判断是否首次展示内容
     */
    private boolean isFirstShow = false;

    /**
     * 监听网络状态
     */
    private Subscription network_sc_listener;


    private List<ContentData> readyContentDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        deviceCode="";
        deviceUserName="";
        handleTimeInterval();


        initData();
        listenerNetwork();
    }



    private void initData(){
        if(Utils.networkIsConnected(mContext)){
            Utils.print(TAG,"start load data........");
            getLauncherBackgroud(true,backgroundPage);
            getLauncherChannel(true,1);
        }else{
            setErrorBackground();
        }
    }


    /**
     * 格式化显示时间
     */
    private void showDate(){
        SimpleDateFormat format =  new SimpleDateFormat("HH:mm:ss");
        Date curDate =  new Date(System.currentTimeMillis());
        String str = format.format(curDate);
        //Utils.print(TAG,"time="+str);
        time.setText(str);
    }


    /**
     * 时间间隔刷新
     */
    private void handleTimeInterval(){

        showDate();

        Observable<Long> ob = Observable.interval(TIMER_INTERNAL, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread());
        Subscription time_subscription = ob.subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Long aLong) {
                //Utils.print(TAG,".....time.....");
                showDate();
            }
        });

        addSubscription(time_subscription);
    }

    /**
     * 显示节目内容
     * @param list
     */
    private void showContentData(List<ContentData> list){

        Utils.print(TAG,"showContentData>>>");
        //mNormalBanner.setDuration(int duration);//设置ViewPager（Banner）切换速度
        bannerContent.setIndicatorRes(R.drawable.dot_unselect_image,R.drawable.dot_select_image); // 设置 Indicator资源
        bannerContent.setIndicatorVisible(true);//设置是否显示Indicator
        bannerContent.setDelayedTime(contentIntervalTime*1000);//设置BannerView 的切换时间间隔
        bannerContent.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int position) {
                Toast.makeText(LauncherActivity.this,"click page:"+position, Toast.LENGTH_LONG).show();
                if(list.get(position).getPackageName()!=null && !list.get(position).getPackageName().equals("")){
                    Utils.startAppByPackage(mContext,list.get(position).getPackageName());
                }
            }
        });

        bannerContent.addPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Utils.print(TAG,"current item="+position);
                guide.setText(list.get(position).getGuide());
                if(position==list.size()-1){
                    showNextContent();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        guide.setText(list.get(0).getGuide());
        bannerContent.setPages(list, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });

        bannerContent.start();
    }

    public class BannerViewHolder implements MZViewHolder<ContentData> {
        private TextView content;
        private TextView typeName;
        private LinearLayout layout_item;

        @Override
        public View createView(Context context) {
            // 返回页面布局文件
            View view = LayoutInflater.from(context).inflate(R.layout.banner_item,null);
            content = (TextView) view.findViewById(R.id.content);
            typeName = (TextView)view.findViewById(R.id.channel_name);
            layout_item = (LinearLayout)view.findViewById(R.id.layout_item);

            layout_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showToast(mContext,"wwwwww");
                }
            });

            return view;
        }

        @Override
        public void onBind(Context context, int position, ContentData data) {
            // 数据绑定
            content.setText(data.getContent());
            typeName.setText(data.getTypeName());
            //Utils.print(TAG,"text==="+data.getContent());

        }
    }


    @Override
    public void onPause() {
        super.onPause();
        bannerContent.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        bannerContent.start();
    }


    /**
     * 网络错误显示的默认背景
     */
    private void setErrorBackground(){
        Uri uri = Uri.parse("res://com/"+ + R.drawable.default_bg);
        FrescoHelper.setImage(background, uri);
    }

    /**
     * 从后台获取所有背景图到缓存
     * 测试要点：查看获取的所有后台数据，跟本地缓存的总是是否一致
     * @param isFirst
     * @param page
     */
    public void getLauncherBackgroud(boolean isFirst,int page) {
        Utils.print(TAG, "getLauncherBackgroud page="+page);

        String input = "";
        org.json.JSONObject json;
        try {
            json = new org.json.JSONObject();
            json.put("deviceCode",deviceCode);
            json.put("deviceUserName",deviceUserName);
            json.put("pageNum", page);
            json.put("pageSize", ConStant.PAGESIZE);
            input = json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),input);
        Subscription sc_backgound = RetrofitClient.getDevicesAPI()
                .httpGetLauncherBackgroundData(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BackgroundMessage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        setErrorBackground();
                    }

                    @Override
                    public void onNext(BackgroundMessage data) {
                        Utils.print(TAG, "status==" + data.getCode());
                        if(data.getCode()==200){
                            for (int i = 0; i <data.getResult().getRecords().size() ; i++) {
                                Utils.print(TAG,"url="+data.getResult().getRecords().get(i).getImgUrl());
                            }

                            backgroundDatas.addAll(data.getResult().getRecords());
                            backgroundPage++;
                            Utils.print(TAG,"get all background data size="+backgroundDatas.size());

                            if(isFirst){ //首次访问统计总页数
                                int totalCount = data.getResult().getRecords().size();
                                if(totalCount>0){
                                    backgroundIntervalTime = data.getResult().getRecords().get(0).getIntervals();
                                    maxBackgroundPage = totalCount/ConStant.PAGESIZE;
                                    if(totalCount%ConStant.PAGESIZE!=0){
                                        maxBackgroundPage++;
                                    }
                                    Utils.print(TAG,"totalcount="+totalCount+",background maxpage="+maxBackgroundPage);
                                    showAndSwitchBackground();
                                }
                            }

                            if(backgroundPage>maxBackgroundPage) return;
                            getLauncherBackgroud(false,backgroundPage);
                        }else {
                            setErrorBackground();
                        }

                    }
                });
        addSubscription(sc_backgound);
    }



    private void initParams(){
        backgroundPage=1;
        maxBackgroundPage=1;
        backgroundIntervalTime=30;

        channelPage=1;
        maxChannelPage=1;

        contentIntervalTime=10;
    }

    /**
     * 循环切换背景图
     */
    private void showAndSwitchBackground(){
        Utils.print(TAG,"showAndSwitchBackground backgroundIntervalTime="+backgroundIntervalTime);
        //先显示背景图，再轮询
        FrescoHelper.setImage(background, Uri.parse(backgroundDatas.get(backgroundIndex).getImgUrl()));
        backgroundIndex++;
        if(backgroundIndex>backgroundDatas.size()-1){
            backgroundIndex=0;
        }

        Observable<Long> ob = Observable.interval(backgroundIntervalTime, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread());
        Subscription sc = ob.subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Long aLong) {
                Utils.print(TAG,"backgroundIndex="+backgroundIndex);
                FrescoHelper.setImage(background, Uri.parse(backgroundDatas.get(backgroundIndex).getImgUrl()));
                backgroundIndex++;
                if(backgroundIndex>backgroundDatas.size()-1){
                    backgroundIndex=0;
                }
            }
        });
        addSubscription(sc);
    }


    /**
     * 获取频道数据
     * 要点：所有频道数据获取完成再获取节目内容
     */
    public void getLauncherChannel(boolean isFirst,int page) {
        Utils.print(TAG, "getLauncherChannel page="+page);

        String input = "";
        org.json.JSONObject json;
        try {
            json = new org.json.JSONObject();
            json.put("deviceCode",deviceCode);
            json.put("deviceUserName",deviceUserName);
            json.put("pageNum", page);
            json.put("pageSize", ConStant.PAGESIZE);
            input = json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),input);

        Subscription sc_channel = RetrofitClient.getDevicesAPI()
                .httpGetLauncherChannelData(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ChannelMessage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ChannelMessage data) {
                        //Utils.print(TAG, "status==" + data.getCode());

                        if(data.getCode()==200){
                            /*for (int i = 0; i <data.getResult().getRecords().size() ; i++) {
                                Utils.print(TAG,"channel guide="+data.getResult().getRecords().get(i).getGuide());
                            }*/

                            channelDatas.addAll(data.getResult().getRecords());
                            channelPage++;
                            Utils.print(TAG,"get all channel size="+channelDatas.size());


                            if(isFirst){ //首次访问统计总页数
                                int totalCount = data.getResult().getRecords().size();
                                if(totalCount>0){
                                    maxChannelPage = totalCount/ConStant.PAGESIZE;
                                    if(totalCount%ConStant.PAGESIZE!=0){
                                        maxChannelPage++;
                                    }
                                    Utils.print(TAG,"chanel totalcount="+totalCount+",channel maxpage="+maxChannelPage);
                                }
                            }

                            Utils.print(TAG,"channelPage="+channelPage);
                            if(channelPage>maxChannelPage){
                                //确保全部频道数据获取完成
                                Utils.print(TAG,"channel data load finish");
                                if(channelDatas.size()<=0) return;
                                //检测从第几个频道后，有节目内容,待增加处理
                                for (int i = 0; i < channelDatas.size(); i++) {
                                    getLauncherContent(true,1,i);
                                }
                                return;
                            }
                            //暂时不考虑频道的分页
                            //getLauncherChannel(false,channelPage);
                        }
                    }
                });
        addSubscription(sc_channel);
    }


    /**
     * 获取节目内容
     * 测试要点:查看获取的总是是否一致，节目内容分类是否正常,所有节目内容必须大于4条
     * 必须先单独后期节目内容的第一页数据，因为需要知道分页数据组合
     *
     * @param channelPos 频道数组位置
     * @param page 获取节目内容的当前页码
     */
    public void getLauncherContent(boolean isFirst, int page,int channelPos) {
        Utils.print(TAG, "getLauncherContent page="+page+",channelPos="+channelPos);

        String input = "";
        org.json.JSONObject json;
        try {
            json = new org.json.JSONObject();
            json.put("channelSn",channelDatas.get(channelPos).getSn());
            json.put("pageNum", page);
            json.put("pageSize", ConStant.PAGESIZE);
            input = json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),input);

        Subscription sc_content = RetrofitClient.getDevicesAPI()
                .httpGetLauncherContentData(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ContentMessage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ContentMessage data) {
                        Utils.print(TAG, "status==" + data.getCode());
                        if(data.getCode()==200){
                            for (int i = 0; i <data.getResult().getRecords().size() ; i++) {
                                Utils.print(TAG,"content="+data.getResult().getRecords().get(i).getContent());
                            }

                            Utils.print(TAG,"size="+data.getResult().getRecords().size());

                            //把数据添加到容器
                            for (int i = 0; i < data.getResult().getRecords().size(); i++) {
                                ContentData contentData =  data.getResult().getRecords().get(i);
                                contentData.setTypeName(channelDatas.get(channelPos).getName());
                                contentData.setGuide(channelDatas.get(channelPos).getGuide());
                                /*if(data.getResult().getRecords().get(i).getResType()==ConStant.TEACH_CHANNEL){
                                    teachContentDatas.add(contentData);
                                }else{*/
                                    otherContentDatas.add(contentData);
                                //}
                            }

                            Utils.print(TAG,"get teach content size="+teachContentDatas.size()+",other content size="+otherContentDatas.size()+",total size="+(otherContentDatas.size()+teachContentDatas.size()));

                            int maxContentPage=1;
                            if(isFirst){ //首次访问统计总页数
                                int totalCount = data.getResult().getRecords().size();
                                if(totalCount>0){
                                    maxContentPage = totalCount/ConStant.PAGESIZE;
                                    if(totalCount%ConStant.PAGESIZE!=0){
                                        maxContentPage++;
                                    }
                                    Utils.print(TAG,"totalcount="+totalCount+",channel maxpage="+maxChannelPage);
                                }
                                totalSize = totalSize+totalCount;
                            }

                            firstShowContent();

                            if(maxContentPage<2) return;
                            //同时获取当前频道，节目内容的其他分页数据,第二页开始
                            int nextID  = page;
                            nextID++;
                            Utils.print(TAG,"has content from channel id="+nextID);
                            for (int i = nextID; i <= maxContentPage; i++) {
                                getLauncherMoreContent(i,channelPos,maxContentPage);
                            }
                        }else{
                            //没有节目内容的情况
                            /*int nextChannelPos = channelPos;
                            nextChannelPos++;
                            getLauncherContent(true,1,nextChannelPos);*/
                        }
                    }
                });
        addSubscription(sc_content);
    }





    public void getLauncherMoreContent(int page,int channelPos,int maxpage) {
        Utils.print(TAG, "getLauncherContent page="+page);

        String input = "";
        org.json.JSONObject json;
        try {
            json = new org.json.JSONObject();
            json.put("channelSn",channelDatas.get(channelPos).getSn());
            json.put("pageNum", page);
            json.put("pageSize", ConStant.PAGESIZE);
            input = json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"),input);

        Subscription sc_content = RetrofitClient.getDevicesAPI()
                .httpGetLauncherContentData(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ContentMessage>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ContentMessage data) {
                        Utils.print(TAG, "status==" + data.getCode());
                        if(data.getCode()==200){
                            /*for (int i = 0; i <data.getResult().getRecords().size() ; i++) {
                                Utils.print(TAG,"content="+data.getResult().getRecords().get(i).getContent());
                            }*/

                            for (int i = 0; i < data.getResult().getRecords().size(); i++) {
                                ContentData contentData =  data.getResult().getRecords().get(i);
                                contentData.setTypeName(channelDatas.get(channelPos).getName());
                                contentData.setGuide(channelDatas.get(channelPos).getGuide());
                                /*if(data.getResult().getRecords().get(i).getResType()==ConStant.TEACH_CHANNEL){
                                    teachContentDatas.add(contentData);
                                }else{*/
                                    otherContentDatas.add(contentData);
                                //}
                            }
                            Utils.print(TAG,"get more teach content size="+teachContentDatas.size()+",other content size="+otherContentDatas.size()+",total size="+(otherContentDatas.size()+teachContentDatas.size()));
                            firstShowContent();
                        }
                    }
                });
        addSubscription(sc_content);
    }


    /**
     * 首次显示节目内容，快速展示
     * @param nextPos 频道节目循环获取的起始位置
     */
    private void firstShowContent(ContentMessage data,int nextPos){
        Utils.print(TAG,"firstShowContent");
        List<ContentData> showContents = new ArrayList<>();
        if(data.getResult().getRecords().size()<4) return;
        for (int i = 0; i < 4; i++) {
            showContents.add(data.getResult().getRecords().get(i));
        }

        //把教育，以及其他节目数组，置位已经显示过，避免第二次重复显示
        for (int i = 0; i < showContents.size(); i++) {
            for (int j = 0; j < teachContentDatas.size(); j++) {
                if(teachContentDatas.get(j).equals(showContents.get(i))){
                    teachContentDatas.get(j).setShowed(true);
                    break;
                }
            }

            for (int j = 0; j < otherContentDatas.size(); j++) {
                if(otherContentDatas.get(j).equals(showContents.get(i))){
                    otherContentDatas.get(j).setShowed(true);
                    break;
                }
            }
        }

        showContentData(showContents); //显示内容



        //test
        for (int i = 0; i < teachContentDatas.size(); i++) {
            Utils.print(TAG,"status="+teachContentDatas.get(i).isShowed());
        }

        for (int i = 0; i < otherContentDatas.size(); i++) {
            Utils.print(TAG,"status="+otherContentDatas.get(i).isShowed());
        }

        /**
         * 再次获取第二个频道以后的首页节目内容,0 对应第一个频道，在之前已经获取
         */
        for (int i = nextPos+1; i < channelDatas.size(); i++) {
            getLauncherContent(true,1,i);
        }
    }


    /**
     * 首页展示节目内容
     */
    private void firstShowContent(){
        if(isFirstShow) return;
        /*if(teachContentDatas.size()<2 || otherContentDatas.size()<2){
            return;
        }*/
        if(otherContentDatas.size()<4) return;
        Utils.print(TAG,"firstShowContent");
        isFirstShow = true;
        Subscription sc = Observable.create(new Observable.OnSubscribe<List<ContentData>>() {
            @Override
            public void call(Subscriber<? super List<ContentData>> subscriber) {
                subscriber.onNext(getShowContents());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<ContentData>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(List<ContentData> datas) {
                showContentData(datas); //显示内容
                testShowState();
            }
        });
        addSubscription(sc);
    }

    /**
     * 测试
     */
    private void testShowState(){
        //test
        /*for (int i = 0; i < teachContentDatas.size(); i++) {
            Utils.print(TAG,"status="+teachContentDatas.get(i).isShowed());
        }

        for (int i = 0; i < otherContentDatas.size(); i++) {
            Utils.print(TAG,"status="+otherContentDatas.get(i).isShowed());
        }*/
    }

    /**
     * 获取任意数组内容的随机数,如果数组内容50，那么查询100次
     * @param list
     * @return
     */
    private ContentData getRandomContent(List<ContentData> list){
        ContentData returnValue=null;
        for (int i = 0; i < 2*list.size(); i++) {
            int pos = getRandomPos(list.size());
            ContentData data = list.get(pos);
            //Utils.print(tag,"get data="+data.getContent()+",isShowed="+data.isShowed());
            if(data.isShowed()){
                continue;
            }
            list.get(pos).setShowed(true);
            returnValue=data;
            break;
        }
        return returnValue;
    }

    /**
     * 复位节目内容数组
     */
    private void resetContentDatas(List<ContentData> list){
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setShowed(false);
        }
    }


    private int getRandomPos(int totalsize){
        return  (int)(Math.random()*(totalsize));
    }

    /**
     * 获取两组教育类型的节目内容
     * @return
     */
    private List<ContentData> getTechContentDatas(){
        List<ContentData> results = new ArrayList<>();
        int times = 1;
        while (true){
            times++;
            results.clear();
            for (int i = 0; i < 2; i++) {
                ContentData data = getRandomContent(teachContentDatas);
                if(data==null){
                    Utils.print(TAG,"reset array");
                    resetContentDatas(teachContentDatas);
                    break;
                }else{
                    results.add(data);
                }
            }
            if(results.size()>=2 || times>MAX_TIMES){
                break;
            }
        }
        return results;
    }



    /**
     * 获取两组其他类型的节目内容
     * @return
     */
    private List<ContentData> getOtherContentDatas(){
        List<ContentData> results = new ArrayList<>();
        int times = 1;
        while (true){
            times++;
            results.clear();
            for (int i = 0; i < 4; i++) {
                ContentData data = getRandomContent(otherContentDatas);
                if(data==null){
                    Utils.print(TAG,"reset array");
                    resetContentDatas(otherContentDatas);
                    break;
                }else{
                    results.add(data);
                }
            }
            if(results.size()>=2 || times>MAX_TIMES){
                break;
            }
        }
        return results;
    }


    /**
     * 切换下一轮节目内容，并且展示在bannel里面
     */
    private void showNextContent(){
        if(timer_conent_subscription!=null && !timer_conent_subscription.isUnsubscribed()){
            timer_conent_subscription.unsubscribe();
        }

        if(get_content_subscription!=null && !get_content_subscription.isUnsubscribed()){
            get_content_subscription.unsubscribe();
        }

        /**
         * 获取数据启动线程
         */
        get_content_subscription = Observable.create(new Observable.OnSubscribe<List<ContentData>>() {
            @Override
            public void call(Subscriber<? super List<ContentData>> subscriber) {
                subscriber.onNext(getShowContents());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<ContentData>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(List<ContentData> datas) {
                 readyContentDatas.clear();
                 readyContentDatas.addAll(datas);
            }
        });
        addSubscription(get_content_subscription);

        /**
         * 滚动到最后一个最后1s展示内容
         */
        Observable<Long> ob = Observable.timer(contentIntervalTime-1, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        timer_conent_subscription = ob.subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long l) {
                        if(readyContentDatas.size()>0){
                            List<ContentData> datas = new ArrayList<>();
                            datas.addAll(readyContentDatas);
                            showContentData(datas);
                            testShowState();
                        }

                    }
                });
        addSubscription(timer_conent_subscription);
    }


    /**
     * 获取用于展示的随机节目内容
     * @return
     */
    private List<ContentData> getShowContents(){
        long time1 = System.currentTimeMillis();
        List<ContentData> results = new ArrayList<>();
        //results.addAll(getTechContentDatas());
        results.addAll(getOtherContentDatas());
        long time2 = System.currentTimeMillis();
        Utils.print(TAG,"get random four data time="+(time2-time1));
        //test
        Utils.print(TAG,"result size="+results.size());
        for (int i = 0; i < results.size(); i++) {
            Utils.print(TAG,"show content="+results.get(i).getContent());
        }
        return results;
    }




    /**
     * 监听网络状态
     */
    private void listenerNetwork(){
        Utils.print(TAG, "start listener......");
        Observable<Long> ob = Observable.interval(10, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread());
        network_sc_listener = ob.subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Long aLong) {
                Utils.print(TAG,"......listener......");
                if(channelDatas.size()==0){
                    initData();
                }else{
                    cancelNetworkListener();
                }
            }
        });
        addSubscription(network_sc_listener);
    }

    private void cancelNetworkListener(){
        Utils.print(TAG,"cancel network listener ");
        if(network_sc_listener!=null && !network_sc_listener.isUnsubscribed()){
            network_sc_listener.unsubscribe();
        }
    }
}

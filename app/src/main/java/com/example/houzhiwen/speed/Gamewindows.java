package com.example.houzhiwen.speed;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Date;

import static com.amap.api.maps.AMapUtils.calculateLineDistance;


/**
 * Created by 四季折之羽 on 2017/6/2.
 */

public class Gamewindows extends AppCompatActivity implements View.OnClickListener, LocationSource, AMapLocationListener {

    private Button btn_click;
    private Button btn_gameset;
    private EditText mResultText;
    private MapView mapView;
    private AMap aMap;
    private Double latitude;
    private Double longitude;
    private MyLocationStyle myLocationStyle;
    private LatLng firstPoint;
    private LatLng secondPoint;
    private Circle circle;
    private float radius;
    private int gameNum;
    private Intent intent;
    public static String gender = "xiaoyan";
    public static int speed = 50;
    public static int volume = 80;
    public static int type = 1;

    /** 声明mLocationClient对象 */
    private AMapLocationClient mLocationClient = null;
    /** 声明mLocationOption对象 */
    private AMapLocationClientOption mLocationOption = null;
    /** 声明mListener对象，定位监听器*/
    private LocationSource.OnLocationChangedListener mListener = null;
    /** 标识，用于判断是否只显示一次定位信息和用户重新定位*/
    private boolean isFirstLoc = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.game_layout);
        ActionBar actionbar= getSupportActionBar();
        if(actionbar!=null) actionbar.hide();
        Button btn_rank=(Button)findViewById(R.id.button_getrank);
        btn_rank.setOnClickListener(new onclick1());
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        init();
        //开始定位
        location();

    }
    private class onclick1 implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Button btn_rank=(Button)findViewById(R.id.button_getrank);
            btn_rank.setOnClickListener(new onclick2());
            btn_rank.setBackgroundResource(R.drawable.rank_behind);
            RelativeLayout rank=(RelativeLayout)findViewById(R.id.rank);
            rank.setVisibility(View.VISIBLE);

        }
    }
    private class onclick2 implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Button btn_rank=(Button)findViewById(R.id.button_getrank);
            btn_rank.setOnClickListener(new onclick1());
            btn_rank.setBackgroundResource(R.drawable.rank_front);
            RelativeLayout rank=(RelativeLayout)findViewById(R.id.rank);
            rank.setVisibility(View.GONE);
        }
    }

    private class onclick3 implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Gamewindows.this, SoundSetting.class);
            startActivity(intent);
        }
    }

    public void showdialog(View view) {
        AlertDialog.Builder alertdialogbuilder=new AlertDialog.Builder(this);
        alertdialogbuilder.setMessage("您确认要退出当前游戏?");
        alertdialogbuilder.setPositiveButton("确定", click1);
        alertdialogbuilder.setNegativeButton("取消", click2);
        AlertDialog alertdialog1=alertdialogbuilder.create();
        alertdialog1.show();
    }
    private DialogInterface.OnClickListener click1=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0,int arg1){
            Intent intent=new Intent(Gamewindows.this,FirstActivity.class);
            startActivity(intent);
            Gamewindows.this.finish();
            }
        };
    private DialogInterface.OnClickListener click2=new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface arg0,int arg1)
        {
            arg0.cancel();
            }
        };
    private void init() {
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=59186484");
        if (aMap == null) {
            aMap = mapView.getMap();
            UiSettings settings = aMap.getUiSettings();
            aMap.setLocationSource(this);//设置了定位的监听
            // 是否显示定位按钮
            settings.setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
        }
        intent = getIntent();
        gameNum = Integer.valueOf(intent.getStringExtra("gameNum"));
        radius = Float.valueOf(intent.getStringExtra("radius"));
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        btn_click = (Button) findViewById(R.id.btn_click);
        mResultText = ((EditText) findViewById(R.id.result));
        btn_click.setOnClickListener(this);
        btn_gameset = (Button)findViewById(R.id.btn_gameset);
        btn_gameset.setOnClickListener(new onclick3());

    }

    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        // TODO Auto-generated method stub

        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCity();//城市信息
                amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
                latitude = amapLocation.getLatitude();
                longitude = amapLocation.getLongitude();
                //将地图移动到定位点
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                //点击定位按钮 能够将地图的中心移动到定位点
                mListener.onLocationChanged(amapLocation);
                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                firstPoint = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(20));
                    if(type == 1) {
                        secondPoint = newPoint(firstPoint);
                    }
                    else{
                        secondPoint = newPoint2(firstPoint);
                    }
                    circle = aMap.addCircle(new CircleOptions().
                            center(firstPoint).
                            radius(radius).
                            fillColor(Color.argb(25, 1, 1, 1)).
                            strokeColor(Color.argb(225, 1, 1, 1)).
                            strokeWidth(3));
                    isFirstLoc = false;
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("info", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
                Toast.makeText(getApplicationContext(), "定位失败"+amapLocation.getErrorCode(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private LatLng newPoint(LatLng firstPoint){
        boolean flag = false;
        LatLng secondPoint = null;
        while(!flag) {
            secondPoint = new LatLng((Math.random()/10.0 + firstPoint.latitude), (Math.random()/100.0 + firstPoint.longitude));
            if(calculateLineDistance(firstPoint, secondPoint) < radius)
                flag = true;
        }
        return secondPoint;
    }
    private LatLng newPoint2(LatLng firstPoint){
        boolean flag = false;
        LatLng secondPoint = null;
        while(!flag) {
            secondPoint = new LatLng((Math.random()/100.0 + firstPoint.latitude), (Math.random()/100.0 + firstPoint.longitude));
            if(calculateLineDistance(firstPoint, secondPoint) < radius)
                flag = true;
        }
        return secondPoint;
    }
    @Override
    public void onClick(View v) {
        btnVoice();
    }

    //TODO 开始说话：
    private void btnVoice() {
        RecognizerDialog dialog = new RecognizerDialog(this,null);
        dialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        dialog.setParameter(SpeechConstant.ACCENT, "mandarin");

        dialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                printResult(recognizerResult);
            }
            @Override
            public void onError(SpeechError speechError) {
            }
        });
        dialog.show();
        Toast.makeText(this, "请提问", Toast.LENGTH_SHORT).show();
    }

    //回调结果：
    private void printResult(RecognizerResult results) {
        String text = parseIatResult(results.getResultString());
        // 自动填写地址
        if(text.indexOf("多远")>=0) {
            mResultText.setText("");
            mResultText.append(String.valueOf(calculateLineDistance(firstPoint, secondPoint))+"米");
            speech(mResultText.getText().toString());
        }
        else if(text.indexOf("到")>=0) {
            mResultText.setText("");
            if (type == 1 && calculateLineDistance(firstPoint, secondPoint) < 5.0) {
                mResultText.append("到了");
                secondPoint = newPoint(firstPoint);
                TextView t1 = (TextView) findViewById(R.id.text_score);
                t1.setText(String.valueOf(Integer.valueOf(t1.getText().toString()) + 1));
                TextView t2 = (TextView)findViewById(R.id.current_scores);
                t2.setText(t1.getText().toString());
            }
            else if(type == 2 && calculateLineDistance(firstPoint, secondPoint) < 1.0){
                mResultText.append("到了");
                secondPoint = newPoint2(firstPoint);
                TextView t1 = (TextView) findViewById(R.id.text_score);
                t1.setText(String.valueOf(Integer.valueOf(t1.getText().toString()) + 1));
                TextView t2 = (TextView)findViewById(R.id.current_scores);
                t2.setText(t1.getText().toString());
            }
            else
                mResultText.append("没到");
            speech(mResultText.getText().toString());
            gameNum--;
            if(gameNum <= 0){
                TextView t = (TextView)findViewById(R.id.text_score);
                Intent intent = new Intent(Gamewindows.this, GameResult.class);
                intent.putExtra("final scores",t.getText().toString());
                startActivity(intent);
                Gamewindows.this.finish();
            }
        }
        else
            mResultText.append("");
    }

    private void speech(String s){
        //1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(Gamewindows.this, null);

        /**
         2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
         *
         */

        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);

        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        mTts.setParameter(SpeechConstant.VOICE_NAME, gender);//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, String.valueOf(speed));//设置语速
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        mTts.setParameter(SpeechConstant.VOLUME, String.valueOf(volume));//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        boolean isSuccess = mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts2.wav");
//        Toast.makeText(MainActivity.this, "语音合成 保存音频到本地：\n" + isSuccess, Toast.LENGTH_LONG).show();
        //3.开始合成
        int code = mTts.startSpeaking(s, mSynListener);

        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                //上面的语音配置对象为初始化时：
                Toast.makeText(Gamewindows.this, "语音组件未安装", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(Gamewindows.this, "语音合成失败,错误码: " + code, Toast.LENGTH_LONG).show();
            }
        }
    }

    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener() {
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
        }

        //缓冲进度回调
        //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        //开始播放
        public void onSpeakBegin() {
        }

        //暂停播放
        public void onSpeakPaused() {
        }

        //播放进度回调
        //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        //恢复播放回调接口
        public void onSpeakResumed() {
        }

        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }
    };

    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mLocationClient.stopLocation();//停止定位
        mLocationClient.onDestroy();//销毁定位客户端。
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }
}


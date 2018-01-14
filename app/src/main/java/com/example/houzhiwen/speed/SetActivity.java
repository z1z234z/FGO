package com.example.houzhiwen.speed;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import java.util.Date;

public class SetActivity extends AppCompatActivity implements View.OnClickListener, LocationSource, AMapLocationListener, AMap.OnMarkerDragListener {

    private Button btn_set;
    private MapView mapView;
    private AMap aMap;
    private MyLocationStyle myLocationStyle;
    private Marker makerA;
    private Marker makerB;
    private LatLng myPoint;
    private LatLng targetPoint;
    private Circle circle;
    private float distance;
    private TextView Text;
    private int gameNum;
    private Intent intent;
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
        setContentView(R.layout.activity_set);
        ActionBar actionbar= getSupportActionBar();
        if(actionbar!=null) actionbar.hide();
        if(Gamewindows.type == 1){
            distance = 600;
        }else{
            distance = 60;
        }
        intent = getIntent();
        gameNum = Integer.valueOf(intent.getStringExtra("gameNum"));
        Button btn_return = (Button) findViewById(R.id.button_mapreturn);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetActivity.this, GameSetting.class);
                startActivity(intent);
            }
        });
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        //开始定位
        location();
        Text = (TextView) findViewById(R.id.info_text);
        Text.setText("长按Marker可拖动\n默认半径为："+ distance +"m");
    }

    private void location() {
        if (aMap == null) {
            aMap = mapView.getMap();
            UiSettings settings = aMap.getUiSettings();
            aMap.setLocationSource(this);//设置了定位的监听
            // 是否显示定位按钮
            settings.setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
        }
        btn_set = (Button) findViewById(R.id.btn_set);
        btn_set.setOnClickListener(this);
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
                //点击定位按钮 能够将地图的中心移动到定位点
                mListener.onLocationChanged(amapLocation);
                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //设置缩放级别
                    if(Gamewindows.type == 1) {
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    }
                    else{
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(20));
                    }
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                    aMap.setOnMarkerDragListener(this);
                    myPoint = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                    if(Gamewindows.type == 1) {
                        targetPoint = new LatLng(amapLocation.getLatitude() + 0.005242, amapLocation.getLongitude());
                    }else{
                        targetPoint = new LatLng(amapLocation.getLatitude() + 0.0005242, amapLocation.getLongitude());
                    }
                    makerA = aMap.addMarker(new MarkerOptions().position(myPoint)
                            .draggable(false)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    makerB = aMap.addMarker(new MarkerOptions().position(targetPoint)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                    makerA.setTitle("你的位置");
                    makerA.showInfoWindow();
                    makerB.setTitle("拖动以设置半径");
                    makerB.showInfoWindow();
                    distance = AMapUtils.calculateLineDistance(makerA.getPosition(), makerB.getPosition());
                    circle = aMap.addCircle(new CircleOptions().
                            center(myPoint).
                            radius(distance).
                            fillColor(Color.argb(125, 1, 1, 1)).
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

    /**
     *  在marker拖动过程中回调此方法, 这个marker的位置可以通过getPosition()方法返回。
     *  这个位置可能与拖动的之前的marker位置不一样。
     *  marker 被拖动的marker对象。
     */
    @Override
    public void onMarkerDrag(Marker marker) {

        distance = AMapUtils.calculateLineDistance(makerA.getPosition(), makerB.getPosition());
        Text.setText("长按Marker可拖动\n两点间距离为："+distance+"m");
        circle.setRadius(distance);
    }

    /**
     * 在marker拖动完成后回调此方法, 这个marker的位置可以通过getPosition()方法返回。
     * 这个位置可能与拖动的之前的marker位置不一样。
     * marker 被拖动的marker对象。
     */
    @Override
    public void onMarkerDragEnd(Marker arg0) {
    }

    /** 当marker开始被拖动时回调此方法, 这个marker的位置可以通过getPosition()方法返回。
     * 这个位置可能与拖动的之前的marker位置不一样。
     * marker 被拖动的marker对象。
     */
    @Override
    public void onMarkerDragStart(Marker arg0) {

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
    @Override
    public void onClick(View v) {
        if(Gamewindows.type == 1) {
            if (distance >= 100.0 && distance <= 10000.0) {
                /** 新建一个Intent对象 */
                Intent intent = new Intent(SetActivity.this, Gamewindows.class);
                intent.putExtra("radius", String.valueOf(distance));
                intent.putExtra("gameNum", String.valueOf(gameNum));
                /** 启动一个新的Activity */
                startActivity(intent);
                /** 关闭当前的Activity */
                SetActivity.this.finish();
            } else if (distance < 100.0) {
                Toast.makeText(getApplicationContext(), "半径应大于等于100m", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "半径应小于等于10000m", Toast.LENGTH_LONG).show();
            }
        }
        else{
            if (distance >= 10.0 && distance <= 100.0) {
                /** 新建一个Intent对象 */
                Intent intent = new Intent(SetActivity.this, Gamewindows.class);
                intent.putExtra("radius", String.valueOf(distance));
                intent.putExtra("gameNum", String.valueOf(gameNum));
                /** 启动一个新的Activity */
                startActivity(intent);
                /** 关闭当前的Activity */
                SetActivity.this.finish();
            } else if (distance < 10.0) {
                Toast.makeText(getApplicationContext(), "半径应大于等于10m", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "半径应小于等于100m", Toast.LENGTH_LONG).show();
            }
        }
    }

}

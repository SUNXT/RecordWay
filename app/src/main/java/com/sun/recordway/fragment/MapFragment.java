package com.sun.recordway.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.sun.recordway.R;
import com.sun.recordway.ShowRecordActivity;
import com.sun.recordway.bean.RecordBean;
import com.sun.recordway.database.Database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by SUN on 2017/5/19.
 */

public class MapFragment extends Fragment implements LocationSource, AMapLocationListener, SensorEventListener {

    private String tag = getClass().getSimpleName();
    private final int OPEN_GPS = 0;

    private ImageView iv_map_record;
    private MapView mMapView;
    private AMap aMap;
    private UiSettings mUiSettings;

    /**
     * 定位
     */
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private OnLocationChangedListener mListener;

    /**
     * 传感器
     */
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Sensor aSensor;

    /**
     * 记录重力感应的一些数据
     */
    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];
    float[] values = new float[3];
    float  tempAngles = 0;
    float floats;
    float[] R2 = new float[9];

    /**
     * 地图上画线相关
     */
    private PolylineOptions mPolylineOptions;
    private Polyline mPolyline;
    private List<LatLng> latLngs = new ArrayList<LatLng>();
    private int pointCount = 0;
    private boolean isRecord = false;//是否处于记录状态
    private boolean isGPSConnected = false;//判断GPS是否已经定位成功
    private boolean isFirstRecord = true;

    private long startTime;
    private long endTime;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initView(view, savedInstanceState);
        initSensor();
        addGpsStatusListener();
        initPolylineOption();
        return view;
    }

    /**
     * 初始化View
     * @param view
     * @param savedInstanceState
     */
    private void initView(View view, Bundle savedInstanceState){
        initMap(view, savedInstanceState);

        iv_map_record = (ImageView) view.findViewById(R.id.iv_map_recode);
        iv_map_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iv_map_record.isSelected()){
                    endRecord();
                }else {
                    startRecord();
                }
            }
        });
    }


    /**
     * 初始化地图
     * @param view
     * @param savedInstanceState
     */
    private void initMap(View view, Bundle savedInstanceState){
        mMapView = (MapView) view.findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        aMap = mMapView.getMap();
        mUiSettings = aMap.getUiSettings();
        aMap.setLocationSource(this);//通过aMap对象设置定位数据源的监听

        mUiSettings.setMyLocationButtonEnabled(true); //显示默认的定位按钮

        aMap.setMyLocationEnabled(true);// 可触发定位并显示当前位置

        mUiSettings.setScaleControlsEnabled(true);
        mUiSettings.setAllGesturesEnabled(true);

        initMapLocationIcon();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

    /**
     * 初始化定位图标
     */
    private void initMapLocationIcon(){
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_80));// 设置小蓝点的图标
        myLocationStyle.radiusFillColor(0);
        myLocationStyle.strokeColor(0);
        myLocationStyle.strokeWidth(0);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位图标样式
    }

    /**
     * 初始化传感器
     */
    private void initSensor(){
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        aSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);陀螺仪
        mSensorManager.registerListener(this, aSensor,
                SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * 初始化定位，并开始定位
     */
    private void initLocation(){
        //初始化定位
        mLocationClient = new AMapLocationClient(getActivity());
        //设置定位回调监听，这里要实现AMapLocationListener接口，AMapLocationListener接口只有onLocationChanged方法可以实现，用于接收异步返回的定位结果，参数是AMapLocation类型。
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
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        mLocationClient.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 激化定位
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null){
            initLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    /**
     * 位置变化的回调
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
//                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
//                aMapLocation.getLatitude();//获取纬度
//                aMapLocation.getLongitude();//获取经度
//                aMapLocation.getAccuracy();//获取精度信息
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(aMapLocation.getTime());
//                df.format(date);//定位时间
//                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                aMapLocation.getCountry();//国家信息
//                aMapLocation.getProvince();//省信息
//                aMapLocation.getCity();//城市信息
//                aMapLocation.getDistrict();//城区信息
//                aMapLocation.getStreet();//街道信息
//                aMapLocation.getStreetNum();//街道门牌号信息
//                aMapLocation.getCityCode();//城市编码
//                aMapLocation.getAdCode();//地区编码

                    //设置缩放级别
//                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //将地图移动到定位点
//                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(aMapLocation);
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(aMapLocation.getCountry() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getCity() + ""
                            + aMapLocation.getProvince() + ""
                            + aMapLocation.getDistrict() + ""
                            + aMapLocation.getStreet() + ""
                            + aMapLocation.getStreetNum());
                    Log.d(tag, buffer.toString());

                //再打开GPS和已经定位好开始记录路径
//                if (iv_map_record.isSelected())
//                Toast.makeText(getActivity(), "isRecord:" + isRecord + " isGPSConnected:" + isGPSConnected, Toast.LENGTH_SHORT).show();
                if (isFirstRecord){
                    if (isRecord && isGPSConnected){
                        LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        latLngs.add(pointCount, latLng);
                        mPolylineOptions.add(latLng);
                        pointCount ++;
//                        Toast.makeText(getActivity(), "符合画的要求", Toast.LENGTH_SHORT).show();
                        drawPolyline();
                    }
                }else {
                    Log.d(tag, "isRecord: " + isRecord);
                    if (isRecord){
                        LatLng latLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        latLngs.add(pointCount, latLng);
                        mPolylineOptions.add(latLng);
                        pointCount ++;
//                        Toast.makeText(getActivity(), "符合画的要求", Toast.LENGTH_SHORT).show();
                        drawPolyline();
                    }
                }


            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                Log.d(tag, "定位失败");
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
//            float degree = event.values[0];
//            float bearing = aMap.getCameraPosition().bearing;
//            if (degree + bearing > 360)
//                aMap.setMyLocationRotateAngle(degree + bearing - 360);// 设置小蓝点旋转角度
//            else
//                aMap.setMyLocationRotateAngle(degree + bearing);//
//        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values.clone();
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticFieldValues = event.values.clone();
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            floats = event.values[1];
        }

        //方向传感器
        // 调用getRotaionMatrix获得变换矩阵R[]
        SensorManager.getRotationMatrix(R2, null, accelerometerValues,
                magneticFieldValues);
        SensorManager.getOrientation(R2, values);
        // 经过SensorManager.getOrientation(R, values);得到的values值为弧度
        // 转换为角度
        values[0] = (float) Math.toDegrees(values[0]);
//        Log.d(tag, "values:"+ values[0]);
        //当旋转角度大于1°才转动
        if (Math.abs(tempAngles - values[0]) > 1){
            aMap.setMyLocationRotateAngle(-values[0]);
        }
        tempAngles = values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 监听GPS的状态
     */
    private void addGPSListener(){
        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        ContentObserver mGpsMonitor = new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//                    iv_map_record.setSelected(false);
                    isRecord = false;
//                    isGPSConnected = false;
//                    showOpenGpsDialog();
                }else {
                    addGpsStatusListener();
                }
            }
        };
        getActivity().getContentResolver()
                .registerContentObserver(
                        Settings.Secure
                                .getUriFor(Settings.System.LOCATION_PROVIDERS_ALLOWED),
                        false, mGpsMonitor);
    }

    /**
     * 监听GPS是否已经定位
     */
    private void addGpsStatusListener(){
        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        GpsStatus.Listener statusListener = new GpsStatus.Listener() {
            public void onGpsStatusChanged(int event) {
                GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                //Utils.DisplayToastShort(GPSService.this, "GPS status listener  ");
                switch (event) {
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        locationManager.removeGpsStatusListener(this);
                        isGPSConnected = true;
                        break;
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},2);
        }else {
            locationManager.addGpsStatusListener(statusListener);//侦听GPS状态
        }
    }

    /**
     * 开始记录
     */
    private void startRecord(){
        startTime = System.currentTimeMillis();
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isRecord = true;
            iv_map_record.setSelected(true);
            addGpsStatusListener();
        }else {
            isRecord = false;
            showOpenGpsDialog();
        }
    }

    /**
     * 结束记录路径
     */
    private void endRecord(){
        endTime = System.currentTimeMillis();
        isRecord = false;
        iv_map_record.setSelected(false);
        isFirstRecord = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("是否保存你的路径？");
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveRecord();
            }
        });
        builder.setNegativeButton("舍弃", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                aMap.clear();
                initMapLocationIcon();
                initPolylineOption();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    /**
     * 显示让用户打开GPS的对话框
     */
    private void showOpenGpsDialog(){
        //要求用户打开GPS
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("为了记录更加准确，请先打开GPS");
        dialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // 转到手机设置界面，用户设置GPS
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, OPEN_GPS); // 设置完成后返回到原来的界面
                    }
                });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 初始化画线参数
     */
    private void initPolylineOption(){
        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.width(10);
        mPolylineOptions.color(Color.argb(255, 18,150,219));
        mPolyline = null;
    }

    /**
     * 地图上画线
     */
    private void drawPolyline(){
        if (mPolylineOptions.getPoints().size() > 1) {
            if (mPolyline != null) {
//                Toast.makeText(getActivity(), "开始记录", Toast.LENGTH_SHORT).show();
                mPolyline.setPoints(mPolylineOptions.getPoints());
            } else {
//                Toast.makeText(getActivity(), "mPolyline = null", Toast.LENGTH_SHORT).show();
                mPolyline = aMap.addPolyline(mPolylineOptions);
            }
        }else {
//            Toast.makeText(getActivity(), "getPoints.size < 1", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 保存记录
     */
    private void saveRecord(){
        float distance = 0;
        if (latLngs.size() > 1){
            for (int i = 0; i < latLngs.size()-1; ++ i){
                Log.d(tag, (i+1) + latLngs.get(i).toString());
                distance += AMapUtils.calculateLineDistance(latLngs.get(i), latLngs.get(i+1));
            }
            Log.d(tag, latLngs.size() + latLngs.get(latLngs.size()-1).toString());
            Log.d(tag, "distance:"+ distance);
        }
        Database database = Database.getInstance(getActivity());
        RecordBean recordBean = new RecordBean();
        Calendar c = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(c.get(Calendar.YEAR));
        sb.append(c.get(Calendar.MONTH));
        sb.append(c.get(Calendar.DAY_OF_MONTH));
        sb.append(c.get(Calendar.HOUR_OF_DAY));
        sb.append(c.get(Calendar.MINUTE));
        recordBean.setTitle(sb.toString());
        recordBean.setDistance(String.valueOf(distance));
        recordBean.setDuration((endTime-startTime) / 1000 + "秒");
        if (database.addItem(recordBean)){
            Toast.makeText(getActivity(), "保存成功！", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), ShowRecordActivity.class);
            intent.putExtra("title", recordBean.getTitle());
            intent.putExtra("duration", recordBean.getDuration());
            intent.putExtra("distance", recordBean.getDistance());
            intent.putExtra("isFromMap", true);
            startActivity(intent);
            getActivity().finish();
        }
    }
}

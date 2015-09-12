package me.seewhy.baidumapmarkerclusterdemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private String TAG = "MainActivity";

    private MapView mMapView;
    private Boolean isAverageCenter = false;
    private Integer mMaxZoom = 12;
    private Integer mGridSize = 60;
    private ArrayList<MarkerOptions> mMarkers;
    private Cluster mCluster;
    private double mDistance = 600000;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient = null;
    public BDLocationListener myLocationListener = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        // 生产随机经纬度坐标
        addFakeDate();
        this.isAverageCenter = false;
        // 监听mapview
        setListener();
        LocationOrientate();
    }

    private void init() {
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(5.0f));
        mCluster = new Cluster(this, mMapView, isAverageCenter, mGridSize, mDistance);
        mBaiduMap.clear();
    }

    /**
     * 创建虚拟数据
     */
    private void addFakeDate() {
        mMarkers = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            LatLng pt = new LatLng(((Math.random() * 30 + 15)), ((Math.random() * 25 + 95)));
            MarkerOptions item = new MarkerOptions().position(pt)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.nav_turn_via_1));
            mMarkers.add(item);
        }
    }

    /**
     * 在地图上显示标注点
     * @param list 所有标注点
     */
    private void pinMarkers(ArrayList<MarkerOptions> list) {
        this.mBaiduMap.clear();
        Log.e(TAG, "pinMarkers: size:" + list.size());
        for (int i = 0; i < list.size(); i++) {
            this.mBaiduMap.addOverlay(list.get(i));
        }
        mMapView.refreshDrawableState();
    }

    private void setListener() {

        //点击标注点放大
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (mBaiduMap.getMapStatus().zoom < mBaiduMap.getMaxZoomLevel()) {
                    float newZoom = mBaiduMap.getMapStatus().zoom + 2.0f;
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(newZoom));
                }
                return false;
            }
        });

        //地图加载结束时显示标注点
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                refreshMarks();
            }
        });

        //地图状态改变时重新绘制标注点
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                refreshMarks();
            }
        });

    }

    /**
     * 如果当前地图缩放程度超过设置的最大值，则不改变点坐标，否则重新进行聚合运算
     */
    private void refreshMarks() {
        if (mBaiduMap.getMapStatus().zoom >= MainActivity.this.mMaxZoom) {
            mBaiduMap.clear();
            pinMarkers(refreshVersionClusterMarker(mMarkers));
        } else {
            ArrayList<MarkerOptions> clusters = mCluster.createCluster(refreshVersionClusterMarker(mMarkers));
            mBaiduMap.clear();
            pinMarkers(clusters);
        }
    }

    private void LocationOrientate() {
        mLocationClient = new LocationClient(MainActivity.this);
        myLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myLocationListener);

        LocationClientOption option = new LocationClientOption();
        option.setAddrType("all");
        option.setProdName("定位GPS");
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        mLocationClient.setLocOption(option);
        mLocationClient.start();

        mMapView.refreshDrawableState();
    }


    /**
     * 初始时定位到当前位置
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(currentPosition));
        }
    }

    private ArrayList<MarkerOptions> refreshVersionClusterMarker(ArrayList<MarkerOptions> list) {
        MapStatus mapStatus = mBaiduMap.getMapStatus();
        LatLngBounds latLngBounds = mapStatus.bound;

        ArrayList<MarkerOptions> result = new ArrayList<MarkerOptions>();

        for (int i = 0; i < list.size(); i++) {
            if (latLngBounds.contains(list.get(i).getPosition())) {
                result.add(list.get(i));
            }
        }
        Log.e(TAG, "可见点：" + result.size());

        return result;

    }

}

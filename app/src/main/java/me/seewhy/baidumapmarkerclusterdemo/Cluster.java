package me.seewhy.baidumapmarkerclusterdemo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

    private static final String TAG = "Cluster";
    private static final String TAG_ADD_Cluster = "AddCluster_method";
    private Context mContext;
    private MapView mMapView;
    private Boolean isAverageCenter;
    private int mGridSize;
    private double mDistance;

    private List<ClusterMarker> mClusterMarkers;


    public Cluster(Context context, MapView mapView
            , Boolean isAverageCenter
            , int mGridSize, double mDistance) {
        this.mContext = context;
        this.mMapView = mapView;
        this.isAverageCenter = isAverageCenter;
        this.mGridSize = mGridSize;
        this.mDistance = mDistance;
        mClusterMarkers = new ArrayList<>();
    }

    public ArrayList<MarkerOptions> createCluster(List<MarkerOptions> markerList) {
        this.mClusterMarkers.clear();
        ArrayList<MarkerOptions> itemList = new ArrayList<MarkerOptions>();
        for (int i = 0; i < markerList.size(); i++) {
            addCluster(markerList.get(i));
        }
        for (int i = 0; i < mClusterMarkers.size(); i++) {
            ClusterMarker cm = mClusterMarkers.get(i);
            setClusterDrawable(cm);
            MarkerOptions oi = new MarkerOptions().position(cm.getmCenter()).icon(cm.getMarkerOptions().getIcon());
            itemList.add(oi);
        }

        Log.e(TAG, "itemList.size:" + itemList.size());
        return itemList;
    }

    /**
     * 添加标注点，如果第一次添加，直接新建，否则与地图上原有的点进行判断，如果距离小于mDistance,则进行聚合
     *
     * @param marker
     */

    private void addCluster(MarkerOptions marker) {
        LatLng markGeo = marker.getPosition();
        // 没有ClusterMarkers
        if (mClusterMarkers.size() == 0) {
            ClusterMarker clusterMarker = new ClusterMarker(marker.getPosition());
            clusterMarker.getMarkerOptions().icon(marker.getIcon());
            clusterMarker.AddMarker(marker, isAverageCenter);
            MBound bound = new MBound(markGeo.latitude, markGeo.longitude, markGeo.latitude, markGeo.longitude);
            bound = MapUtils.getExtendedBounds(mMapView, bound, mGridSize);
            clusterMarker.setmGridBounds(bound);
            mClusterMarkers.add(clusterMarker);
        } else {
            ClusterMarker clusterContain = null;
            double distance = mDistance;

            for (int i = 0; i < mClusterMarkers.size(); i++) {
                ClusterMarker clusterMarker = mClusterMarkers.get(i);
                Log.e(TAG_ADD_Cluster, "in mClusterMarker.size  size = = " + mClusterMarkers.size());
                LatLng center = clusterMarker.getmCenter();
                double d = DistanceUtil.getDistance(center, marker.getPosition());

                //[]--------选择clusterMarker 中最近的，clusterMarker-------双重循环-----------[]
                if (d < distance) {
                    distance = d;
                    clusterContain = clusterMarker;
                } else {
//					Log.d(TAG_ADD_Cluster, "d>distence,不满足聚合距离");
                }

            }

            // 现存的clusterMarker 没有符合条件的
            if (clusterContain == null || !isMarkersInCluster(markGeo, clusterContain.getmGridBounds())) {
//				Log.e(TAG_ADD_Cluster, "======clusterContain=======================--------------");
                ClusterMarker clusterMarker = new ClusterMarker(marker.getPosition());

                clusterMarker.getMarkerOptions().icon(marker.getIcon());
                clusterMarker.AddMarker(marker, isAverageCenter);
                MBound bound = new MBound(markGeo.latitude, markGeo.longitude, markGeo.latitude, markGeo.longitude);
                bound = MapUtils.getExtendedBounds(mMapView, bound, mGridSize);
                clusterMarker.setmGridBounds(bound);

                mClusterMarkers.add(clusterMarker);

            } else {
                clusterContain.AddMarker(marker, isAverageCenter);
                Log.e(TAG_ADD_Cluster, "添加到选中 clusterMarker:--->clusterContain.size:---->" + clusterContain.getmMarkers().size());
            }
        }
    }

    /**
     * 设置聚合点的颜色与中间数字
     *
     * @param clusterMarker
     */
    private void setClusterDrawable(ClusterMarker clusterMarker) {

        View drawableView = LayoutInflater.from(mContext).inflate(
                R.layout.drawable_mark, null);
        TextView text = (TextView) drawableView.findViewById(R.id.drawble_mark);
        text.setPadding(3, 3, 3, 3);

        int markNum = clusterMarker.getmMarkers().size();
        Log.e("setClusterDrawable", "!!!!!!!!!!!!!!!!!!!!!!!" + markNum);
        if (markNum >= 2) {
            text.setText(markNum + "");
            if (markNum < 11) {
                text.setBackgroundResource(R.drawable.m0);
            } else if (markNum > 10 && markNum < 21) {
                text.setBackgroundResource(R.drawable.m1);
            } else if (markNum > 20 && markNum < 31) {
                text.setBackgroundResource(R.drawable.m2);
            } else if (markNum > 30 && markNum < 41) {
                text.setBackgroundResource(R.drawable.m3);
            } else {
                text.setBackgroundResource(R.drawable.m4);
            }
            clusterMarker.getMarkerOptions().icon(BitmapDescriptorFactory.fromView(drawableView));
        } else {

            clusterMarker.getMarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.nav_turn_via_1));
        }
    }

    /**
     * 判断坐标点是否在MBound 覆盖区域内
     *
     * @param markerGeo
     * @param bound
     * @return
     */
    private Boolean isMarkersInCluster(LatLng markerGeo, MBound bound) {

        Log.e(TAG, "rightTopLat:" + bound.getRightTopLat());
        Log.e(TAG, "rightTopLng:" + bound.getRightTopLng());
        Log.e(TAG, "leftBottomLat:" + bound.getLeftBottomLat());
        Log.e(TAG, "leftBottomlng:" + bound.getLeftBottomLng());

        if (markerGeo.latitude > bound.getLeftBottomLat()
                && markerGeo.latitude < bound.getRightTopLat()
                && markerGeo.longitude > bound.getLeftBottomLng()
                && markerGeo.longitude < bound.getRightTopLng()) {
            return true;
        }
        return false;

    }

}

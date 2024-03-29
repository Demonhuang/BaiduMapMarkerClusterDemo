package me.seewhy.baidumapmarkerclusterdemo;

import android.util.Log;

import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class ClusterMarker {
    private LatLng mCenter;
    private List<MarkerOptions> mMarkers;
    private MBound mGridBounds;

    public MarkerOptions getMarkerOptions() {
        return mMarkerOptions;
    }

    private MarkerOptions mMarkerOptions;

    public ClusterMarker(LatLng geoPoint) {
        mMarkers = new ArrayList<>();
        mMarkerOptions = new MarkerOptions().position(geoPoint);
    }

    /**
     * 计算平均中心点
     *
     * @return
     */
    private LatLng calAverageCenter() {
        double latitude = 0, longitude = 0;
        int len = mMarkers.size() == 0 ? 1 : mMarkers.size();

        Log.e("calAverageCenter:", "calAverageCenter：------>" + len);

        for (int i = 0; i < len; i++) {
            latitude = latitude + mMarkers.get(i).getPosition().latitude;
            longitude = longitude + mMarkers.get(i).getPosition().longitude;
        }

        return new LatLng((int) (latitude / len), (int) (longitude / len));
    }

    /**
     * ClusterMarker 中添加marker
     *
     * @param marker
     * @param isAverageCenter
     */
    public void AddMarker(MarkerOptions marker, Boolean isAverageCenter) {
        mMarkers.add(marker);

        if (!isAverageCenter) {

            if (mCenter == null)
                this.mCenter = mMarkers.get(0).getPosition();
        } else {
            this.mCenter = calAverageCenter();
        }
    }

    public LatLng getmCenter() {
        return this.mCenter;
    }

    public void setmCenter(LatLng mCenter) {
        this.mCenter = mCenter;
    }

    public List<MarkerOptions> getmMarkers() {
        return mMarkers;
    }

    public void setmMarkers(List<MarkerOptions> mMarkers, Boolean isAverageCenter) {
        this.mMarkers.addAll(mMarkers);
        if (!isAverageCenter) {
            if (mCenter == null) {
                this.mCenter = mMarkers.get(0).getPosition();
            }
        } else
            this.mCenter = calAverageCenter();
    }

    public MBound getmGridBounds() {
        return mGridBounds;
    }

    public void setmGridBounds(MBound mGridBounds) {
        this.mGridBounds = mGridBounds;
    }
}

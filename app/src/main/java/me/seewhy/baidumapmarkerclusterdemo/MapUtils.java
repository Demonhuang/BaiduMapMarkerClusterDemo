package me.seewhy.baidumapmarkerclusterdemo;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.View;
import android.view.View.MeasureSpec;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;


public class MapUtils {
    static double DEF_PI = 3.14159265359; // PI
    static double DEF_2PI = 6.28318530712; // 2*PI
    static double DEF_PI180 = 0.01745329252; // PI/180.0
    static double DEF_R = 6370693.5; // radius of earth

    public static MBound getExtendedBounds(MapView map, MBound bound,
                                           Integer gridSize) {
        MBound tbounds = cutBoundsInRange(bound);

        Projection projection = map.getMap().getProjection();
        Point pixelNE = projection.toScreenLocation(tbounds.getRightTop());
        Point pixelSW = projection.toScreenLocation(tbounds.getLeftBottom());
        pixelNE.x += gridSize;
        pixelNE.y -= gridSize;
        pixelSW.x -= gridSize;
        pixelSW.y += gridSize;
        LatLng rightTop = projection.fromScreenLocation(new Point(pixelNE.x, pixelNE.y));
        LatLng leftBottom = projection.fromScreenLocation(new Point(pixelSW.x, pixelSW.y));

        return new MBound(rightTop.latitude, rightTop.longitude, leftBottom.latitude, leftBottom.longitude);
    }

    public static MBound cutBoundsInRange(MBound bounds) {
        double maxX = getRange(bounds.getRightTopLat(),
                -74, 74);
        double minX = getRange(bounds.getRightTopLat(),
                -74, 740);
        double maxY = getRange(bounds.getRightTopLng(),
                -180, 180);
        double minY = getRange(bounds.getLeftBottomLng(),
                -180, 180);
        return new MBound(minX, minY, maxX, maxY);
    }

    public static double getRange(double i, double mix, double max) {
        i = Math.max(i, mix);
        i = Math.min(i, max);
        return i;
    }

    public static double GetShortDistance(double lon1, double lat1, double lon2,
                                          double lat2) {
        double ew1, ns1, ew2, ns2;
        double dx, dy, dew;
        double distance;

        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;

        dew = ew1 - ew2;

        if (dew > DEF_PI)
            dew = DEF_2PI - dew;
        else if (dew < -DEF_PI)
            dew = DEF_2PI + dew;
        dx = DEF_R * Math.cos(ns1) * dew;
        dy = DEF_R * (ns1 - ns2);

        distance = Math.sqrt(dx * dx + dy * dy);
        return distance;
    }

    public static double GetLongDistance(double lon1, double lat1, double lon2,
                                         double lat2) {
        double ew1, ns1, ew2, ns2;
        double distance;

        ew1 = lon1 * DEF_PI180;
        ns1 = lat1 * DEF_PI180;
        ew2 = lon2 * DEF_PI180;
        ns2 = lat2 * DEF_PI180;

        distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1)
                * Math.cos(ns2) * Math.cos(ew1 - ew2);

        if (distance > 1.0)
            distance = 1.0;
        else if (distance < -1.0)
            distance = -1.0;

        distance = DEF_R * Math.acos(distance);
        return distance;
    }

    public static Bitmap convertViewToBitmap(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }
}

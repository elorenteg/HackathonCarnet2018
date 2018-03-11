package com.bvisible.carnet.utils;

public class PointUtils {
    public static double pointToLineDistance(Point A, Point B, Point P) {
        double d1 = distance(A.lat, P.lat, A.lng, P.lng);
        double d2 = distance(B.lat, P.lat, B.lng, P.lng);
        return Math.min(d1, d2);
    }

    public static double distance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }
}

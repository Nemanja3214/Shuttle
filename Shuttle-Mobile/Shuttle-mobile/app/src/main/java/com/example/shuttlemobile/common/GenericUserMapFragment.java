package com.example.shuttlemobile.common;

import static com.mapbox.core.constants.Constants.PRECISION_6;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.shuttlemobile.R;
import com.example.shuttlemobile.util.Utils;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.CoordinateBounds;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.animation.Cancelable;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class GenericUserMapFragment extends GenericUserFragment {
    private MapView mapView;
    private MapboxMap mapboxMap;
    private AnnotationPlugin annotationApi;
    private CircleAnnotationManager circleAnnotationManager;
    private PointAnnotationManager pointAnnotationManager;
    private PolylineAnnotationManager polylineAnnotationManager;

    private PolylineAnnotationManager routeAnnotationManager;
    private CircleAnnotationManager routeCircleAnnotationManager;

    private PointAnnotationManager currentPositionAnnotationManager;

    private Bitmap carAvailable;
    private Bitmap carUnavailable;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getLayoutID(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = getActivity().findViewById(getMapViewID());

        initLocationListener();
        initLocationManager();

        initMapAPI();
        initIcons();

        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, style -> onMapLoaded());
    }

    @Override
    public void onResume() {
        super.onResume();
        createLocationRequest();
    }

    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    public abstract String getPublicMapApiToken();

    public abstract void onMapLoaded();

    public abstract int getLayoutID();

    public abstract int getMapViewID();

    private void initLocationListener() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                onNewLocation(location);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }
        };
    }

    public abstract void onNewLocation(Location location);

    private void initLocationManager() {
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        createLocationRequest();
    }

    @SuppressLint("MissingPermission") // Checked inside the splash screen.
    private void createLocationRequest() {
        Log.e("", "createLocationRequest()");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000L, (float) 0.1, locationListener);
    }

    private void initMapAPI() {
        mapboxMap = mapView.getMapboxMap();
        annotationApi = AnnotationPluginImplKt.getAnnotations(mapView);
        circleAnnotationManager = CircleAnnotationManagerKt.createCircleAnnotationManager(annotationApi, new AnnotationConfig());
        pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationApi, new AnnotationConfig());
        polylineAnnotationManager = PolylineAnnotationManagerKt.createPolylineAnnotationManager(annotationApi, new AnnotationConfig());
        routeAnnotationManager = PolylineAnnotationManagerKt.createPolylineAnnotationManager(annotationApi, new AnnotationConfig());
        routeCircleAnnotationManager = CircleAnnotationManagerKt.createCircleAnnotationManager(annotationApi, new AnnotationConfig());
        currentPositionAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationApi, new AnnotationConfig());
    }

    private void initIcons() {
        carAvailable = Utils.getBitmapFromVectorDrawable(getActivity(), R.drawable.car_green);
        carUnavailable = Utils.getBitmapFromVectorDrawable(getActivity(), R.drawable.car_red);
    }

    public final void drawCurrentLocation(Point pos) {
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withPoint(pos)
                .withIconImage(carAvailable)
                ;
        pointAnnotationManager.deleteAll();
        pointAnnotationManager.create(pointAnnotationOptions);
    }


    public final void drawCar(Point pos, boolean available) {
        if (available) {
            drawImage(pos, carAvailable);
        } else {
            drawImage(pos, carUnavailable);
        }
    }

    public final void drawCircle(Point point, Double radius, String hexColor) {
        CircleAnnotationOptions circleAnnotationOptions = new CircleAnnotationOptions()
                .withPoint(point)
                .withCircleRadius(radius)
                .withCircleColor(hexColor)
                .withCircleStrokeWidth(2.0)
                .withCircleStrokeColor("#ffffff")
        ;
        circleAnnotationManager.create(circleAnnotationOptions);
    }

    public final void drawImage(Point point, Bitmap image) {
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(image)
        ;
        pointAnnotationManager.create(pointAnnotationOptions);
    }

    public final void deleteAllPoints() {
        pointAnnotationManager.deleteAll();
    }

    public final void deleteAllRoutes() {
        routeAnnotationManager.deleteAll();
    }

    public final void deleteAllCircles() {
        circleAnnotationManager.deleteAll();
    }

    public final void deleteAllPolylines() {
        polylineAnnotationManager.deleteAll();
    }

    public final void deleteAllRouteCircles() {
        routeCircleAnnotationManager.deleteAll();
    }

    /**
     * Draw a polyline from the specified points with a given color.
     * <br/>
     * If you want to draw a route, use <code>drawPolylineRoute()</code>, as that
     * allows only 1 route to be drawn at any time.
     * @param points List of points that compose the polyline.
     * @param hexColor Color of the polyline.
     */
    public final void drawPolyline(List<Point> points, String hexColor) {
        PolylineAnnotationOptions polylineAnnotationOptions = new PolylineAnnotationOptions()
                .withPoints(points)
                .withLineWidth(6.0)
                .withLineColor(hexColor);
        ;
        polylineAnnotationManager.create(polylineAnnotationOptions);
    }

    /**
     * Draw a route between the specified points with a given color. If another route is already
     * drawn, it will be deleted before a new one is drawn.
     * <br/>
     * Note that only 1 route polyline can be present on the map at any given time,
     * use <code>drawPolyline()</code> to draw an arbitrary polyline.
     * @param points List of points that compose the polyline.
     * @param hexColor Color of the polyline.
     */
    public final void drawPolylineRoute(List<Point> points, String hexColor) {
        removeRoute();

        PolylineAnnotationOptions lineInner = new PolylineAnnotationOptions()
                .withPoints(points)
                .withLineWidth(5.0)
                .withLineColor(hexColor);
        ;
        routeAnnotationManager.create(lineInner);

        PolylineAnnotationOptions lineOuter = new PolylineAnnotationOptions()
                .withPoints(points)
                .withLineWidth(2.0)
                .withLineGapWidth(3.0)
                .withLineColor("#ffffff");
        ;
        routeAnnotationManager.create(lineOuter);

        final Point A = points.get(0);
        final Point B = points.get(points.size() - 1);

        CircleAnnotationOptions circleAnnotationOptions = new CircleAnnotationOptions()
                .withPoint(A)
                .withCircleColor(hexColor)
                .withCircleRadius(5.0)
                .withCircleStrokeWidth(3)
                .withCircleStrokeColor("#ffffff");
        routeCircleAnnotationManager.create(circleAnnotationOptions);

        circleAnnotationOptions.withPoint(B);
        routeCircleAnnotationManager.create(circleAnnotationOptions);
    }

    public final void drawRoute(Point A, Point B, String hexColor) {
        if (getActivity() == null) {
            return;
        }

        final List<Point> points = Arrays.asList(A, B);

        final MapboxDirections client = MapboxDirections.builder()
                .accessToken(getPublicMapApiToken())
                .routeOptions(RouteOptions.builder()
                        .coordinatesList(points)
                        .profile(DirectionsCriteria.PROFILE_DRIVING)
                        .overview(DirectionsCriteria.OVERVIEW_FULL)
                        .build())
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                drawRoute_OnResponse(call, response, hexColor);
                onGetDistance(response.body().routes().get(0).distance());
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                // Failed to call.
            }
        });
    }

    public void onGetDistance(Double distance) {}

    /**
     * Remove the route and its endpoint from the map (if any).
     */
    public final void removeRoute() {
        routeAnnotationManager.deleteAll();
        routeCircleAnnotationManager.deleteAll();
    }

    private Feature drawRoute_OnResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response, String hexColor) {
        if (response.body() == null) {
            // No response.
            return null;
        }

        final List<DirectionsRoute> routes = response.body().routes();

        if (routes.size() == 0) {
            // No routes.
            return null;
        }

        // We only draw the first route since we don't need alternative routes.

        final DirectionsRoute route = routes.get(0);
        final Feature routeFeature = Feature.fromGeometry(LineString.fromPolyline(route.geometry(), PRECISION_6));
        final LineString routeGeometry = (LineString)(routeFeature.geometry());

        final List<Point> routePoints = routeGeometry.coordinates();
        final Point A = routePoints.get(0);
        final Point B = routePoints.get(routePoints.size() - 1);

        drawPolylineRoute(routePoints, hexColor);

        return routeFeature;
    }

    public final void fitViewport(Point A, Point B, long animDurationInMs) {
        // https://stackoverflow.com/questions/69877907/animate-the-mapbox-camera-in-v10
        // https://github.com/mapbox/mapbox-maps-android/issues/776

        // Due to rotation it may not fit always, so we add some padding. 64dp should be enough.

        final double padding_dp = 64.0;
        final double padding_px = Utils.dp2px(getContext(), padding_dp);
        final EdgeInsets padding = new EdgeInsets(padding_px, padding_px, padding_px, padding_px);

        // The points could be anywhere, create a BBox which the viewport will try to fit.

        final double top = Math.min(A.latitude(), B.latitude());
        final double bottom = Math.max(A.latitude(), B.latitude());
        final double left = Math.min(A.longitude(), B.longitude());
        final double right = Math.max(A.longitude(), B.longitude());
        final CoordinateBounds bounds = new CoordinateBounds(Point.fromLngLat(left, bottom), Point.fromLngLat(right, top));

        // Apply transformation.

        final CameraOptions fitOptions = mapboxMap.cameraForCoordinateBounds(bounds, padding, 0.0, 0.0);
        final MapAnimationOptions mapAnimationOptions = new MapAnimationOptions.Builder().duration(animDurationInMs).build();
        final Cancelable cancelable = CameraAnimationsUtils.flyTo(mapboxMap, fitOptions, mapAnimationOptions);

        // Note: use `mapboxMap.setCamera(fitOptions);` to perform the transition without animation.
    }

    public final void lookAtPoint(Point p, double zoom, long animDurationInMs) {
        final CameraOptions lookOptions = new CameraOptions.Builder().center(p).zoom(zoom).build();
        final MapAnimationOptions mapAnimationOptions = new MapAnimationOptions.Builder().duration(animDurationInMs).build();
        final Cancelable cancelable = CameraAnimationsUtils.flyTo(mapboxMap, lookOptions, mapAnimationOptions);
    }
}
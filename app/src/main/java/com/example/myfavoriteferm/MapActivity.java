package com.example.myfavoriteferm;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.geometry.Polyline;

import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.map.SublayerManager;
import com.yandex.mapkit.mapview.MapView;

import java.util.ArrayList;
import java.util.Date;

//ОКНО С РИСОВАНИЕМ НА КАРТЕ

public class MapActivity extends Activity {
    //ЯНДЕКС АПИ
    private final String MAPKIT_API_KEY = "db74e702-41e5-457e-95c5-f654b98aff89";
    private MapView mapView;
    private SublayerManager sublayerManager;
    LocationManager manager;
    TextView textview;

    private LocationManager locationManager;
    StringBuilder sbGPS = new StringBuilder();
    StringBuilder sbNet = new StringBuilder();
    private String login;
    private String coordpoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_map);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.getMap().addInputListener(inputListener);

        sublayerManager = mapView.getMap().getSublayerManager();
        mapObjects = mapView.getMap().getMapObjects();
        mapObjectTemp = mapView.getMap().getMapObjects();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Bundle arguments = getIntent().getExtras();
        login = arguments.get("secretkey").toString();
        coordpoints=login+"+";

    }

    //ВЕРХНЯЯ ПАНЕЛЬ С ВЫБОРОМ: ЛИЧНЫЙ КАБИНЕТ, СОХРАНИТЬ ПОЛЕ
    public void ClickPanel(View v)
    {
        switch (v.getId()) {
            case R.id.Button_lk:
                Intent intent = new Intent(this, Activity_lk.class);
                intent.putExtra("secretkey", login);
                startActivity(intent);
                break;
            case R.id.Button_mapdrawing:
                if(linepoint==null || linepoint.size()<2) {
                    Toast.makeText(this, "Создайте поле", Toast.LENGTH_SHORT).show();
                    break;
                }
                //СЛОЖНАЯ МАХИНАЦИЯ ДЛЯ ХРАНЕНИЯ КООРДИНАТ ТОЧЕК В БД С РЕАЛЬНЫМ ВОССТАНОВЛЕНИЕМ И ПОВТОРНЫМ ИСПОЛЬЗОВАНИЕМ
                for(int i=0;i<linepoint.size();i++)
                    coordpoints+=(linepoint.get(i)).getLatitude()+";"+(linepoint.get(i)).getLongitude()+" ";
                intent = new Intent(this, Activity_savefields.class);
                intent.putExtra("secretkey", coordpoints);
                startActivity(intent);
                linepoint.clear();
                mapObjects.clear();
                mapObjectTemp.clear();
        }
    }

    //ДЛЯ РИСОВАНИЕ
    private MapObjectCollection mapObjects;
    private MapObjectCollection mapObjectTemp;//ВРЕМЕННАЯ КРАСНАЯ ЛИНИЯ, КОТОРАЯ ПРОПАДАЕТ,ЕСЛИ ПОЛЬЗОВАТЕЛЬ ДОРИСОВЫВАЕТ НОВУЮ

    private Point a0 = null, a1 = null, firstpoint = null;
    private InputListener inputListener = new InputListener() {
        //ОПРЕДЕЛЯЕТ ВТОРУЮ И ПОСЛЕДУЮЩИЕ ТОЧКИ
        @Override
        public void onMapTap(@NonNull Map map, @NonNull Point point) {
            a1 = point;
            if (a0 != null) {
                //DrawPolygon();
                //DrawConsistentlyLine();
                DrawPoints(linepoint);
                DrawFalsePolygon(a1);
                //DrawCircle();
            }
        }

        //ДОЛГОЕ НАЖАТИЕ ОПРЕДЕЛЯЕТ ПОЗИЦИЮ ПЕРВОЙ ТОЧКИ
        @Override
        public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
            linepoint = new ArrayList<Point>();
            Toast.makeText(getApplicationContext(), "Начальная точка выбрана. \n Укажите следующую точку", Toast.LENGTH_SHORT).show();
            a0 = point;
            firstpoint = point;
            linepoint.add(a0);
            DrawPoint(a0);
        }
    };

    //РИСОВАТЬ КРУЖОЧЕК
    private void DrawCircle() {
        if (a0 == null || a1 == null)
            return;
        double len = Math.sqrt(Math.pow(Math.abs((a1.getLatitude() - a0.getLatitude())) * 75000, 2) + Math.pow(Math.abs((a1.getLongitude() - a0.getLongitude())) * 75000, 2));
        Circle circle = new Circle(a0, (float) len);
        mapObjects.addCircle(circle, Color.GRAY, 1.f, Color.WHITE);
    }

    //РИСОВАТЬ ОДНУ ТОЧКУ
    private void DrawPoint(Point a) {
        if (a == null)
            return;
        Circle circle = new Circle(a, 40);
        mapObjects.addCircle(circle, Color.RED, 1.f, Color.RED);
    }

    //РИСОВАТЬ ТОЧКИ ИЗ СПИСКА
    private void DrawPoints(ArrayList<Point> arr_points) {
        for (int i = 0; i < arr_points.size(); i++)
            DrawPoint(arr_points.get(i));
    }

    private ArrayList<Point>linepoint;

    //НАРИСОВАТЬ ОДНУ ЛИНИЮ
    private void DrawLine(Point a0, Point a1) {
        if (a0 == null || a1 == null)
            return;
        ArrayList<Point> linepoints = new ArrayList<>();
        linepoints.add(a0);
        linepoints.add(a1);
        PolylineMapObject line = mapObjects.addPolyline(new Polyline(linepoints));
        line.setStrokeColor(Color.GRAY);
        line.setZIndex(2.0f);
    }

    //НАРИСОВАТЬ НЕПРАВИЛЬНЫЙ МНОГОУГОЛЬНИК
    private void DrawFalsePolygon(Point point) {
        mapObjectTemp.clear();
        linepoint.add(point);
        ArrayList<Point> temp = new ArrayList<>();
        temp.add(firstpoint);
        temp.add(linepoint.get(linepoint.size() - 1));
        Polyline lastpolyline = new Polyline(temp);
        PolylineMapObject line_temp = mapObjectTemp.addPolyline(lastpolyline);
        line_temp.setStrokeColor(Color.RED);
        line_temp.setStrokeWidth(1);

        PolylineMapObject line = mapObjects.addPolyline(new Polyline(linepoint));
        line.setStrokeColor(Color.BLACK);
        line.setStrokeWidth(1);
        a0 = a1;
    }

    //РИСОВАНИЕ ЛОМАННОЙ ПРЯМОЙ
    private void DrawConsistentlyLine() {
        DrawLine(a0, a1);
        a0 = a1;
    }

    //РИСОВАНИЕ ПРЯМОУГОЛЬНИКА, ГДЕ ТОЧКИ ОБРАЗУЮТ ДИАГОНАЛЬ
    private void DrawPolygon() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(a0);
        double y = a0.getLongitude() - a1.getLongitude();
        double x = a0.getLatitude() - a1.getLatitude();
        points.add(new Point(a0.getLatitude(), a0.getLongitude() - y));
        points.add(a1);
        points.add(new Point(a0.getLatitude() - x, a0.getLongitude()));
        points.add(a0);
        PolylineMapObject polyline = mapObjects.addPolyline(new Polyline(points));
        polyline.setStrokeColor(Color.BLACK);
        polyline.setZIndex(100.0f);
    }


    //ДЛЯ ОПР МЕСТОПОЛОЖЕНИЯ
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "бест", Toast.LENGTH_SHORT).show();
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 10, 10,
                locationListener);
        Toast.makeText(getApplicationContext(), "GPS1\n" + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER), Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "NETWORK1 \n" + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER), Toast.LENGTH_SHORT).show();
        showLocation(locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER));

    }

    //ДЛЯ ОПР МЕСТОПОЛОЖЕНИЯ
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    //ТОЖЕ ДЛЯ ОПР МЕСТОПОЛОЖЕНИЯ
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS2 \n" + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER), Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "NETWORK2 \n" + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS3 \n" + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER), Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "NETWORK3 \n" + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER), Toast.LENGTH_SHORT).show();
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getApplicationContext(), "чикс1", Toast.LENGTH_SHORT).show();
                //tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                Toast.makeText(getApplicationContext(), "чикс2", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "GPS \n"+formatLocation(location), Toast.LENGTH_SHORT).show();
        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "ТУЕЦЩКЛ \n"+formatLocation(location), Toast.LENGTH_SHORT).show();
        }
    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format("Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT", location.getLatitude(), location.getLongitude(), new Date(location.getTime()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }


}

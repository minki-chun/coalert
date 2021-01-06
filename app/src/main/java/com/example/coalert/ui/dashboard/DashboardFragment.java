package com.example.coalert.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.example.coalert.SharedViewModel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.location.LocationListener;
import com.example.coalert.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.xml.transform.dom.DOMLocator;


public class DashboardFragment extends Fragment implements OnMapReadyCallback {
    Geocoder geocoder;
    TextView maptx;
    public SharedViewModel sharedViewModel;
    Switch mapsw;
    LocationManager manager;
    LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            showCurrentLocation(latitude, longitude);
        }
    };
    private Marker currentMarker = null;
    private GoogleMap mMap;
    private MapView mapView = null;
    private Double lattt;
    private Double lnggg;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mapView = (MapView) root.findViewById(R.id.mapview);
        mapView.getMapAsync(this);
        mapsw = (Switch) root.findViewById(R.id.switch2);
        maptx = (TextView) root.findViewById(R.id.maptextView);

        mapsw.setOnCheckedChangeListener(new nML());
        AutoPermissions.Companion.loadAllPermissions(getActivity(), 100);
        AutoPermissions.Companion.loadAllPermissions(getActivity(), 101);
        return root;
    }

    class nML implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                sharedViewModel.setHere(true);
                manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                startLocationService();
            }
            else{
                sharedViewModel.setHere(false);
                manager = null;
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(mapView!=null){mapView.onCreate(savedInstanceState);}

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        if(sharedViewModel.getHere()){
            mapsw.setChecked(true);
        }
        else {mapsw.setChecked(false);}
        if(sharedViewModel.getSettin()){ sharedViewModel.setHere(false); }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Circle circle;
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                float zoomLevel = mMap.getCameraPosition().zoom;
                sharedViewModel.setLatitude(mMap.getCameraPosition().target.latitude);
                sharedViewModel.setLongitude(mMap.getCameraPosition().target.longitude);
                sharedViewModel.setZoomLevel(zoomLevel);
            }
        });
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        startLocationService();
        int count = 0;
        for(int i=0; i<25; i++){
            CircleOptions circleOptions;
            if(Float.parseFloat(sharedViewModel.getHang(i, 3))<0.8*sharedViewModel.allCount/25){
                circleOptions = new CircleOptions()
                        .center(new LatLng(Float.parseFloat(sharedViewModel.getHang(i, 1)), Float.parseFloat(sharedViewModel.getHang(i, 2))))
                        .radius(35000*Float.parseFloat(sharedViewModel.getHang(i, 3))/sharedViewModel.allCount) // In meters
                        .strokeWidth(0f)
                        .clickable(true)
                        .fillColor(Color.parseColor("#6600ff00"));
                circle = mMap.addCircle(circleOptions);
                circle.setTag(i);
            }
            else if(Float.parseFloat(sharedViewModel.getHang(i, 3))>=0.8*sharedViewModel.allCount/25&&Integer.parseInt(sharedViewModel.getHang(i, 3))<1.2*sharedViewModel.allCount/25){
                circleOptions = new CircleOptions()
                        .center(new LatLng(Float.parseFloat(sharedViewModel.getHang(i, 1)), Float.parseFloat(sharedViewModel.getHang(i, 2))))
                        .radius(35000*Float.parseFloat(sharedViewModel.getHang(i, 3))/sharedViewModel.allCount) // In meters
                        .strokeWidth(0f)
                        .clickable(true)
                        .fillColor(Color.parseColor("#88ffff00"));
                circle = mMap.addCircle(circleOptions);
                circle.setTag(i);

            }
            else{
                circleOptions = new CircleOptions()
                        .center(new LatLng(Float.parseFloat(sharedViewModel.getHang(i, 1)), Float.parseFloat(sharedViewModel.getHang(i, 2))))
                        .radius(35000*Float.parseFloat(sharedViewModel.getHang(i, 3))/sharedViewModel.allCount) // In meters
                        .strokeWidth(0f)
                        .clickable(true)
                        .fillColor(Color.parseColor("#66ff0000"));
                circle = mMap.addCircle(circleOptions);
                circle.setTag(i);
            }
            //Circle circle = mMap.addCircle(circleOptions);
            mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                @Override
                public void onCircleClick(Circle circle) {
                    //maptx.setText(circle.getTag()+" ");
                    maptx.setText(sharedViewModel.getHang(Integer.parseInt(circle.getTag().toString()),4));
                }
            });
        }

        if(sharedViewModel.getHere()){
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lattt, lnggg), sharedViewModel.getZoomLevel()));
        }
        else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sharedViewModel.getLatitude(), sharedViewModel.getLongitude()), sharedViewModel.getZoomLevel()));
        }
    }



    private void showCurrentLocation(Double latitude, Double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);

        //디폴트 위치, Seoul
        String markerTitle = "내위치";
        String markerSnippet = sharedViewModel.getAllAdress()+"";

        if (currentMarker != null) currentMarker.remove();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(curPoint);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);
        if(sharedViewModel.getHere()) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(curPoint, 15);
            sharedViewModel.setLatitude(latitude);
            sharedViewModel.setLongitude(longitude);
            mMap.moveCamera(cameraUpdate);
        }
    }
    //역지오코딩
    public void reverseGeoCoding(double latitude, double longitude){
        List<Address> list = null;
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            list = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("e", "geo-coding failed");
        } catch (IllegalArgumentException illegalArgumentException) {
            maptx.setText("errorlat");
        }
        if (list != null) {
            String cut[] = list.get(0).toString().split(" ");
            sharedViewModel.setGeoLargeloc(cut[1]);
            sharedViewModel.setGeoSmallloc(cut[2]);
            sharedViewModel.setAllAdress(cut[1]+ " " + cut[2]+ " " + cut[3]+ " " + cut[4].split("\"")[0]);
            // cut[0] : Address[addressLines=[0:"대한민국
            // cut[1] : 서울특별시  cut[2] : 송파구  cut[3] : 오금동
            // cut[4] : cut[4] : 41-26"],feature=41-26,admin=null ~~~~
        }
    }

    private void startLocationService() {

        long minTime = 3000;
        float minDistance = 0;
        try {
            int chk1 = ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION);
            int chk2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

            Location location = null;
            if (chk1 == PackageManager.PERMISSION_GRANTED && chk2 == PackageManager.PERMISSION_GRANTED) {
                if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        lattt = location.getLatitude();
                        lnggg = location.getLongitude();
                        showCurrentLocation(lattt, lnggg);
                        reverseGeoCoding(lattt, lnggg);
                    }
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
                }
                else if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        showCurrentLocation(latitude,longitude);
                        reverseGeoCoding(latitude, longitude);
                    }
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
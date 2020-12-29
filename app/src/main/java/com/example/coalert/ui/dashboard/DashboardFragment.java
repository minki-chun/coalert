package com.example.coalert.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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

import java.util.Objects;


public class DashboardFragment extends Fragment implements OnMapReadyCallback {
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

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(sharedViewModel.getLatitude(), sharedViewModel.getLongitude()), sharedViewModel.getZoomLevel()));
    }

    private void showCurrentLocation(Double latitude, Double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);

        //디폴트 위치, Seoul
        String markerTitle = "내위치";
        String markerSnippet = "위치정보가 확인되었습니다.";

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
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        showCurrentLocation(latitude, longitude);;
                    }
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
                }
                else if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        showCurrentLocation(latitude,longitude);
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
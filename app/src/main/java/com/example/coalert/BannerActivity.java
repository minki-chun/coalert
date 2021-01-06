package com.example.coalert;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.TedPermission;
import com.gun0912.tedpermission.PermissionListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.coalert.SharedViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static java.security.AccessController.getContext;

public class BannerActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    public SharedViewModel sharedViewModel;

    private void checkPermissions () {
        if (Build.VERSION.SDK_INT >= 27) { // 마시멜로(안드로이드 6.0) 이상 권한 체크
            TedPermission.with(this)
                    .setRationaleMessage("앱을 이용하기 위해서는 접근 권한이 필요합니다")
                    .setDeniedMessage("앱에서 요구하는 권한설정이 필요합니다...\n [설정] > [권한] 에서 사용으로 활성화해주세요.")
                    .setPermissions(new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    })
                    .check();

        } else {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_banner);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("");
        startLocationService();

        sharedViewModel.setHang(0,0, "강남구"); sharedViewModel.setHang(0,1, "37.4959854"); sharedViewModel.setHang(0,2, "127.0664091"); sharedViewModel.setHang(0,3, "0");
        sharedViewModel.setHang(1,0, "강동구"); sharedViewModel.setHang(1,1, "37.5492077"); sharedViewModel.setHang(1,2, "127.1464824"); sharedViewModel.setHang(1,3, "0");
        sharedViewModel.setHang(2,0, "강북고"); sharedViewModel.setHang(2,1, "37.6469954"); sharedViewModel.setHang(2,2, "127.0147158"); sharedViewModel.setHang(2,3, "0");
        sharedViewModel.setHang(3,0, "강서구"); sharedViewModel.setHang(3,1, "37.5657617"); sharedViewModel.setHang(3,2, "126.8226561"); sharedViewModel.setHang(3,3, "0");
        sharedViewModel.setHang(4,0, "관악구"); sharedViewModel.setHang(4,1, "37.4653993"); sharedViewModel.setHang(4,2, "126.9438071"); sharedViewModel.setHang(4,3, "0");
        sharedViewModel.setHang(5,0, "광진구"); sharedViewModel.setHang(5,1, "37.5481445"); sharedViewModel.setHang(5,2, "127.0857528"); sharedViewModel.setHang(5,3, "0");
        sharedViewModel.setHang(6,0, "구로구"); sharedViewModel.setHang(6,1, "37.4954856"); sharedViewModel.setHang(6,2, "126.858121"); sharedViewModel.setHang(6,3, "0");
        sharedViewModel.setHang(7,0, "금천구"); sharedViewModel.setHang(7,1, "37.4600969"); sharedViewModel.setHang(7,2, "126.9001546"); sharedViewModel.setHang(7,3, "0");
        sharedViewModel.setHang(8,0, "노원구"); sharedViewModel.setHang(8,1, "37.655264"); sharedViewModel.setHang(8,2, "127.0771201"); sharedViewModel.setHang(8,3, "0");
        sharedViewModel.setHang(9,0, "도봉구"); sharedViewModel.setHang(9,1, "37.6658609"); sharedViewModel.setHang(9,2, "127.0317674"); sharedViewModel.setHang(9,3, "0");
        sharedViewModel.setHang(10,0, "동대문구"); sharedViewModel.setHang(10,1, "37.5838012"); sharedViewModel.setHang(10,2, "127.0507003"); sharedViewModel.setHang(10,3, "0");
        sharedViewModel.setHang(11,0, "동작구"); sharedViewModel.setHang(11,1, "37.4965037"); sharedViewModel.setHang(11,2, "126.9443073"); sharedViewModel.setHang(11,3, "0");
        sharedViewModel.setHang(12,0, "마포구"); sharedViewModel.setHang(12,1, "37.5622906"); sharedViewModel.setHang(12,2, "126.9087803"); sharedViewModel.setHang(12,3, "0");
        sharedViewModel.setHang(13,0, "서대문구"); sharedViewModel.setHang(13,1, "37.5820369"); sharedViewModel.setHang(13,2, "126.9356665"); sharedViewModel.setHang(13,3, "0");
        sharedViewModel.setHang(14,0, "서초구"); sharedViewModel.setHang(14,1, "37.4769528"); sharedViewModel.setHang(14,2, "127.0378103"); sharedViewModel.setHang(14,3, "0");
        sharedViewModel.setHang(15,0, "성동구"); sharedViewModel.setHang(15,1, "37.5506753"); sharedViewModel.setHang(15,2, "127.0409622"); sharedViewModel.setHang(15,3, "0");
        sharedViewModel.setHang(16,0, "성북구"); sharedViewModel.setHang(16,1, "37.606991"); sharedViewModel.setHang(16,2, "127.0232185"); sharedViewModel.setHang(16,3, "0");
        sharedViewModel.setHang(17,0, "송파구"); sharedViewModel.setHang(17,1, "37.5048534"); sharedViewModel.setHang(17,2, "127.1144822"); sharedViewModel.setHang(17,3, "0");
        sharedViewModel.setHang(18,0, "양천구"); sharedViewModel.setHang(18,1, "37.5270616"); sharedViewModel.setHang(18,2, "126.8561534"); sharedViewModel.setHang(18,3, "0");
        sharedViewModel.setHang(19,0, "영등포구"); sharedViewModel.setHang(19,1, "37.520641"); sharedViewModel.setHang(19,2, "126.9139242"); sharedViewModel.setHang(19,3, "0");
        sharedViewModel.setHang(20,0, "용산구"); sharedViewModel.setHang(20,1, "37.5311008"); sharedViewModel.setHang(20,2, "126.9810742"); sharedViewModel.setHang(20,3, "0");
        sharedViewModel.setHang(21,0, "은평구"); sharedViewModel.setHang(21,1, "37.6176125"); sharedViewModel.setHang(21,2, "126.9227004"); sharedViewModel.setHang(21,3, "0");
        sharedViewModel.setHang(22,0, "종로구"); sharedViewModel.setHang(22,1, "37.5990998"); sharedViewModel.setHang(22,2, "126.9861493"); sharedViewModel.setHang(22,3, "0");
        sharedViewModel.setHang(23,0, "중구"); sharedViewModel.setHang(23,1, "37.5579452"); sharedViewModel.setHang(23,2, "126.9941904"); sharedViewModel.setHang(23,3, "0");
        sharedViewModel.setHang(24,0, "중랑구"); sharedViewModel.setHang(24,1, "37.5953795"); sharedViewModel.setHang(24,2, "127.0939669"); sharedViewModel.setHang(24,3, "0");


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    messageList messageList = snapshot.getValue(com.example.coalert.messageList.class);
                    messageList.setMessage(messageList.getMessage().split("-송출지역")[0]);
                    for(int i=0; i<25; i++){
                        if(messageList.getMessage().startsWith("["+sharedViewModel.getHang(i, 0))){
                            sharedViewModel.setHang(i, 3, String.valueOf(Integer.valueOf(sharedViewModel.getHang(i, 3))+1));
                            sharedViewModel.setHang(i, 4, messageList.getMessage().split("-송출지역")[0]);
                            sharedViewModel.allCount = sharedViewModel.allCount + 1;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("home", String.valueOf(databaseError.toException()));
            }
        });

        //checkPermissions();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(navView, navController);
    }

    //역지오코딩
    public void reverseGeoCoding(double latitude, double longitude){
        Geocoder geocoder;
        List<Address> list = null;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            list = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("e", "geo-coding failed");
        } catch (IllegalArgumentException illegalArgumentException) {
            Log.e("e", "geo-coding failed");
        }
        if (list != null) {
            String cut[] = list.get(0).toString().split(" ");
            sharedViewModel.setGeoLargeloc(cut[1]);
            sharedViewModel.setGeoSmallloc(cut[2]);
            // cut[0] : Address[addressLines=[0:"대한민국
            // cut[1] : 서울특별시  cut[2] : 송파구  cut[3] : 오금동
            // cut[4] : cut[4] : 41-26"],feature=41-26,admin=null ~~~~
        }
    }

    LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            reverseGeoCoding(latitude, longitude);
        }
    };

    private void startLocationService() {
        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        long minTime = 3000;
        float minDistance = 0;
        try {
            int chk1 = ContextCompat.checkSelfPermission(Objects.requireNonNull(this), Manifest.permission.ACCESS_FINE_LOCATION);
            int chk2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

            Location location = null;
            if (chk1 == PackageManager.PERMISSION_GRANTED && chk2 == PackageManager.PERMISSION_GRANTED) {
                if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        reverseGeoCoding(latitude, longitude);
                    }
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
                }
                else if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        reverseGeoCoding(latitude, longitude);
                    }
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
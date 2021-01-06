package com.example.coalert.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.location.Geocoder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coalert.CustomAdapter;
import com.example.coalert.R;
import com.example.coalert.SharedViewModel;
import com.example.coalert.messageList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class HomeFragment extends Fragment {

    Geocoder geocoder;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<messageList> arrayList;
    private CustomAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private HomeViewModel homeViewModel;
    String sample_prev;
    private int current_status;
    Switch sw;
    public SharedViewModel sharedViewModel;
    LocationManager manager;

    LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            reverseGeoCoding(latitude, longitude);
        }
    };

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
            Log.e("e", "geo-coding failed");
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

    class nSL implements CompoundButton.OnCheckedChangeListener{ //현재지역 스위치 눌렀을 때
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                sharedViewModel.setHere(true);
                //startLocationService();
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        arrayList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            messageList messageList = snapshot.getValue(com.example.coalert.messageList.class);
                            if(messageList.getMessage().startsWith("["+sharedViewModel.getGeoSmallloc())) {
                                messageList.setMessage(messageList.getMessage().split("-송출지역")[0]);
                                arrayList.add(messageList);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(adapter.getItemCount()-1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("home", String.valueOf(databaseError.toException()));
                    }
                });}
            else{
                sharedViewModel.setHere(false);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        arrayList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            messageList messageList = snapshot.getValue(com.example.coalert.messageList.class);
                            messageList.setMessage(messageList.getMessage().split("-송출지역")[0]);
                            arrayList.add(messageList);
                        }
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(adapter.getItemCount()-1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("home", String.valueOf(databaseError.toException()));
                    }
                });}
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(sharedViewModel.getHere()){sw.setChecked(true);}
        else {sw.setChecked(false);}
        if(sharedViewModel.getSettin()&&!sharedViewModel.getHere()){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    arrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        messageList messageList = snapshot.getValue(com.example.coalert.messageList.class);
                        //if(messageList.getMessage().contains(sharedViewModel.getSmallloc())&&today(messageList.getDate().split(" ")[0])<=(long)sharedViewModel.getTime()) {
                        if(messageList.getMessage().startsWith("["+sharedViewModel.getSmallloc())&&today(messageList.getDate().split(" ")[0])<=(long)sharedViewModel.getTime()) {
                            messageList.setMessage(messageList.getMessage().split("-송출지역")[0]);
                            arrayList.add(messageList);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("home", String.valueOf(databaseError.toException()));
                }
            });
        }
    }
    /*
    private void startLocationService() {
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView prev = (TextView) root.findViewById(R.id.hometextView);
        sw = root.findViewById(R.id.switch1);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        current_status = 0;
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("");
        sw.setOnCheckedChangeListener(new nSL());

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        //startLocationService();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    messageList messageList = snapshot.getValue(com.example.coalert.messageList.class);
                    messageList.setMessage(messageList.getMessage().split("-송출지역")[0]);
                    arrayList.add(messageList);
                    if(messageList.getMessage().contains("중대본")) {
                        sample_prev = messageList.getMessage().substring(0,40);
                    }
                }
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(adapter.getItemCount()-1);
                prev.setText(sample_prev + "  (see more..)");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("home", String.valueOf(databaseError.toException()));
            }
        });
        adapter = new CustomAdapter(arrayList, getContext());

        recyclerView.setAdapter(adapter);
        prev.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(current_status == 0){
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                messageList messageList = snapshot.getValue(com.example.coalert.messageList.class);
                                if(messageList.getMessage().contains("중대본")) {
                                    messageList.setMessage(messageList.getMessage().split("-송출지역")[0]);
                                    arrayList.add(messageList);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(adapter.getItemCount()-1);
                            prev.setText("back to list");
                            current_status = 1;
                            sw.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("home", String.valueOf(databaseError.toException()));
                        }
                    });
                }
                else{
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            arrayList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                messageList messageList = snapshot.getValue(com.example.coalert.messageList.class);
                                messageList.setMessage(messageList.getMessage().split("-송출지역")[0]);
                                arrayList.add(messageList);
                            }
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(adapter.getItemCount()-1);
                            prev.setText(sample_prev + "  (sea more..)");
                            current_status = 0;
                            sw.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("home", String.valueOf(databaseError.toException()));
                        }
                    });
                }
            }
        });

        return root;

        //final TextView textView = root.findViewById(R.id.text_home);
        //homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
        //    @Override
        //    public void onChanged(@Nullable String s) {
        //        //textView.setText(s);
        //    }
        //});
        //return root;
    }
    public long today(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
            Date firstDate = format.parse(date);
            Date nowTime = Calendar.getInstance().getTime();
            String secondTime = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault()).format(nowTime);
            Date secondDate = format.parse(secondTime);
            long calDate = firstDate.getTime() - secondDate.getTime();
            long calDateDays = calDate / (24*60*60*1000);
            calDateDays = Math.abs(calDateDays);
            return calDateDays;
        }
        catch(ParseException e){
            Log.e("e","error");
        }
        return 0;
    }
}
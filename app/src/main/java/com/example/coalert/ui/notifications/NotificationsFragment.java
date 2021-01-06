package com.example.coalert.ui.notifications;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;


import com.example.coalert.BannerActivity;
import com.example.coalert.R;
import com.example.coalert.SharedViewModel;
import com.example.coalert.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NotificationsFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private NotificationsViewModel notificationsViewModel;
    Button oneDay;
    Button threeDay;
    Button sevenDay;
    Button oneFourDay;
    Button setOk;
    Spinner spinner1;
    Spinner spinner2;
    private String lar = "";
    private String sma = "";
    TextView textView;
    BottomNavigationView bn;
    FragmentTransaction transaction;
    Fragment newFragment;
    TextView notiTe;
    Geocoder geocoder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        spinner1 = root.findViewById(R.id.spinner1);
        spinner2 = root.findViewById(R.id.spinner2);

        oneDay = root.findViewById(R.id.oneday);
        threeDay = root.findViewById(R.id.threeday);
        sevenDay = root.findViewById(R.id.sevenday);
        oneFourDay = root.findViewById(R.id.onefourday);
        setOk = root.findViewById(R.id.setOk);
        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        newFragment = new HomeFragment();
        transaction.replace(container.getId(), newFragment );
        transaction.addToBackStack(null);
        notiTe = root.findViewById(R.id.noti);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lar = (spinner1.getItemAtPosition(i).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sma = (spinner2.getItemAtPosition(i).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        oneDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneDay.setSelected(true);
                threeDay.setSelected(false);
                sevenDay.setSelected(false);
                oneFourDay.setSelected(false);
            }
        });

        threeDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneDay.setSelected(false);
                threeDay.setSelected(true);
                sevenDay.setSelected(false);
                oneFourDay.setSelected(false);
            }
        });

        sevenDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneDay.setSelected(false);
                threeDay.setSelected(false);
                sevenDay.setSelected(true);
                oneFourDay.setSelected(false);
            }
        });

        oneFourDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oneDay.setSelected(false);
                threeDay.setSelected(false);
                sevenDay.setSelected(false);
                oneFourDay.setSelected(true);
            }
        });

        setOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Address> list = null;
                geocoder = new Geocoder(getContext(), Locale.getDefault());
                try{
                    list = geocoder.getFromLocationName(lar + " " + sma, 1);
                }
                catch (IOException e){
                    Log.e("e","error");
                }

                if(oneDay.isSelected()&&list!=null){
                    if((lar != "")&&(sma != "")){
                        sharedViewModel.setTime(1);
                        sharedViewModel.setLargeloc(lar);
                        sharedViewModel.setSmallloc(sma);
                        sharedViewModel.setSettin(true);
                        sharedViewModel.setHere(false);
                        sharedViewModel.setLatitude(list.get(0).getLatitude());
                        sharedViewModel.setLongitude(list.get(0).getLongitude());
                        transaction.commit();
                    }
                }
                else if(threeDay.isSelected()&&list!=null){
                    if((lar != "")&&(sma != "")){
                        sharedViewModel.setTime(3);
                        sharedViewModel.setLargeloc(lar);
                        sharedViewModel.setSmallloc(sma);
                        sharedViewModel.setSettin(true);
                        sharedViewModel.setHere(false);
                        sharedViewModel.setLatitude(list.get(0).getLatitude());
                        sharedViewModel.setLongitude(list.get(0).getLongitude());
                        transaction.commit();
                    }
                }
                else if(sevenDay.isSelected()&&list!=null){
                    if((lar != "")&&(sma != "")){
                        sharedViewModel.setTime(7);
                        sharedViewModel.setLargeloc(lar);
                        sharedViewModel.setSmallloc(sma);
                        sharedViewModel.setSettin(true);
                        sharedViewModel.setHere(false);
                        sharedViewModel.setLatitude(list.get(0).getLatitude());
                        sharedViewModel.setLongitude(list.get(0).getLongitude());
                        transaction.commit();
                    }
                }
                else if(oneFourDay.isSelected()&&list!=null){
                    if((lar != "")&&(sma != "")){
                        sharedViewModel.setTime(14);
                        sharedViewModel.setLargeloc(lar);
                        sharedViewModel.setSmallloc(sma);
                        sharedViewModel.setSettin(true);
                        sharedViewModel.setHere(false);
                        sharedViewModel.setLatitude(list.get(0).getLatitude());
                        sharedViewModel.setLongitude(list.get(0).getLongitude());
                        transaction.commit();
                    }
                }
                else{
                    notiTe.setText("Please select both 'Area' and 'Period'");
                }

                //textView.setText(sharedViewModel.getTime() + sharedViewModel.getLargeloc() + sharedViewModel.getSmallloc() + sharedViewModel.getSettin() + sharedViewModel. getHere());
                //textView.setText(lar+"  "+sma);
                //oneDay.setSelected(false);
                //threeDay.setSelected(false);
                //sevenDay.setSelected(false);
                //oneFourDay.setSelected(false);
                //sharedViewModel.setHere(false);
                //setOk.setPressed(true);

                //transaction.detach(newFragment);
            }
        });


        return root;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }
}
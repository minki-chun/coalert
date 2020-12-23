package com.example.coalert.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment {

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

    class nSL implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                sharedViewModel.setHere(true);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        arrayList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            messageList messageList = snapshot.getValue(com.example.coalert.messageList.class);
                            if(messageList.getMessage().contains("동대문구")) {
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

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        if(sharedViewModel.getHere()){sw.setChecked(true);}
        else {sw.setChecked(false);}
        if(sharedViewModel.getSettin()){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    arrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        messageList messageList = snapshot.getValue(com.example.coalert.messageList.class);
                        if(messageList.getMessage().contains(sharedViewModel.getSmallloc())&&today(messageList.getDate().split(" ")[0])<=(long)sharedViewModel.getTime()) {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView prev = (TextView) root.findViewById(R.id.hometextView);
        sw = (Switch) root.findViewById(R.id.switch1);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();
        current_status = 0;
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("");
        sw.setOnCheckedChangeListener(new nSL());


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
                prev.setText(sample_prev + "  (더보기..)");
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
                            prev.setText("전체 목록으로 돌아가기");
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
                            prev.setText(sample_prev + "  (더보기..)");
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
package com.hackathon.tracky;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Graph_Item extends android.support.v4.app.Fragment {

    BarChart barChart;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.graph_item, container, false);
        try {
            final Context context = getActivity().getApplicationContext();
            barChart = (BarChart) view.findViewById(R.id.bargraph);
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String uid = firebaseUser.getUid();//Fetch from firebase Auth
            DatabaseReference ref = database.getReference().child("performance").child(uid);
            ValueEventListener listener = new ValueEventListener() {
                List<String> list = new ArrayList<>();
                List<Performance> p = new ArrayList<>();
                ArrayList<BarEntry> barEntries = new ArrayList<>();
                int count = 0, i;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            list.add(d.getKey());
                            p.add(d.getValue(Performance.class));
                        }
                        count = list.size();

                        for (i = 0; i < count; i++) {
                            Performance first = new Performance(0.0);
                            first = p.get(i);
                            int x = (int) Math.round(first.perform);
                            barEntries.add(new BarEntry(x, i));
                        }
                        BarDataSet barDataSet = new BarDataSet(barEntries, "Display");
                        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
                        BarData theData = new BarData(list, barDataSet);
                        barChart.setData(theData);
                        barChart.setTouchEnabled(true);
                        barChart.setDragEnabled(true);
                        barChart.setDragEnabled(true);

                    } catch (Exception e) {

                    }

                }

                public void onCancelled(DatabaseError databaseError) {

                }

            };
            ref.addValueEventListener(listener);

        }catch (Exception ex){

        }
        return view;
    }


}

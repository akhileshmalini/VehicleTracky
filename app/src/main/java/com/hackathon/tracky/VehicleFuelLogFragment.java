package com.hackathon.tracky;


import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class VehicleFuelLogFragment extends android.support.v4.app.Fragment {
    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference,mPostReference2;
    FirebaseUser firebaseUser;
    String vehicleNumber;
     int odoDistanceVal;
        DatabaseReference mFuellogReference;
    //Inflate Layout

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        final View view =  inflater.inflate(R.layout.fuel_list, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final VehicleDetailActivity activity =(VehicleDetailActivity) getActivity();
        vehicleNumber= activity.getVehicleNumber();
        final ProgressBar pgbar =view.findViewById(R.id.progressBar);

        //Declare Views
        RecyclerView recyclerView =view.findViewById(R.id.recyclerview);

        odoDistanceVal=0;
        firebaseUser.getUid();

        final List<Newrefill> fuellist = new ArrayList<Newrefill>();


        mFuellogReference = mDatabase.child("users").child(firebaseUser.getUid()).child("vehicles").child(vehicleNumber).child("fuelstops");
        final FuelAdapter adapter = new FuelAdapter(fuellist,getActivity(),mFuellogReference );

        ValueEventListener fuelloglistener =new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fuellist.clear();
                for(DataSnapshot dsnap : dataSnapshot.getChildren()){
                    double dis =dsnap.child("distance").getValue(Double.class);
                    double petrolb =dsnap.child("petrol_before_refill").getValue(Double.class);
                    double petrola =dsnap.child("petrol_after_refill").getValue(Double.class);
                    double lat =dsnap.child("latitude").getValue(Double.class);
                    double lon=dsnap.child("longitude").getValue(Double.class);


                    fuellist.add(new Newrefill(petrolb,petrola,dis,lat,lon,dsnap.getKey()));
                }
                adapter.notifyDataSetChanged();
                pgbar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mFuellogReference.addValueEventListener(fuelloglistener);


        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);





        return view;
    }


}

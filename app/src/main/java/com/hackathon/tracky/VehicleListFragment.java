/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hackathon.tracky;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class VehicleListFragment extends Fragment {
    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference,mPostReference2;

    ImageView img;
    String uid;
    TextView txt2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Fetch Firebase Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        uid =firebaseUser.getUid();//Fetch from firebase Auth
        mDatabase.child("users").child(uid).child("removeVehiclesFlag").setValue(false);

        //Inflate Layout
        final View view =  inflater.inflate(R.layout.fragment_vehicle_list, container, false);

        //Declare Views
        RecyclerView recyclerView =view.findViewById(R.id.recyclerview);
        final ProgressBar pgbar =view.findViewById(R.id.progressBar);


         img =view.findViewById(R.id.imageView2);
            txt2=view.findViewById(R.id.textView2);
        Glide   .with(getActivity().getApplicationContext())
                .load(R.drawable.wheels)
                .apply(new RequestOptions()

                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .fitCenter()
                ).into(img);
        img.setVisibility(View.INVISIBLE);
        txt2.setVisibility(View.INVISIBLE);

        //Fetch Data from Firebase
        final ArrayList<Vehicle> list = new ArrayList<>();
        final PersonalVehicleAdapter adapter = new PersonalVehicleAdapter(list,getActivity());
        mPostReference = mDatabase.child("users").child(uid).child("vehicles");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Vehicle vehicle = postSnapshot.getValue(Vehicle.class);
                    list.add(vehicle);
                }

                if(list.size()==0){
                    mDatabase.child("users").child(uid).child("removeVehiclesFlag").setValue(false);
                    img.setVisibility(View.VISIBLE);
                    txt2.setVisibility(View.VISIBLE);


                }
                adapter.notifyDataSetChanged();
                pgbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mPostReference.addValueEventListener(postListener);

        mPostReference2 = mDatabase.child("users").child(uid).child("removeVehiclesFlag");
        ValueEventListener postListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean val =dataSnapshot.getValue(Boolean.class);
                final Snackbar snackbar= Snackbar.make(view, "Remove Vehicles", Snackbar.LENGTH_INDEFINITE);

                   if (val) {
                       snackbar.setAction("Dismiss", new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {

                                       mDatabase.child("users").child(uid).child("removeVehiclesFlag").setValue(false);
                                       snackbar.dismiss();

                                   }});
                               snackbar.show();
                   }
        }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mPostReference2.addValueEventListener(postListener2);




        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }


}

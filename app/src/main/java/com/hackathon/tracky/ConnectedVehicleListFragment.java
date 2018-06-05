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
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConnectedVehicleListFragment extends Fragment {
    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Fetch Firebase Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String uid ="uid1";//Fetch from firebase Auth

        //Inflate Layout
        final View view =  inflater.inflate(R.layout.fragment_vehicle_list, container, false);

        //Declare Views
        RecyclerView recyclerView =view.findViewById(R.id.recyclerview);
        final ProgressBar pgbar =view.findViewById(R.id.progressBar);

        //Fetch Data from Firebase
        final ArrayList<Vehicle> list = new ArrayList<>();
        final PersonalVehicleAdapter adapter = new PersonalVehicleAdapter(list,getActivity());
        mPostReference = mDatabase.child("users").child(uid).child("vehicles");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Vehicle vehicle = postSnapshot.getValue(Vehicle.class);
                    list.add(vehicle);
                }
                adapter.notifyDataSetChanged();
                pgbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mPostReference.addListenerForSingleValueEvent(postListener);

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);

        return view;
    }

}

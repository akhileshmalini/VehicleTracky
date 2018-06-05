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

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;

import static com.google.android.gms.location.places.Place.TYPE_GAS_STATION;

public class VehicleDetailActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference, mVehicleRef;
    String vehicleName, vehicleType, vehicleNumber;
    Location currentLocation;
    String uid;
    String address;
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    double currentlat, currentlong;
    private static DecimalFormat df2 = new DecimalFormat(".######");
    double odometer;
    DatabaseReference odoRef;
    ValueEventListener post;
    Dialog rankDialog;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        odoRef.removeEventListener(post);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        NestedScrollView scrollView = (NestedScrollView) findViewById(R.id.nest_scrollview);
        scrollView.setFillViewport(true);

       //Fetch Data From Intent
        Intent intent = getIntent();
        vehicleName = intent.getStringExtra("VehicleName");
        vehicleType = intent.getStringExtra("VehicleType");
        vehicleNumber = intent.getStringExtra("VehicleNumber");


        //Get Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Database Auth Details
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();//Fetch from firebase Auth

        GeoDataClient mGeoDataClient;
        odoRef=mDatabase.child("users").child(firebaseUser.getUid()).child("vehicles").child(vehicleNumber).child("latestlocation");


        //Dialog to Add FuelLog
        rankDialog = new Dialog(VehicleDetailActivity.this, R.style.MyAlertDialogStyle);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);



         post =new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    odometer = dataSnapshot.child("odometer").getValue(Double.class);
                }catch (Exception e){
                    odometer=0;
                    mDatabase.child("users").child(uid).child("vehicles").child(vehicleNumber).child("latestlocation").child("odometer").setValue(0.0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        odoRef.addValueEventListener(post);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            showFuelLogDialog();

            }
        });


        //Code For Detecting CurrentLocation To See if Petrol Bunks are Nearby
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }
        //Fetch Current Place
        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                float  mostLikely= 0.0f ;
                Place mostlikelyplace=null;
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    for(int a : placeLikelihood.getPlace().getPlaceTypes()){
                        if (a==TYPE_GAS_STATION){
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                                showFuelLogDialog();
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };
                            AlertDialog.Builder builder = new AlertDialog.Builder(VehicleDetailActivity.this);
                            builder.setMessage("You seem to be in the vicinity of a Petrol Station, Do you wish to add a Fuel log?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        }
                    }
                    if(placeLikelihood.getLikelihood()>mostLikely){
                        mostLikely=placeLikelihood.getLikelihood();
                        mostlikelyplace=placeLikelihood.getPlace();
                    }
                }
                currentlat=mostlikelyplace.getLatLng().latitude;
                currentlong=mostlikelyplace.getLatLng().longitude;
                currentLocation=new Location("");
                currentLocation.setLatitude(currentlat);
                currentLocation.setLongitude(currentlong);
                likelyPlaces.release();
            }
        });




        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(vehicleName);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        loadBackdrop();





    }

    private void loadBackdrop() {
        final ImageView imageView = findViewById(R.id.backdrop);
        if (vehicleType.equals("bike")) {

            Glide.with(this).load(R.drawable.davidson).apply(RequestOptions.centerCropTransform()).into(imageView);
        } else {
            Glide.with(this).load(R.drawable.aventedor).apply(RequestOptions.centerCropTransform()).into(imageView);
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_actions, menu);
        return true;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public Location getLocation() {
        return currentLocation;
    }

    public Double getOdometer() {
        return odometer;
    }

    private void setupViewPager(ViewPager viewPager) {
        MainActivity.Adapter adapter = new MainActivity.Adapter(getSupportFragmentManager());


        adapter.addFragment(new VehicleHomeFragment(), "Details");
        adapter.addFragment(new VehicleFuelLogFragment(), "Fuel Logs");
        adapter.addFragment(new Graph_Item(), "Graph");
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(VehicleDetailActivity.this, EditVehicleActivity.class);
                intent.putExtra("VehicleName", vehicleName);
                intent.putExtra("VehicleType", vehicleType);
                intent.putExtra("VehicleNumber", vehicleNumber);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }





        void mileage(){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference().child("users").child(uid).child("vehicles").child(getVehicleNumber()).child("fuelstops");
            ValueEventListener listener =new ValueEventListener()
            {
                List<refill> list =new ArrayList<>();
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot d : dataSnapshot.getChildren())
                    {
                        double dis =d.child("distance").getValue(Double.class);
                        double petrolb =d.child("petrol_before_refill").getValue(Double.class);
                        double petrola =d.child("petrol_after_refill").getValue(Double.class);
                        list.add(new refill(dis,petrolb,petrola));
                    }
                    int count = list.size();
                    refill last = new refill();
                    refill secondlast = new refill();
                    last = list.get(count-1);
                    secondlast = list.get(count-2);
                    double distance = last.distance - secondlast.distance;
                    double mileage_cal = distance / (secondlast.petrol_after_refuel - last.petrol_before_refuel);
                    create(getVehicleNumber(),mileage_cal);
                }
                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            };
            ref.addListenerForSingleValueEvent(listener);


        }


    void create(String vehicle, double mileage)
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("mileage").child(uid).child(vehicle);
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        ref.child(ts).setValue(new mileage(mileage));
    }


        void showFuelLogDialog(){
            mPostReference = mDatabase.child("users").child(uid).child("vehicles").child(vehicleNumber).child("fuelstops");
            rankDialog.setContentView(R.layout.fuellog_dialog);
            rankDialog.setCancelable(true);
            final EditText fbr, far;
            TextView latlong = rankDialog.findViewById(R.id.txtLatLong);
            fbr = rankDialog.findViewById(R.id.editFuelBefore);
            far = rankDialog.findViewById(R.id.editFuelAfter);
            if (currentLocation == null) {
                latlong.setText("Location not Found");
            } else {
                latlong.setText("Lat. " + df2.format(currentLocation.getLatitude()) + " Lon. " + df2.format(currentLocation.getLongitude()));
            }
            Button updateButton = (Button) rankDialog.findViewById(R.id.btnAddFuel);
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String b,a;

                    try {
                        Double fuelbefor, fuelafter;
                        fuelbefor = Double.parseDouble(fbr.getText().toString());
                        fuelafter = Double.parseDouble(far.getText().toString());
                        if(!(fuelbefor>fuelafter)) {
                            if(fuelbefor<90 && fuelafter<90 ){
                                Map fuel = new HashMap<String, Double>();
                                fuel.put("petrol_before_refill", fuelbefor);
                                fuel.put("petrol_after_refill", fuelafter);
                                fuel.put("distance", odometer);

                                fuel.put("latitude", currentLocation.getLatitude());
                                fuel.put("longitude", currentLocation.getLongitude());
                                Long tsLong = System.currentTimeMillis() / 1000;
                                String ts = tsLong.toString();

                                mPostReference.child(ts).setValue(fuel);

                                mPostReference.child(ts).setValue(fuel);

                                ValueEventListener val = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.getChildrenCount()>1){
                                            mileage();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                };
                                mPostReference.addListenerForSingleValueEvent(val);



                                Toast.makeText(getApplicationContext(), "Sucessfully Added Fuel Log", Toast.LENGTH_SHORT).show();
                                rankDialog.dismiss();

                            }else{
                                Toast.makeText(getApplicationContext(), "Fuel ExceedsCapacity", Toast.LENGTH_SHORT).show();

                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"How did you end up with lesser petrol than you started with?",Toast.LENGTH_LONG).show();

                        }
                    }catch(Exception e){
                        Toast.makeText(getApplicationContext(),"Fields Left Empty",Toast.LENGTH_SHORT).show();

                    }
                }
            });
            rankDialog.show();


        }








}

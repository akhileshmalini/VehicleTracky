package com.hackathon.tracky;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import xyz.pinaki.android.wheelticker.Odometer;
import xyz.pinaki.android.wheelticker.OdometerAdapter;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class VehicleHomeFragment extends android.support.v4.app.Fragment {
    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference, mPostReference2;
    FirebaseUser firebaseUser;
    String vehicleNumber, vehicleType, vehicleName;
    String expectedMileage;
    TextView textView, textView2, textView3;
    TextView textexpected, textActual;
    RelativeLayout r1;
    Double lastMileage, averageMileage;
    Double odometer;
    boolean go;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        go=false;
        final View view = inflater.inflate(R.layout.vehicle_home_fragment, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final VehicleDetailActivity activity = (VehicleDetailActivity) getActivity();
        vehicleNumber = activity.getVehicleNumber();
        vehicleType = activity.getVehicleType();
        vehicleName = activity.getVehicleName();
        textView = (TextView) view.findViewById(R.id.txtDerived);
        textView2 = (TextView) view.findViewById(R.id.textView12);
        textView3 = (TextView) view.findViewById(R.id.txtAverage);
        r1 = (RelativeLayout) view.findViewById(R.id.bgColor);


        final Odometer frequncyCounter = (Odometer) view.findViewById(R.id.frequency_counter);
        final RandomAdapter randomAdapter = new RandomAdapter();

        textexpected = view.findViewById(R.id.txtExpected);
        textActual = view.findViewById(R.id.txtDerived);

        textexpected.setText("");
        ValueEventListener post = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                frequncyCounter.setAdapter(randomAdapter);
                double num = 0;
                try {
                    num = dataSnapshot.child("odometer").getValue(Double.class);
                } catch (Exception e) {
                }
                int a = (int) Math.round(num);
                randomAdapter.setValue(a);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };

        mDatabase.child("users").child(firebaseUser.getUid()).child("vehicles").child(vehicleNumber).child("latestlocation").addValueEventListener(post);
        DatabaseReference mVehicleRef = mDatabase.child("vehicles").child(vehicleType);
        try {
            ValueEventListener VehiclevalueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        String name = d.child("vehicleName").getValue(String.class);
                        if (name.equals(vehicleName)) {
                            expectedMileage = d.child("vehicleMileage").getValue(String.class);
                            textexpected.setText(expectedMileage + "kmpl");
                            go=true;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mVehicleRef.addListenerForSingleValueEvent(VehiclevalueEventListener);
        } catch (Exception e) {

        }


        DatabaseReference mFuellogReference = mDatabase.child("users").child(firebaseUser.getUid()).child("vehicles").child(vehicleNumber).child("fuelstops");
        ValueEventListener fuelloglistener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() < 1) {
                    mDatabase.child("mileage").child(firebaseUser.getUid()).child(activity.getVehicleNumber()).removeValue();
                    mDatabase.child("performance").child(firebaseUser.getUid()).child(activity.getVehicleNumber()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        mFuellogReference.addValueEventListener(fuelloglistener);


        DatabaseReference mileageRef = mDatabase.child("mileage").child(firebaseUser.getUid()).child(activity.getVehicleNumber());
        try {
            ValueEventListener listener = new ValueEventListener() {
                List<mileage> mlist = new ArrayList<>();
                double mileage_cal = 0.0;
                int count = 0, i;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        mlist.add(d.getValue(mileage.class));
                    }
                    if (mlist.size() > 1) {
                        count = mlist.size();
                        mileage last = new mileage(0.0);
                        last = mlist.get(count - 1);
                        for (mileage m : mlist) {
                            mileage_cal += m.mileage;
                        }
                        lastMileage = last.mileage;
                        averageMileage = mileage_cal/count;
                        DatabaseReference ref2 = mDatabase.child("performance").child(firebaseUser.getUid()).child(activity.getVehicleNumber());
                        ref2.setValue(new Performance(averageMileage));
                        setPerformance();
                    } else if (mlist.size() == 1) {
                        mileage last = new mileage(0.0);
                        last = mlist.get(0);
                        lastMileage = last.mileage;
                        averageMileage = lastMileage;
                        DatabaseReference ref2 = mDatabase.child("performance").child(firebaseUser.getUid()).child(activity.getVehicleNumber());
                        ref2.setValue(new Performance(averageMileage));
                        setPerformance();
                    } else {
                        textView.setText("N/A");
                        textView2.setText("Need Minimum 2 Fuel Log Entries");
                        textView3.setText("N/A");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mileageRef.addValueEventListener(listener);
        } catch (Exception e) {
        }

        return view;
    }

    private Callable<Boolean> expectedMileageisAvailable() {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception {
                return go; // The condition that must be fulfilled
            }
        };
    }

    void setPerformance() {
        try {
            DecimalFormat df2 = new DecimalFormat("0.00");
            String LastMileage = "" + df2.format(lastMileage);
            String AverageMileage = "" + df2.format(averageMileage);
            textView3.setText(AverageMileage + "kmpl");
            textView.setText(LastMileage + "kmpl");

            String Performance = "";

            //await().atMost(1, SECONDS).until(expectedMileageisAvailable());

            Double em= Double.parseDouble(expectedMileage);

            double diff = em - averageMileage;

            if (diff > 7.0) {

                Performance = "Performance is Bad";

                r1.setBackgroundColor(Color.parseColor("#900C3F"));

            } else if (diff > 4.0 && diff < 7.0) {
                Performance = "Performance is Average";
                r1.setBackgroundColor(Color.parseColor("#FFFF00"));

            } else if (diff < 4.0 && diff >= 0.0) {
                Performance = "Performance is Good";
                r1.setBackgroundColor(Color.parseColor("#32CD32"));

            } else {

                Performance = "Wow! This is Crazy";
                r1.setBackgroundColor(Color.parseColor("#0000FF"));
            }
            textView2.setText(Performance);

        } catch (Exception e) {

        }

    }


    public static class RandomAdapter extends OdometerAdapter {
        private int randomInt = 0;

        @Override
        public int getNumber() {
            return randomInt;
        }

        void setValue(int num) {
            randomInt = num;
            notifyDataSetChanged();
        }
    }
}

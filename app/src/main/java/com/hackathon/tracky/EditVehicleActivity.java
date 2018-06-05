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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditVehicleActivity extends AppCompatActivity {


    TextView txt1, txt2,txt3,txt4;
    Spinner sp1,sp2;
    Button bt1;
    CardView crd1;
    EditText ed1;
    String uid;
    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference;
    de.hdodenhof.circleimageview.CircleImageView img;
    ProgressBar pg1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_vehicle_dialog);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        final String vehicleName = intent.getStringExtra("VehicleName");
        final String vehicleNumber = intent.getStringExtra("VehicleNumber");
        final String vehicleType = intent.getStringExtra("VehicleType");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        uid =firebaseUser.getUid();//Fetch from firebase Auth

        txt1=findViewById(R.id.textView4);
        txt2=findViewById(R.id.textView5);
        txt3=findViewById(R.id.txt_vehName);
        txt4=findViewById(R.id.txt_vehNum);


        sp1=findViewById(R.id.spinner_type);
        sp2=findViewById(R.id.spinner_model);
        bt1=findViewById(R.id.button_addVehicle);
        crd1=findViewById(R.id.cardview);
        ed1=findViewById(R.id.editText_vehicleNumber);
        pg1=findViewById(R.id.progressBar2);
        img =findViewById(R.id.img_vehImg);

        txt1.setVisibility(View.INVISIBLE);
        txt2.setVisibility(View.INVISIBLE);
        sp2.setVisibility(View.INVISIBLE);
        bt1.setVisibility(View.INVISIBLE);
        crd1.setVisibility(View.INVISIBLE);
        ed1.setVisibility(View.INVISIBLE);
        pg1.setVisibility(View.INVISIBLE);

        ed1.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        ed1.setText(vehicleNumber);
        ed1.setEnabled(false);


        bt1.setText("Update Vehicle");


        List<String> list = new ArrayList<String>();
        list.add("Motorbike");
        list.add("Car");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(R.layout.vehicle_spinner_item);

        sp1.setAdapter(dataAdapter);

        int spinnerPosition = dataAdapter.getPosition(vehicleType);

        sp1.setSelection(spinnerPosition);



        final ArrayList<String> modelList = new ArrayList<>();

        final ArrayAdapter<String> modelAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, modelList);
        modelAdapter.setDropDownViewResource(R.layout.vehicle_spinner_item);
        sp2.setAdapter(modelAdapter);




        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                txt3.setText(sp2.getSelectedItem().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });


        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String text = sp1.getSelectedItem().toString();
                String type="";
                pg1.setVisibility(View.VISIBLE);
                txt1.setVisibility(View.INVISIBLE);
                txt2.setVisibility(View.INVISIBLE);
                sp2.setVisibility(View.INVISIBLE);
                bt1.setVisibility(View.INVISIBLE);
                crd1.setVisibility(View.INVISIBLE);
                ed1.setVisibility(View.INVISIBLE);

                if(text.equals("Motorbike")){
                    type="bike";
                    img.setImageResource(R.drawable.bike);
                }else{
                    type="car";
                    img.setImageResource(R.drawable.car);

                }


                //Fetch Data from Firebase
                mPostReference = mDatabase.child("vehicles").child(type);
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        modelList.clear();
                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                            String name = postSnapshot.child("vehicleName").getValue(String.class);
                            modelList.add(name);
                        }
                        modelAdapter.notifyDataSetChanged();
                        int spinnerPosition2 = modelAdapter.getPosition(vehicleType);

                        sp2.setSelection(spinnerPosition2);
                        txt1.setVisibility(View.VISIBLE);
                        txt2.setVisibility(View.VISIBLE);
                        sp2.setVisibility(View.VISIBLE);
                        bt1.setVisibility(View.VISIBLE);
                        crd1.setVisibility(View.VISIBLE);
                        ed1.setVisibility(View.VISIBLE);
                        pg1.setVisibility(View.INVISIBLE);



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                mPostReference.addListenerForSingleValueEvent(postListener);




            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });



        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

         final TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                txt4.setText(s);
            }

            public void afterTextChanged(Editable s) {
            }
        };

        ed1.addTextChangedListener(mTextEditorWatcher);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vehName = sp2.getSelectedItem().toString();
                String vehNumber = ed1.getText().toString();
                String vehType = sp1.getSelectedItem().toString();

                //String Validation Required


                if(vehNumber.replaceAll("\\s","").equals("")){
                    Toast.makeText(getApplicationContext(),"Invalid Vehicle Number", Toast.LENGTH_SHORT).show();
                }else{


                    Map mParent = new HashMap();
                    mParent.put("vehicleName", vehName);
                    mParent.put("vehicleNumber", vehNumber);
                    if(vehType.equals("Motorbike")){
                        mParent.put("vehicleImage", "bike");
                    }else{
                        mParent.put("vehicleImage", "car");

                    }
                    mDatabase.child("users").child(uid).child("vehicles").child(vehNumber).setValue(mParent);
                    Toast.makeText(getApplicationContext(),"Updated Vehicle Sucessfully",Toast.LENGTH_SHORT).show();
                    Intent intent =new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }

            }
        });


    }






}

package com.hackathon.tracky;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Keep;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


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

import java.util.List;

/**
 * Created by Akhilesh on 9/8/2017.
 */
@Keep
public class PersonalVehicleAdapter extends RecyclerView.Adapter<PersonalVehicleAdapter.MyViewHolder> {

    private List<Vehicle> VehicleList;
    Context mContext;
    Context activityContext;
    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference;
    TextView txt2;
    String uid;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView vehName, vehNumber;
        public de.hdodenhof.circleimageview.CircleImageView img;
        ImageView remove;

        public MyViewHolder(View view) {
            super(view);
            vehName = (TextView) view.findViewById(R.id.txt_vehName);
            vehNumber = (TextView) view.findViewById(R.id.txt_vehNum);
            img=(de.hdodenhof.circleimageview.CircleImageView)view.findViewById(R.id.img_vehImg);
            remove=view.findViewById(R.id.imageView);
            mDatabase = FirebaseDatabase.getInstance().getReference();

        }
    }


    public PersonalVehicleAdapter(List<Vehicle> VehicleList, Context mContext) {
        this.VehicleList = VehicleList;
        this.mContext = mContext;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        uid =firebaseUser.getUid();//Fetch from firebase Auth
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vehicle_list_item, parent, false);

        return new MyViewHolder(itemView);
    }





    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Vehicle v = VehicleList.get(position);
        holder.vehName.setText(v.getVehicleName());
        holder.vehNumber.setText(v.getVehicleNumber());
        RequestOptions options = new RequestOptions();
        holder.remove.setVisibility(View.INVISIBLE);

        final String vn=v.getVehicleNumber();

        mPostReference = mDatabase.child("users").child(uid).child("removeVehiclesFlag");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               boolean val =dataSnapshot.getValue(Boolean.class);
               if(val){
                   holder.remove.setVisibility(View.VISIBLE);

               }else{
                   holder.remove.setVisibility(View.INVISIBLE);

               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mPostReference.addValueEventListener(postListener);


        if(v.getVehicleImage().equals("bike")){
            Glide   .with(mContext)
                    .load(R.drawable.bike)
                    .apply(new RequestOptions()

                            .placeholder(R.mipmap.ic_launcher)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter()
                    )

                    .into(holder.img);
        }else{
            Glide   .with(mContext)
                    .load(R.drawable.car)
                    .apply(new RequestOptions()

                            .placeholder(R.mipmap.ic_launcher)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter()
                    )

                    .into(holder.img);

        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent =new Intent(mContext, VehicleDetailActivity.class);
               intent.putExtra("VehicleName",v.getVehicleName() );
                intent.putExtra("VehicleType",v.getVehicleImage() );
                intent.putExtra("VehicleNumber",v.getVehicleNumber() );
                mContext.startActivity(intent);

            }
        });


        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                mDatabase.child("users").child(uid).child("vehicles").child(vn).removeValue();
                                mDatabase.child("users").child(uid).child("removeVehiclesFlag").setValue(false);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to remove this Vehicle? All Data is Permanently lost.").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return VehicleList.size();
    }






}
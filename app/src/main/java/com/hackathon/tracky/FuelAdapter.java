package com.hackathon.tracky;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Keep;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.security.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Akhilesh on 9/8/2017.
 */
@Keep
public class FuelAdapter extends RecyclerView.Adapter<FuelAdapter.MyViewHolder> {

    private List<Newrefill> FuelList;
    Context mContext;
    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference;
    String uid;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fuelconsumed, cost,dates,lcn;
        ImageView delete;

        public MyViewHolder(View view) {
            super(view);
            fuelconsumed = (TextView) view.findViewById(R.id.txtExpected);
            cost = (TextView) view.findViewById(R.id.txtDerived);
            mDatabase = FirebaseDatabase.getInstance().getReference();
            delete=view.findViewById(R.id.imageDelete);
            dates=view.findViewById(R.id.txtDate);
            lcn=view.findViewById(R.id.lcnbtn);
        }
    }


    public FuelAdapter(List<Newrefill> FuelList, Context mContext,DatabaseReference mPostReference ) {
        this.FuelList = FuelList;
        this.mContext = mContext;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.mPostReference=mPostReference;
        uid =firebaseUser.getUid();//Fetch from firebase Auth
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fuel_listitem, parent, false);

        return new MyViewHolder(itemView);
    }





    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        DecimalFormat df2 = new DecimalFormat("0.00");

        final Newrefill r = FuelList.get(position);
        double f=r.getPetrol_after_refuel()-r.getPetrol_before_refuel();
        holder.fuelconsumed.setText(""+df2.format(f)+"L");
        double price =80; //Hardcoded Fuel Price
        holder.cost.setText("₹"+df2.format(f*price)+"/-");
        long time = Long.parseLong(r.timestamp);


        Date expiry = new Date( time * 1000 );
        // Addressing your comment:
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy");
        holder.dates.setText(sdf.format(expiry));
        holder.lcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<"+r.latitude+">,<"+r.longitude+">?q=<"+r.latitude+">,<"+r.longitude+">(Gas+Station)"));
                mContext.startActivity(intent);
            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                mPostReference.child(r.timestamp).removeValue();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to delete this fuel log?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();



            }
        });

    }

    @Override
    public int getItemCount() {
        return FuelList.size();
    }






}
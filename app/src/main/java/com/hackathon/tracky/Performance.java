package com.hackathon.tracky;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Performance {
    double perform;

    public Performance(double perform) {
        this.perform = perform;
    }

    public Performance() {
        this.perform = 1.0;

    }

    public double getPerform() {
        return perform;
    }

    public void setPerform(double perform) {
        this.perform = perform;
    }
}

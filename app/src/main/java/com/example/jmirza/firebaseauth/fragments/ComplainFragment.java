package com.example.jmirza.firebaseauth.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jmirza.firebaseauth.R;
import com.example.jmirza.firebaseauth.activities.ProfileActivity;
import com.example.jmirza.firebaseauth.models.Complaint;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ComplainFragment extends Fragment implements View.OnClickListener {
    TextInputEditText deviceNumberEt, roomNumberEt, descriptionEt;
    Button createButton;
    public FirebaseAuth uAuth;
    public DatabaseReference myRef;
    public DatabaseReference myComRef;
    public View view;
    public ProgressBar progressBar;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_complain, container, false);
        initialization();
        onClick();

        return view;
    }

    private void initialization() {
        uAuth = FirebaseAuth.getInstance();
        user = uAuth.getCurrentUser();
        progressBar = view.findViewById(R.id.progressBarID);
        deviceNumberEt = view.findViewById(R.id.deviceNumberEtID);
        roomNumberEt = view.findViewById(R.id.roomNoET);
        descriptionEt = view.findViewById(R.id.descriptionID);
        createButton = view.findViewById(R.id.createID);
        myComRef = FirebaseDatabase.getInstance().getReference("complaints");
    }

    @Override
    public void onResume() {
        super.onResume();
        // we typecasted getActivity for getting profile's actionBar....
        ((ProfileActivity) getActivity()).setActionBarTitle("Make a Complaint...");
    }


    public void onClick() {
        createButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.createID) {
            createComplaint();
        }
    }

    private void createComplaint() {

        if (checkValidity()) {
            // checkValidity is true so given errors are shown to the user
        } else {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseUser user = uAuth.getCurrentUser();
            final String complaintUserId = user.getUid();
            myRef = FirebaseDatabase.getInstance().getReference("users").child(complaintUserId);
            myRef.addValueEventListener(new ValueEventListener() {
                String deviceNumber = deviceNumberEt.getText().toString().trim();
                String roomNo = roomNumberEt.getText().toString().trim();
                String description = descriptionEt.getText().toString().trim();
                String complainStatus = "Pending";
                String complainNote = "null";

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat mdFormat = new SimpleDateFormat("yyyy / MM / dd ");
                    String date = mdFormat.format(calendar.getTime());
                    final String complainPushId = myComRef.push().getKey();
                    final String complaintUserName = dataSnapshot.child("name").getValue().toString();
                    final String complaintUserDept = dataSnapshot.child("department").getValue().toString();
                    final String complaintUserDeviceToken = dataSnapshot.child("deviceToken").getValue().toString();
                    final Complaint complaint = new Complaint(complainPushId, complaintUserId, complaintUserName,
                            complaintUserDept, complaintUserDeviceToken, deviceNumber, roomNo, description, complainStatus, date, complainNote);
                    if (complainPushId != null) {
                        FirebaseDatabase.getInstance().getReference("complaints").child(complainPushId)
                                .setValue(complaint).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Your complaint submitted successfully  !!",
                                            Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getContext(), ProfileActivity.class));
                                } else {
                                    Toast.makeText(getContext(), "error!!" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }

                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    public boolean checkValidity() {

        boolean cancel = false;
        String uName = deviceNumberEt.getText().toString();
        String roomNo = roomNumberEt.getText().toString();
        String description = descriptionEt.getText().toString();

        if (TextUtils.isEmpty(uName)) {
            cancel = true;
            deviceNumberEt.setError("Enter number of your complaint!!");
            deviceNumberEt.requestFocus();
        } else if (TextUtils.isEmpty(roomNo)) {
            cancel = true;
            roomNumberEt.setError("Enter a room number!!");
            roomNumberEt.requestFocus();
        } else if (TextUtils.isEmpty(description)) {
            cancel = true;
            descriptionEt.setError("Enter a description!!!");
            descriptionEt.requestFocus();
        }
        return cancel;

    }


}

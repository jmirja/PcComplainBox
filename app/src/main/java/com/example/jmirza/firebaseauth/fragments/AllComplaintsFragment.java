package com.example.jmirza.firebaseauth.fragments;


import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;


import com.example.jmirza.firebaseauth.R;

import com.example.jmirza.firebaseauth.activities.ProfileActivity;
import com.example.jmirza.firebaseauth.activities.SearchActivity;
import com.example.jmirza.firebaseauth.adapters.ComplainAdapter;
import com.example.jmirza.firebaseauth.models.Complaint;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AllComplaintsFragment extends Fragment implements SearchView.OnQueryTextListener, View.OnClickListener {

    private View view;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private RecyclerView recyclerView;
    private ComplainAdapter complainAdapter;
    private List<Complaint> allComplaintList;
    private FirebaseAuth uAuth;
    private DatabaseReference myRef;
    private FirebaseUser user;
    private Complaint allComplaints;
    private android.support.v7.widget.SearchView searchView;
    private Button searchBT;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_complaints_all, container, false);
        setHasOptionsMenu(true);
        initialization();
        onClick();
        return view;
    }

    private void initialization() {

        uAuth = FirebaseAuth.getInstance();
        user = uAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference("complaints");
        searchBT = view.findViewById(R.id.searchBT);

        // setting up custom toolbar or actionbar
        toolbar = view.findViewById(R.id.toolbarID);
        toolbarTitle = view.findViewById(R.id.toolbar_title);
        recyclerView = view.findViewById(R.id.all_complaints_recycleView);
        // recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allComplaintList = new ArrayList<>();

    }


    @Override
    public void onStart() {
        super.onStart();
        allComplaints();
    }

    private void allComplaints() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allComplaintList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    allComplaints = postSnapshot.getValue(Complaint.class);
                    allComplaintList.add(allComplaints);
                }
                complainAdapter = new ComplainAdapter(getContext(), allComplaintList);
                recyclerView.setAdapter(complainAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ((ProfileActivity) getActivity()).setActionBarTitle("All Complaints...");
    }

    private void onClick() {
        searchBT.setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem item = menu.findItem(R.id.searchID);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        searchView = (android.support.v7.widget.SearchView) item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(this);

    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        s = s.toLowerCase();
        ArrayList<Complaint> newList = new ArrayList<>();
        for (Complaint complaint : allComplaintList) {
            String name = complaint.complainStatus.toLowerCase();
            if (name.contains(s)) {
                newList.add(complaint);
            }
        }
        complainAdapter.setSearchOperation(newList);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.searchBT) {
            startActivity(new Intent(getActivity(), SearchActivity.class));
        }

    }


  /*  private void showFromDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String dateFrom = "month/day/year: " + month + "/" + dayOfMonth + "/" + year;
            Toast.makeText(getActivity(), "month/day/year: from " + month + dayOfMonth + year , Toast.LENGTH_LONG).show();
         *//*else if (showToDatePickerDialog()) {
            String dateTo = "month/day/year: " + month + "/" + dayOfMonth + "/" + year;
            Toast.makeText(getActivity(), "month/day/year: to " + month + dayOfMonth + year , Toast.LENGTH_LONG).show();
        }*//*


    }*/
}
package com.nkrute.cigarchat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nkrute.cigarchat.adapters.AdapterNotification;
import com.nkrute.cigarchat.models.ModelNotification;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    RecyclerView notificationRv;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelNotification> notificationList;

    private AdapterNotification adapterNotification;
    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        notificationRv = view.findViewById(R.id.notificationRv);
        firebaseAuth = FirebaseAuth.getInstance();

        getAllNotifications();
        
        return view;
    }

    private void getAllNotifications() {
        notificationList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Notifications")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notificationList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()) {
                            //get data
                            ModelNotification model = ds.getValue(ModelNotification.class);

                            //add to list
                            notificationList.add(model);
                        }

                        //adapter
                        adapterNotification = new AdapterNotification(getActivity(), notificationList);
                        //set to recycler view
                        notificationRv.setAdapter(adapterNotification);
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {

                    }
                });
    }
}
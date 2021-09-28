package com.nkrute.cigarchat.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nkrute.cigarchat.GroupCreateActivity;
import com.nkrute.cigarchat.MainActivity;
import com.nkrute.cigarchat.R;
import com.nkrute.cigarchat.SettingsActivity;
import com.nkrute.cigarchat.adapters.AdapterUsers;
import com.nkrute.cigarchat.models.ModelUser;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUsers adapterUsers;
    List<ModelUser> userList;

    //firebase auth
    FirebaseAuth firebaseAuth;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_users, container, false);

        // init
        firebaseAuth = FirebaseAuth.getInstance();

        // init recycler view
        recyclerView = view.findViewById(R.id.users_recyclerView);
        // set it's properties
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager((new LinearLayoutManager(getActivity())));

        // init user list
        userList = new ArrayList<>();

        // get all usres
        getAllUsers();

        return view;
    }

    private void getAllUsers() {
        // get current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        // get path of database named "Users" containing users info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    //get all users except current
                    if (!modelUser.getUid().equals(fUser.getUid())) {
                        userList.add(modelUser);
                    }

                    // adapter
                    adapterUsers = new AdapterUsers(getActivity(), userList);
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchUsers(String query) {
        // get current user
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        // get path of database named "Users" containing users info
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    ModelUser modelUser = ds.getValue(ModelUser.class);

                    //get all users except current
                    if (!modelUser.getUid().equals(fUser.getUid())) {
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) || modelUser.getEmail().toLowerCase().contains(query.toLowerCase())) {
                            userList.add(modelUser);
                        }
                    }

                    // adapter
                    adapterUsers = new AdapterUsers(getActivity(), userList);
                    //refresh view
                    adapterUsers.notifyDataSetChanged();
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterUsers);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUserStatus() {
        // get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // user is signed in stay hee
            // set email of logged in user
            // mProfileTv.setText(user.getEmail());
        }
        else {
            // user is not signed in, go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    /*handle menu item click*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // get item id
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        else if (id==R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        }
        else if (id==R.id.action_create_group) {
            startActivity(new Intent(getActivity(), GroupCreateActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    /* inflate options menu */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflating
        inflater.inflate(R.menu.menu_main, menu);

        // hide add post icon from this fragment
        menu.findItem(R.id.action_add_post).setVisible(false);
        menu.findItem(R.id.action_add_participant).setVisible(false);
        menu.findItem(R.id.action_groupinfo).setVisible(false);

        // search View
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        // search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // if search query is not emtpy then search
                if (!TextUtils.isEmpty(s.trim())) {
                    // search partial
                    searchUsers(s);
                } else {
                    // search text empty, get all users
                    getAllUsers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // if search query is not emtpy then search
                if (!TextUtils.isEmpty(s.trim())) {
                    // search partial
                    searchUsers(s);
                } else {
                    // search text empty, get all users
                    getAllUsers();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

}
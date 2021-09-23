package com.nkrute.cigarchat.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
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
import com.nkrute.cigarchat.adapters.AdapterGroupChatList;
import com.nkrute.cigarchat.models.ModelGroupChatList;

import java.util.ArrayList;

public class GroupChatFragment extends Fragment {

    private RecyclerView groupsRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelGroupChatList> groupChatLists;
    private AdapterGroupChatList adapterGroupChatList;

    public GroupChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);
        groupsRv = view.findViewById(R.id.groupRv);
        firebaseAuth = FirebaseAuth.getInstance();
        loadGroupChatsList();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_chat, container, false);
    }

    private void loadGroupChatsList() {
        groupChatLists = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatLists.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    if (ds.child("Participants").child(firebaseAuth.getUid()).exists()) {
                        ModelGroupChatList model = ds.getValue(ModelGroupChatList.class);
                        groupChatLists.add(model);
                    }
                }
                adapterGroupChatList = new AdapterGroupChatList(getActivity(), groupChatLists);
                groupsRv.setAdapter(adapterGroupChatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGroupChatsList(String query) {
        groupChatLists = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatLists.clear();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    if (ds.child("Participants").child(firebaseAuth.getUid()).exists()) {
                        //search by group title
                        if (ds.child("groupTitle").toString().toLowerCase().contains(query.toLowerCase())) {
                            ModelGroupChatList model = ds.getValue(ModelGroupChatList.class);
                            groupChatLists.add(model);
                        }
                    }
                }
                adapterGroupChatList = new AdapterGroupChatList(getActivity(), groupChatLists);
                groupsRv.setAdapter(adapterGroupChatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            // user is not signed in, go to main activity
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    }

    /* inflate options menu */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // inflating
        inflater.inflate(R.menu.menu_main, menu);

        // hide add post icon from this fragment
        menu.findItem(R.id.action_add_post).setVisible(false);
        menu.findItem(R.id.action_settings).setVisible(false);

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
                    searchGroupChatsList(s);
                } else {
                    // search text empty, get all users
                    loadGroupChatsList();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // if search query is not emtpy then search
                if (!TextUtils.isEmpty(s.trim())) {
                    // search partial
                    searchGroupChatsList(s);
                } else {
                    // search text empty, get all users
                    loadGroupChatsList();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }
}
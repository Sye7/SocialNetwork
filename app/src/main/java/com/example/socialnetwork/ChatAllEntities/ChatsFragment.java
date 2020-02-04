package com.example.socialnetwork.ChatAllEntities;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialnetwork.R;
import com.example.socialnetwork.model.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;


public class ChatsFragment extends Fragment {

    public RecyclerView recyclerView;
    public UserAdapter userAdapter;
    public HashSet<Profile> mUsers;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    public List<String> usersList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_fr);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        usersList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference().child("Chats");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);


                    if(chat.getSender().equals(firebaseUser.getUid())){
                        usersList.add(chat.getReceiver());
                    }
                    if(chat.getReceiver().equals(firebaseUser.getUid())){
                        usersList.add(chat.getSender());

                    }
                }
                readChats();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    public void readChats() {
        mUsers = new HashSet<>();
        reference = FirebaseDatabase.getInstance().getReference().child("Profile");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Profile user = snapshot.getValue(Profile.class);
                    try{
                        for(String id : usersList){
                            if(user.getId().equals(id)){
                                if(mUsers.size() != 0){
                                    for(Profile user1 : mUsers){
                                        if(!user.getId().equals(user1.getId())){
                                            mUsers.add(user);
                                        }
                                    }
                                }else{
                                    mUsers.add(user);
                                }
                            }
                        }
                    }catch (ConcurrentModificationException e){
                    }

                }

                userAdapter = new UserAdapter(getContext(),new ArrayList<Profile>(mUsers));
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
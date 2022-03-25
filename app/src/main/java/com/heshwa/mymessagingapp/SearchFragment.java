package com.heshwa.mymessagingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    private ListView mListView;
    private ArrayAdapter mArrayAdapter;
    private ArrayList<String> userArrayList;
    private static ArrayList<String> userIds;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private EditText edtSearch;



    public SearchFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);


        mListView = view.findViewById(R.id.listViewUsers);
        userArrayList = new ArrayList<>();
        userIds = new ArrayList<>();
        mArrayAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,userArrayList);
        mListView.setAdapter(mArrayAdapter);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(getContext(), MessageActivity.class);
                intent.putExtra("ReceiverId",userIds.get(position));
                intent.putExtra("ReceiverName",userArrayList.get(position));
                startActivity(intent);



            }
        });
        edtSearch = view.findViewById(R.id.edtSearch);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Getting Users");
        progressDialog.setMessage("please wait");
        progressDialog.show();
        userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists() && snapshot!= null && !snapshot.getKey().equals(mAuth.getCurrentUser().getUid()))
                {
                    userArrayList.add(snapshot.child("Name").getValue().toString());
                    userIds.add(snapshot.getKey());
                }
                mArrayAdapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
    private void searchUsers(String enterTxt)
    {
        userArrayList.clear();
        userIds.clear();
        mArrayAdapter.notifyDataSetChanged();
        userRef.orderByChild("Name").startAt(enterTxt).endAt(enterTxt +"\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userSnapshots) {
                        userArrayList.clear();
                        userIds.clear();
                        if(userSnapshots.exists() && userSnapshots!= null)
                        {
                            for(DataSnapshot snapshot:userSnapshots.getChildren())
                            {
                                if(!snapshot.getKey().equals(mAuth.getCurrentUser().getUid()))
                                {
                                    userArrayList.add(snapshot.child("Name").getValue().toString());
                                    userIds.add(snapshot.getKey());
                                }


                            }
                            mArrayAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
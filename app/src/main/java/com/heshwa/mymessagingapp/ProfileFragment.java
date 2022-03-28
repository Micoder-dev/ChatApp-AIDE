package com.heshwa.mymessagingapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.Button;
import android.content.Intent;
import java.util.HashMap;
import com.google.firebase.database.ServerValue;


public class ProfileFragment extends Fragment {
    private TextView txtProfile;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
	private Button logoutBtn;



    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        txtProfile = view.findViewById(R.id.txtProfileName);
		
		logoutBtn=view.findViewById(R.id.logoutBtn);
		logoutBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				HashMap<String,Object> map = new HashMap<>();
				map.put("Offline",ServerValue.TIMESTAMP);
				userRef.child(mAuth.getCurrentUser().getUid()).child("Status").setValue(map);
				mAuth.signOut();
				Intent intent = new Intent(getActivity(),LoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				getActivity().finish();
			}
		});
		
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference()
                .child("Users");
        userRef.child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.hasChild("Name"))
                {
                    txtProfile.setText(snapshot.child("Name").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}

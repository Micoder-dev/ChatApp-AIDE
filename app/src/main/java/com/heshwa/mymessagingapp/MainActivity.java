package com.heshwa.mymessagingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private TextView txtdiplayUserName;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private TabAdapter mTabAdapter;
	
	//private ImageView imgView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtdiplayUserName = findViewById(R.id.txtDisplayName);




        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
        mTabAdapter = new TabAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mToolbar = findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
		
		//imgView2=findViewById(R.id.imgView2);
		
		//String imageUri = "https://i.imgur.com/tGbaZCY.jpg";
		//Picasso.with(this).load(imageUri).into(imgView2);


        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        userRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtdiplayUserName.setText(snapshot.child("Name").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });








    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_mainactivity,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.itmLogOut)
        {
            
			new AlertDialog.Builder(this)
				.setMessage("Are you sure want to Logout!")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						HashMap<String,Object> map = new HashMap<>();
						map.put("Offline",ServerValue.TIMESTAMP);
						userRef.child(mAuth.getCurrentUser().getUid()).child("Status").setValue(map);
						mAuth.signOut();
						Intent intent = new Intent(MainActivity.this,LoginActivity.class);
						startActivity(intent);
						
					}
				}).setNegativeButton("No", null)
				.show();
			
			
			
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
		
		new AlertDialog.Builder(this)
			.setMessage("Are you sure want to exit???")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					finish();
				}
			})
			.setNegativeButton("No", null)
			.show();
    }



    @Override
    protected void onStart() {
        super.onStart();
        HashMap<String,Object> map = new HashMap<>();
        map.put("Online",ServerValue.TIMESTAMP);
        userRef.child(mAuth.getCurrentUser().getUid()).child("Status").setValue(map);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HashMap<String,Object> map = new HashMap<>();
        map.put("Offline",ServerValue.TIMESTAMP);
        userRef.child(mAuth.getCurrentUser().getUid()).child("Status").setValue(map);
    }
}

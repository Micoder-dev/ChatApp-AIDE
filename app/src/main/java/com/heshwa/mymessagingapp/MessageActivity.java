package com.heshwa.mymessagingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MessageActivity extends AppCompatActivity {
    private TextView txtRecieverUserName,txtLastSeen;
    private String reciverName,receiverId;
    private DatabaseReference userRef,chatRef;
    private Toolbar mToolbar;
    private EditText edtMessage;
    private ImageButton btnSend;
    private FirebaseAuth mAuth;
    private RecyclerView mRecyclerView;
    public static ArrayList<String> messages;
    public static ArrayList<Integer> messagePosition;
    public static ArrayList<String> messageIds;
    private MessageAdapter mMessageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        txtRecieverUserName = findViewById(R.id.txtReceiverName);
        txtLastSeen = findViewById(R.id.txtLastSeen);
        Intent intent = getIntent();
        reciverName = intent.getStringExtra("ReceiverName");
        receiverId = intent.getStringExtra("ReceiverId");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.child(receiverId);
        txtRecieverUserName.setText(reciverName);
        mToolbar = findViewById(R.id.toolBarMessage);
        setSupportActionBar(mToolbar);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        chatRef = FirebaseDatabase.getInstance().getReference().child("Chats");
        mAuth = FirebaseAuth.getInstance();
        mRecyclerView = findViewById(R.id.recycularViewMessages);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        messages = new ArrayList<>();
        messagePosition = new ArrayList<>();
        messageIds = new ArrayList<>();
        mMessageAdapter= new MessageAdapter(MessageActivity.this,messages);
        mRecyclerView.setAdapter(mMessageAdapter);


        getLastSeen();
        getMessages();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtMessage.getText().toString().equals(""))
                {
                    Toast.makeText(MessageActivity.this,"Enter Something ",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    HashMap<String,String> map = new HashMap<>();
                    map.put("SenderId",mAuth.getCurrentUser().getUid());
                    map.put("Message",edtMessage.getText().toString());
                    map.put("ReceiverId",receiverId);
                    chatRef.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                userRef.child(mAuth.getCurrentUser().getUid())
                                        .child("ChatLists").child(receiverId).setValue(ServerValue.TIMESTAMP);
                                userRef.child(receiverId).child("ChatLists")
                                        .child(mAuth.getCurrentUser().getUid()).setValue(ServerValue.TIMESTAMP);
                                edtMessage.setText("");
                            }
                        }
                    });
                }
            }
        });





    }
    void getLastSeen()
    {
        userRef.child(receiverId).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null && snapshot.hasChild("Status")) {
                    if (snapshot.child("Status").hasChild("Online")) {
                        txtLastSeen.setText("Online");
                    } else {
                        Object objTimeStamp = snapshot.child("Status").child("Offline").getValue();
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String lastseenString = df.format(objTimeStamp);
                        String currentDateString = df.format(new Date());
                        Date currentDate = null,lastseenDate = null;
                        try {
                            currentDate = df.parse(currentDateString);
                            lastseenDate = df.parse(lastseenString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(currentDate.compareTo(lastseenDate)==0)
                        {
                            String time =new SimpleDateFormat("h:mm a").format(objTimeStamp);
                            txtLastSeen.setText("last seen :"+time);
                        }
                        else
                        {
                            String date=  new SimpleDateFormat("yyyy-MM-dd").format(objTimeStamp);
                            txtLastSeen.setText("last seen :"+date);
                        }


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void getMessages()
    {
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot!=null && snapshot.exists())
                {
                    if(snapshot.child("ReceiverId").getValue().toString().equals(receiverId)
                            && snapshot.child("SenderId").getValue().toString().equals(mAuth.getCurrentUser().getUid()))
                    {
                        messages.add(snapshot.child("Message").getValue().toString());
                        messagePosition.add(1);
                        mMessageAdapter.notifyItemChanged(messages.size()-1);
                        messageIds.add(snapshot.getKey());
                        mRecyclerView.smoothScrollToPosition(messages.size()-1);
                    }
                    else if(snapshot.child("ReceiverId").getValue().toString().equals(mAuth.getCurrentUser().getUid())
                            && snapshot.child("SenderId").getValue().toString().equals(receiverId))
                    {
                        messages.add(snapshot.child("Message").getValue().toString());
                        messagePosition.add(0);
                        mMessageAdapter.notifyItemChanged(messages.size()-1);
                        messageIds.add(snapshot.getKey());
                        mRecyclerView.smoothScrollToPosition(messages.size()-1);
                    }
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if((snapshot.child("ReceiverId").getValue().toString().equals(receiverId)
                        && snapshot.child("SenderId").getValue().toString().equals(mAuth.getCurrentUser().getUid()))
                || (snapshot.child("ReceiverId").getValue().toString().equals(mAuth.getCurrentUser().getUid())
                        && snapshot.child("SenderId").getValue().toString().equals(receiverId))) {
                    int pos = messageIds.indexOf(snapshot.getKey());
                    messagePosition.remove(pos);
                    messages.remove(pos);
                    messageIds.remove(pos);
                    mMessageAdapter.notifyItemRemoved(pos);
                }


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
package com.heshwa.mymessagingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>  {

    private Context mContext;
    private ArrayList<String> messsages;
    private DatabaseReference chatRef;

    public MessageAdapter(Context context, ArrayList<String> messsages) {
        mContext = context;
        this.messsages = messsages;
        chatRef = FirebaseDatabase.getInstance().getReference().child("Chats");
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.message_item,parent,false);
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, final int position) {
        final int pos=MessageActivity.messagePosition.get(position);
        if(pos==0)
        {
            holder.txtleft.setVisibility(View.VISIBLE);
            holder.txtleft.setText(messsages.get(position));
            holder.txtRight.setVisibility(View.GONE);
        }
        else {
            holder.txtRight.setVisibility(View.VISIBLE);
            holder.txtRight.setText(messsages.get(position));
            holder.txtleft.setVisibility(View.GONE);

        }
        holder.txtRight.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("Delete Messages");
                alert.setMessage("Are you sure?\nwhether you want to delete this message \nMessage :"+messsages.get(position));
                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        chatRef.child(MessageActivity.messageIds.get(position)).removeValue();

                    }
                });
                alert.show();
                return true;
            }
        });




    }

    @Override
    public int getItemCount() {
        return messsages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtleft,txtRight;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtleft = itemView.findViewById(R.id.txtLeft);
            txtRight = itemView.findViewById(R.id.txtRight);
        }
    }
}

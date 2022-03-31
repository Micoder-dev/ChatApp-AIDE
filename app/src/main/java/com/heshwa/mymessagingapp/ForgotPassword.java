package com.heshwa.mymessagingapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import android.util.Patterns;
import com.google.android.gms.tasks.OnCompleteListener;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import android.widget.Toast;
import android.app.ProgressDialog;

public class ForgotPassword extends Activity {
    private EditText edtEmailReset;
	private Button btnReset;
	private ProgressDialog pg;
	
	private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
		
		edtEmailReset=findViewById(R.id.edtEmailReset);
		btnReset=findViewById(R.id.btnReset);
		
		pg=new ProgressDialog(ForgotPassword.this);
        pg.setTitle("Forgot Password");
        pg.setMessage("Please check your email, after reset your password go back to login page");
		
		auth=FirebaseAuth.getInstance();
		
		btnReset.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				resetPassword();
			}
		});
    }
    private void resetPassword(){
		String email=edtEmailReset.getText().toString().trim();
		
		if(email.isEmpty()){
			edtEmailReset.setError("Email is required");
			edtEmailReset.requestFocus();
			return;
		}
		if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
			edtEmailReset.setError("Enter a valid email");
			edtEmailReset.requestFocus();
			return;
		}
		
		pg.show();
		auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>(){
			@Override
			public void onComplete(@NonNull Task<Void> task){
				if(task.isSuccessful()){
					Toast.makeText(ForgotPassword.this,"Check your email address to change your password",Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(ForgotPassword.this,"Something went wrong try again after sometimes or Check your internet connection",Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}

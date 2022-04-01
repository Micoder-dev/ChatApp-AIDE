package com.heshwa.mymessagingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import android.util.Patterns;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private EditText edtEmail ,edtPassword;
    private FirebaseAuth mAuth;
	private TextView gotoRegister,forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmailLogin);
        edtPassword =findViewById(R.id.edtPasswordLogin);
        final ProgressDialog pg = new ProgressDialog(LoginActivity.this);
        pg.setTitle("Authentication");
        pg.setMessage("Please wait until authentication finishes");
        mAuth = FirebaseAuth.getInstance();
		
		forgotPassword=findViewById(R.id.forgotPassword);
		forgotPassword.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				startActivity(new Intent(LoginActivity.this,ForgotPassword.class));
			}
		});
		
		gotoRegister=findViewById(R.id.gotoRegister);
		gotoRegister.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
			}
		});

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();

                if(email.equals("")|| password.equals(""))
                {
                    //Toast.makeText(LoginActivity.this,"All fields are mandatory",Toast.LENGTH_SHORT).show();
					if(email.isEmpty()){
						edtEmail.setError("Email required");
						edtEmail.requestFocus();
						return;
					}
					if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
						edtEmail.setError("Please enter a valid email");
						edtEmail.requestFocus();
						return;
					}

					if(password.isEmpty()){
						edtPassword.setError("Password required");
						edtPassword.requestFocus();
						return;
					}
					if(password.length() < 6){
						edtPassword.setError("Password must be greater than 6");
						edtPassword.requestFocus();
						return;
					}
                }
                else {
                    pg.show();
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        pg.dismiss();
                                        Toast.makeText(LoginActivity.this,"Successfully Signed in",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(LoginActivity.this,SplashScreen.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        pg.dismiss();
                                        Toast.makeText(LoginActivity.this,"error :"+task.getException().toString(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}

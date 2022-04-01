package com.heshwa.mymessagingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {
	private FirebaseAuth mAuth;
	private FirebaseUser currentUser;
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
		

        mAuth = FirebaseAuth.getInstance();

        if (mAuth != null) {

            currentUser = mAuth.getCurrentUser();

        }

        new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					FirebaseUser user=mAuth.getCurrentUser();
					if(user==null){
						Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
						startActivity(intent);
						finish();
					}
					else {
						Intent mainIntent= new Intent(SplashScreen.this, MainActivity.class);
						mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(mainIntent);
						finish();
					}
				}
			},6*1000);

/*--
        //splashscreen bg vid
        videoView = findViewById(R.id.videoview);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_video);
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.setVolume(0, 0);
            }
        });

    }

    @Override
    protected void onResume() {
        videoView.resume();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        videoView.start();
        super.onRestart();
    }

    @Override
    protected void onPause() {
        videoView.suspend();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        videoView.stopPlayback();
        super.onDestroy();
    }


--*/
    }

    }

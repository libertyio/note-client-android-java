package io.liberty.note;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 300;
    private boolean isShowingSplash = false;
    private LibertyNote mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setContentView(R.layout.activity_main);

        mApp = (LibertyNote) getApplication();

        isShowingSplash = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isShowingSplash = false;
                startLoginActivity();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // check if user is already logged in
        if (!isShowingSplash) {
            startLoginActivity();
        }
    }

    private void startLoginActivity() {
        if (mApp.isAuthenticated()) {
            Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        } else {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }
}

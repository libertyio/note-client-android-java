package io.liberty.note;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import io.liberty.note.R;

public class AboutActivity extends AppCompatActivity {

    TextView textViewVersionNumber;
    ImageView imageViewBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        textViewVersionNumber = findViewById(R.id.textViewVersionNumber);
        imageViewBack = findViewById(R.id.imageViewBack);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        try {
            PackageManager packageManager = getPackageManager();
            String packageName = getPackageName();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA);

            textViewVersionNumber.setText(packageInfo.versionName);
        }
        catch(PackageManager.NameNotFoundException e) {
            Log.d("LIBERTY.IO", "Cannot read package info", e);
            textViewVersionNumber.setVisibility(View.INVISIBLE);
        }
    }
}

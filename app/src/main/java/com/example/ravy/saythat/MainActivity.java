package com.example.ravy.saythat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SurfaceView cameraView;
    ImageView cropView;
    TextView textView;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    TextToSpeech toSpeech;
    int setLanguageResult;
    boolean cameraUpdating;
    STTextRecognizer sttextRecognizer;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        textView = (TextView) findViewById(R.id.text_view);
        cropView = (ImageView) findViewById(R.id.cropimage);

       sttextRecognizer = new STTextRecognizer(getApplicationContext());
        if (!sttextRecognizer.isOperational()) {
            Log.w("MainActivity", "TextRecognizer dependencies are not available.");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), sttextRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(1.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);
                        }
                        cameraSource.start(cameraView.getHolder());
                        cameraUpdating = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                    cameraSource.stop();
                }
            });
            sttextRecognizer.setProcessor(new STDetectorProcessor(textView));
        }

        toSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    setLanguageResult = toSpeech.setLanguage(Locale.ENGLISH);
                } else {
                    Toast.makeText(getApplicationContext(), "Feature not supported on your device.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sayText(View view) {
        toSpeech.speak(textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, "asdf");

    }

    public void pauseCamera(View view) {
        if (cameraUpdating) {
            cameraSource.stop();
            cameraUpdating = false;
            cropView.setImageBitmap(sttextRecognizer.latestCrop);

        } else {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                cameraSource.start(cameraView.getHolder());
                cameraUpdating =true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (toSpeech != null)
        {
            toSpeech.stop();
            toSpeech.shutdown();
        }
    }
}

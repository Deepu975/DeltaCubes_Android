package com.cavox.konverz;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.app.deltacubes.R;
import com.cavox.utils.utils;

import java.io.File;

import static com.cavox.utils.GlobalVariables.LOG;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

//import com.google.android.maps.GeoPoint;


public class SoundRecorderActivity extends AppCompatActivity {
    MediaRecorder recorder = new MediaRecorder();
    Toolbar toolbar;
    private final String TAG = "SoundRecorderActivity";
    TextView timer;
    Button start;
    Button stop;
    static boolean isforeground = false;
    Button share;
    Runnable RunnableObj;
    final Handler h = new Handler();
    int delay = 1000;
    String filename;
    private boolean isRecordStarted=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.soundrecoreder_layout);


        toolbar = findViewById(R.id.toolbar);
        timer = findViewById(R.id.textView5);
        start = findViewById(R.id.button3);
        stop = findViewById(R.id.button8);
        share = findViewById(R.id.button2);

        toolbar.setTitle("Konverz");
        toolbar.setSubtitle("SoundRecorder");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        start.setEnabled(true);
        stop.setEnabled(false);
        timer.setText("00:00");


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOG.info("Yes closing soundrecorder");
                try {
                    stopRecording();
                    h.removeCallbacks(RunnableObj);

                    if (new File(filename).exists()) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("filepath", filename);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error! Record again!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "Error! Record again!!", Toast.LENGTH_SHORT).show();
                }
                ;

            }

        });


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOG.info("start soundrecorder");
                start.setEnabled(false);
                stop.setEnabled(true);
                try {
                    RunnableObj = new Runnable() {
                        int i = 0;

                        public void run() {
                            h.postDelayed(this, delay);
                            //LOG.info("printing at 1 sec");
                            SoundRecorderActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    String minutes = "00";
                                    String seconds = "00";
                                    int x = i++;
                                    int mins = (x) / 60;
                                    int sec = (x) % 60;
                                    if (mins < 10) {
                                        minutes = "0" + String.valueOf(mins);
                                    } else {
                                        minutes = String.valueOf(mins);
                                    }
                                    if (sec < 10) {
                                        seconds = "0" + String.valueOf(sec);
                                    } else {
                                        seconds = String.valueOf(sec);
                                    }

                                    timer.setText(minutes + ":" + seconds);


                                    if (mins >= 60) {
                                        if (isforeground) {
                                            stop.performClick();
                                        } else {
                                            onBackPressed();
                                        }
                                    }
                                }
                            });
                        }
                    };

                    h.postDelayed(RunnableObj, delay);
                    filename = utils.getSentImagesDirectory() + "/" + System.currentTimeMillis() + ".mp3";
                    startRecording(filename);
                    isRecordStarted=true;
                } catch (Exception ex) {
                    utils.logStacktrace(ex);
                }


            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOG.info("stop soundrecorder");
                try {
                    isRecordStarted=false;
                    start.setEnabled(true);
                    stop.setEnabled(false);
                    stopRecording();
                    h.removeCallbacks(RunnableObj);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        try {
            Intent resultIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, resultIntent);
            try {
                h.removeCallbacks(RunnableObj);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            stopRecording();
            try {
                if (new File(filename).exists()) {
                    new File(filename).delete();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            finish();
        } catch (Exception ex) {
            utils.logStacktrace(ex);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isforeground = true;
        Log.i(TAG, "onResume: Called");
        if (recorder != null&&isRecordStarted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recorder.resume();
                h.postDelayed(RunnableObj,delay);
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isforeground = false;
        Log.i(TAG, "onPause: called");
        if (recorder != null&&isRecordStarted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recorder.pause();
                h.removeCallbacks(RunnableObj);
            } else {
                stop.performClick();
            }

        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public void startRecording(String filename) {

        try {
            recorder = new MediaRecorder();

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(filename);

            //recorder.setOnErrorListener(errorListener);
            //recorder.setOnInfoListener(infoListener);


            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            utils.logStacktrace(e);
        }
    }

    private void stopRecording() {

        try {
            if (null != recorder) {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
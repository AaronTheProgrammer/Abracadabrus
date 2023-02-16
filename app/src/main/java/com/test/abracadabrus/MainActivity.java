package com.test.abracadabrus;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimationControlListener;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.squareup.picasso.Picasso;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private SpeechRecognizer speechRecognizer;
    private RecognitionListener recognitionListener;
    ActivityResultLauncher<String[]> permissionLauncher;
    private boolean recordPermissionGranted = false;
    private boolean internetPermissionGranted = false;
    private boolean modifyAudioPermission = false;
    private boolean notReadyForSpeech = true;
    private boolean alreadyListening = false;
    private boolean pictureUp = false;
    private ConstraintLayout theLayout;
    private AnimationDrawable swirlAnimation;
    private View decorView;

    private Runnable pulseRunnable = new Runnable() {
        @Override
        public void run() {
            ImageView theCircle = findViewById(R.id.theCircle);
            theCircle.setVisibility(View.VISIBLE);
            theCircle.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1000).withEndAction(new Runnable() {
                @Override
                public void run() {
                    theCircle.setScaleX(1f);
                    theCircle.setScaleY(1f);
                    theCircle.setAlpha(1f);
                    theCircle.setVisibility(View.INVISIBLE);
                }
            });
        }
    };

    public void startPulse() {
        pulseRunnable.run();
    }


    @Override
    protected void onStop() {
        super.onStop();
        try {
            AudioManager manager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
            manager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            manager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            manager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            //manager.setStreamMute(AudioManager.STREAM_ALARM, false);
            //manager.setStreamMute(AudioManager.STREAM_RING, false);
        } catch(Exception z) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            AudioManager manager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
            manager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            manager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            manager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            //manager.setStreamMute(AudioManager.STREAM_ALARM, false);
            //manager.setStreamMute(AudioManager.STREAM_RING, false);
        } catch(Exception z) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            AudioManager manager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
            manager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            manager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            manager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            //manager.setStreamMute(AudioManager.STREAM_ALARM, false);
            //manager.setStreamMute(AudioManager.STREAM_RING, false);
        } catch(Exception z) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            AudioManager manager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
            manager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            manager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            manager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            //manager.setStreamMute(AudioManager.STREAM_ALARM, true);
            //manager.setStreamMute(AudioManager.STREAM_RING, true);
        } catch(Exception z) {

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        theLayout = findViewById(R.id.theLayout);
        theLayout.setBackgroundResource(R.drawable.animation);
        swirlAnimation = (AnimationDrawable) theLayout.getBackground();
        //TextView textView = findViewById(R.id.spokenWords);
        //TextView errorView = findViewById(R.id.errors2);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if(i == 0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        try {
            AudioManager manager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
            manager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            manager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            manager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            //manager.setStreamMute(AudioManager.STREAM_ALARM, true);
            //manager.setStreamMute(AudioManager.STREAM_RING, true);
        } catch(Exception z) {

        }

//        try {
//            AudioManager manager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//            manager.setStreamMute(AudioManager.STREAM_MUSIC, false);
//            manager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
//            manager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
//            manager.setStreamMute(AudioManager.STREAM_ALARM, false);
//            manager.setStreamMute(AudioManager.STREAM_RING, false);
//        } catch(Exception z) {
//
//        }



        theLayout.post(new Runnable() {
            @Override
            public void run() {
                swirlAnimation.run();
                //This animation requires attribution, and it comes from
                //https://www.vecteezy.com/video/9702719-abstract-red-swirl-holes-background
            }
        });
//
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if(result.get(Manifest.permission.RECORD_AUDIO) != null) {
                    recordPermissionGranted = result.get(Manifest.permission.RECORD_AUDIO);
                }
                if(result.get(Manifest.permission.INTERNET) != null) {
                    internetPermissionGranted = result.get(Manifest.permission.INTERNET);
                }
                if(result.get(Manifest.permission.MODIFY_AUDIO_SETTINGS) != null) {
                    modifyAudioPermission = result.get(Manifest.permission.MODIFY_AUDIO_SETTINGS);
                }
            }
        });
        try {
            requestPermission();
        } catch(Exception e) {
            //errorView.setText(e.getMessage());
        }

        // Initialize the SpeechRecognizer and RecognitionListener
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognitionListener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // Called when the speech recognition process is ready to begin
                notReadyForSpeech = false;
            }

            @Override
            public void onBeginningOfSpeech() {
                // Called when the user begins speaking
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // Called when the volume of the spoken input changes
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // Called when the app receives a buffer of audio data

            }

            @Override
            public void onEndOfSpeech() {
                // Called when the user stops speaking
            }

            @Override
            public void onError(int error) {
                // Called when an error occurs during the speech recognition process
                if(notReadyForSpeech && error == SpeechRecognizer.ERROR_NO_MATCH) {
                    return;
                }
                alreadyListening = false;
                notReadyForSpeech = true;
            }

            @Override
            public void onResults(Bundle results) {
                // Called when the speech recognition process has completed and the results are available
                //errorView.setText("I'm here 7");
                List<String> resultList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String speechResults = "";

                if (resultList != null && resultList.size() > 0) {
                    for(int i = 0; i < resultList.size(); i++) {
                        speechResults += resultList.get(i);
                        if(i != resultList.size() - 1) {
                            speechResults += " ";
                        }
                    }
                    //textView.setText(speechResults);
                }
                alreadyListening = false;
                notReadyForSpeech = true;



                String url = "https://api.openai.com/v1/images/generations";
                String jsonSpeechResults = "{\"prompt\": \"" + speechResults + "\"}";

                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(jsonSpeechResults, MediaType.parse("application/json"));
                Request request = new Request.Builder().url(url).addHeader("Authorization", "Bearer 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").post(body).build();

                try  {
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //errorView.setText("I'm here 11");
                                    //errorView.setText(e.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            final String theResponse = response.body().string();
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //errorView.setText("I'm here 8");
                                        String one = theResponse.substring(theResponse.indexOf("https:"), theResponse.length());
                                        String pictureURL = one.substring(0, one.indexOf("\""));
                                        //errorView.setText(pictureURL);
                                        ImageView pic = findViewById(R.id.imageView3);
                                        Picasso.get().load(pictureURL).into(pic);
                                        pictureUp = true;
                                    } catch(Exception j) {
                                        //errorView.setText("I'm here 9");
                                        //errorView.setText(j.getMessage());
                                    }

                                }
                            });
                        }
                    });

                } catch (Exception g) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //errorView.setText("I'm here 10");
                            //errorView.setText(g.getMessage());
                        }
                    });
                }
            }



            @Override
            public void onPartialResults(Bundle partialResults) {
                // Called when the app receives partial results of the speech recognition process

            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // Called when the app receives other events from the speech recognition process

            }


        };

        speechRecognizer.setRecognitionListener(recognitionListener);
        for(int i = 0; i < theLayout.getChildCount(); i++) {
            theLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!alreadyListening && !pictureUp) {
                        //textView.setText("Listening");
                        notReadyForSpeech = true;
                        startPulse();
                        speechRecognizer.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
                        //errorView.setText("I'm here 5");
                        alreadyListening = true;
                    } else if(pictureUp) {
                        //errorView.setText("I'm here 6");
                        ImageView pic = findViewById(R.id.imageView3);
                        pic.setImageDrawable(null);
                        pictureUp = false;
                    }
                }
            });
        }
        theLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!alreadyListening && !pictureUp) {
                    //textView.setText("Listening");
                    notReadyForSpeech = true;
                    startPulse();
                    speechRecognizer.startListening(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));
                    //errorView.setText("I'm here 3");
                    alreadyListening = true;
                } else if(pictureUp) {
                    //errorView.setText("I'm here 4");
                    ImageView pic = findViewById(R.id.imageView3);
                    pic.setImageDrawable(null);
                    pictureUp = false;
                }
            }
        });



    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    public int hideSystemBars() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }

    public void requestPermission() {
        recordPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        internetPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
        modifyAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        List<String> permissionRequest = new ArrayList<String>();
        if(!recordPermissionGranted) {
            permissionRequest.add(Manifest.permission.RECORD_AUDIO);
        }
        if(!internetPermissionGranted) {
            permissionRequest.add(Manifest.permission.INTERNET);
        }
        if(!modifyAudioPermission) {
            permissionRequest.add(Manifest.permission.MODIFY_AUDIO_SETTINGS);
        }
        if(permissionRequest.size() != 0) {
            String[] requestArray =  new String[permissionRequest.size()];
            for(int i = 0; i < requestArray.length; i++) {
                requestArray[i] = permissionRequest.get(i);
            }
            permissionLauncher.launch(requestArray);
        }
    }

}
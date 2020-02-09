package com.example.socialnetwork;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class AnyTalk extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     */
    private static final String[] COUNTRIES = new String[]{"USA", "UK", "ITALY", "CHINA", "KOREA", "FRANCE", "JAPAN", "FRANCE"};

    private TextToSpeech textToSpeechSystem;
    Intent mSpeechRecognizerIntent;
    AutoCompleteTextView textView;


    public void selectLang()
    {
        String lan = textView.getText().toString();
        switch (lan)
        {
            case "USA":
                textToSpeechSystem.setLanguage(Locale.US);
                break;

            case "ITALY":
                textToSpeechSystem.setLanguage(Locale.ITALY);
                break;

            case "CHINA":
                textToSpeechSystem.setLanguage(Locale.CHINESE);
                break;

            case "KOREA":
                textToSpeechSystem.setLanguage(Locale.KOREA);
                break;

            case "JAPAN":
                textToSpeechSystem.setLanguage(Locale.JAPAN);
                break;

            case "UK":
                textToSpeechSystem.setLanguage(Locale.UK);
                break;

            case "FRANCE":
                textToSpeechSystem.setLanguage(Locale.UK);
                break;

             default:
                 textToSpeechSystem.setLanguage(Locale.UK);
                 break;

        }

    }

    @Override
    protected void onPause() {

        textToSpeechSystem.shutdown();
        super.onPause();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_any_talk);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
         textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteLang);
        textView.setAdapter(adapter);
        say("");



        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        initailzeSpeechRecognizer();

        
    }



    boolean flag = true;

    public void say(final String ans)
    {

        textToSpeechSystem = textToSpeechSystem = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {

                    //  Locale localeToUse = new Locale("en","US");
                //    Locale localeToUse = new Locale("hi","IN");

                      //   Locale localeToUse = new Locale(Locale.CHINA.getLanguage());
                   // textToSpeechSystem.setLanguage(localeToUse);


                      selectLang();
                    if(flag)
                    {
                        textToSpeechSystem.speak("Welcome Lord Syed", TextToSpeech.QUEUE_FLUSH, null);
                        flag=false;

                    }
                    else
                    {
                        textToSpeechSystem.speak(ans, TextToSpeech.QUEUE_FLUSH, null);

                    }
                }
            }
        });

    }

    public void speak(View view) {

      /*  Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        startActivityForResult(intent, 10);


       */
        mySpeechRecognizer.startListening(mSpeechRecognizerIntent);



    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                  //  Toast.makeText(this, result.get(0), Toast.LENGTH_SHORT).show();

                    String sp = result.get(0);
                    say(sp);
                }
                break;
        }
    }

     */


    private SpeechRecognizer mySpeechRecognizer;


    private void initailzeSpeechRecognizer() {

        // check speech recognizer is availabe

        if (SpeechRecognizer.isRecognitionAvailable(this)) {

            mySpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mySpeechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {


                }

                @Override
                public void onError(int error) {

                    mySpeechRecognizer.startListening(mSpeechRecognizerIntent);


                }

                @Override
                public void onResults(Bundle results) {

                    ArrayList<String> result = results.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                    );


                    // output string;

                    //   processResult(result.get(0));


                    String temp = result.get(0).toString().toLowerCase();
                    say(temp);

                    if (temp.contains("stop")) {

                        if (temp.contains("ok")) {

                            mySpeechRecognizer.stopListening();
                            mySpeechRecognizer.destroy();
                        }
                    }


                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });

        }


    }



}

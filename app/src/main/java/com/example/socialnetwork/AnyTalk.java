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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class AnyTalk extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     */
    private static final String[] COUNTRIES = new String[]{"HINDI", "URDU", "ARABIC", "MALAYALAM", "TAMIL", "TELUGU", "UK", "ITALY", "CHINA", "KOREA", "FRANCE", "JAPAN",};

    //Transalate
    String languagePair = "en-en";
    String textToBeTranslated = "Hello world, yeah I know it is stereotye.";

    private TextToSpeech textToSpeechSystem;
    Intent mSpeechRecognizerIntent;
    AutoCompleteTextView textView;

    Locale localeToUse = null;

    public void selectLang() {
        String lan = textView.getText().toString();
        switch (lan) {

            case "URDU":
                languagePair = "en-ur";
                localeToUse = new Locale("ur", "SA");
                textToSpeechSystem.setLanguage(localeToUse);
                break;

            case "TELUGU":
                languagePair = "en-te";
                localeToUse = new Locale("te", "IN");
                textToSpeechSystem.setLanguage(localeToUse);
                break;


            case "TAMIL":
                languagePair = "en-ta";
                localeToUse = new Locale("ta", "IN");
                textToSpeechSystem.setLanguage(localeToUse);
                break;


            case "MALAYALAM":
                languagePair = "en-ml";
                localeToUse = new Locale("ml", "IN");
                textToSpeechSystem.setLanguage(localeToUse);
                break;

            case "ARABIC":
                languagePair = "en-ar";
                localeToUse = new Locale("ar", "EG");
                textToSpeechSystem.setLanguage(localeToUse);
                break;


            case "HINDI":
                languagePair = "en-hi";
                localeToUse = new Locale("hi", "IN");
                textToSpeechSystem.setLanguage(localeToUse);
                break;

            case "ITALY":
                languagePair = "en-it";
                textToSpeechSystem.setLanguage(Locale.ITALY);
                break;

            case "CHINA":
                languagePair = "en-zh";
                textToSpeechSystem.setLanguage(Locale.CHINESE);
                break;

            case "KOREA":
                textToSpeechSystem.setLanguage(Locale.KOREA);
                break;

            case "JAPAN":
                languagePair = "en-ja";
                textToSpeechSystem.setLanguage(Locale.JAPAN);
                break;

            case "UK":
                textToSpeechSystem.setLanguage(Locale.UK);
                break;

            case "FRANCE":
                languagePair = "en-fr";
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


        //Default variables for translation

        //English to French ("<source_language>-<target_language>")
        //Executing the translation function


    }

    static String output = "";

    // Transalate

    //Function for calling executing the Translator Background Task
    void Translate(String textToBeTranslated, String languagePair) {
        TranslatorBackgroundTask translatorBackgroundTask = new TranslatorBackgroundTask(this);

        String output = null; // Returns the translated text as a String
        try {
            output = translatorBackgroundTask.execute(textToBeTranslated, languagePair).get();
            //Getting the characters between [ and ]
            output = output.substring(output.indexOf('[') + 1);
            output = output.substring(0, output.indexOf("]"));
            //Getting the characters between " and "
            output = output.substring(output.indexOf("\"") + 1);
            output = output.substring(0, output.indexOf("\""));
            say(output);


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    boolean flag = true;

    public void say(final String ans) {

        textToSpeechSystem = textToSpeechSystem = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {

                    //  Locale localeToUse = new Locale("en","US");
                    // textToSpeechSystem.setLanguage(localeToUse);

                    Toast.makeText(AnyTalk.this, ans, Toast.LENGTH_SHORT).show();


                    selectLang();
                    if (flag) {
                        textToSpeechSystem.speak("Welcome Lord Syed", TextToSpeech.QUEUE_FLUSH, null);
                        flag = false;

                    } else {
                        textToSpeechSystem.speak(ans, TextToSpeech.QUEUE_FLUSH, null);

                    }
                }
            }
        });

    }

    public void speak(View view) {


        mySpeechRecognizer.startListening(mSpeechRecognizerIntent);


    }

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

                    mySpeechRecognizer.startListening(mSpeechRecognizerIntent);

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
                    textToBeTranslated = temp;
                    Translate(textToBeTranslated, languagePair);


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

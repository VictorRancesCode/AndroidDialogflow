package com.codigopanda.androiddialogflow;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public class clsPrincipal extends AppCompatActivity implements AIListener, View.OnClickListener {
    private ImageButton btnVoice;
    private ImageButton sendmessage;
    private AIService aiService;
    private static final int REQUEST_INTERNET = 200;


    TextToSpeech t1;

    public static String TAG = "AndroidDialogflow";

    List<Message> lista;
    ListView milista;
    EditText message;
    AIConfiguration config;


    public static String API_KEY="YOUR API KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fprincipal);
        btnVoice = (ImageButton) findViewById(R.id.sendspeacker);
        lista = new ArrayList<>();

        validateOS();

        config = new AIConfiguration(API_KEY,
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        milista = (ListView) findViewById(R.id.chat);
        sendmessage = (ImageButton) findViewById(R.id.sendmessage);
        sendmessage.setOnClickListener(this);

        btnVoice.setOnClickListener(this);
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Locale locSpanish = new Locale("spa", "MEX");
                    t1.setLanguage(locSpanish);
                }
            }
        });

        message = (EditText) findViewById(R.id.textmessage);


    }

    private void validateOS() {
        if (ContextCompat.checkSelfPermission(clsPrincipal.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(clsPrincipal.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_INTERNET);
            ActivityCompat.requestPermissions(clsPrincipal.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_INTERNET);
        }
    }

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();
        final Status status = response.getStatus();
        Log.i(TAG, "Status code: " + status.getCode());
        Log.i(TAG, "Status type: " + status.getErrorType());
        final Metadata metadata = result.getMetadata();
        if (metadata != null) {
            Log.i(TAG, "Intent id: " + metadata.getIntentId());
            Log.i(TAG, "Intent name: " + metadata.getIntentName());
        }
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue();
            }
        }
        String cad = response.getResult().getFulfillment().getSpeech().toString();
        if (cad.compareTo("") == 0) {
            cad = "Algunos Datos salieron con error";
        }
        // text to speech get text response
        t1.speak(cad, TextToSpeech.QUEUE_FLUSH, null);
        lista.add(new Message(result.getResolvedQuery(), 1));
        lista.add(new Message(cad, 0));
        AdapMessage adap = new AdapMessage(lista, this);
        milista.setAdapter(adap);
    }

    @Override
    public void onError(AIError error) {
        Log.e(TAG,error.toString());
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendspeacker:
                aiService.startListening();
                break;
            case R.id.sendmessage:
                SendText(message.getText().toString());
                break;
        }
    }

    void SendText(String query) {
        final AIConfiguration config = new AIConfiguration(API_KEY,
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);
        final AIDataService aiDataService = new AIDataService(this,config);
        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(query);

        new AsyncTask<AIRequest, Void, AIResponse>() {
            private final ProgressDialog dialog = new ProgressDialog(clsPrincipal.this);
            protected void onPreExecute() {
                this.dialog.setMessage("Enviando Mensaje...");
                this.dialog.show();
                super.onPreExecute();
            }

            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                final AIRequest request = requests[0];
                try {
                    final AIResponse response = aiDataService.request(aiRequest);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }
            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                if (aiResponse != null) {
                    // process aiResponse here
                    Result result = aiResponse.getResult();
                    String cad = aiResponse.getResult().getFulfillment().getSpeech().toString();
                    if (cad.compareTo("") == 0) {
                        cad = "Algunos Datos salieron con error";
                    }
                    t1.speak(cad, TextToSpeech.QUEUE_FLUSH, null);
                    lista.add(new Message(result.getResolvedQuery(), 1));
                    lista.add(new Message(cad, 0));
                    AdapMessage adap = new AdapMessage(lista, clsPrincipal.this);
                    milista.setAdapter(adap);
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    message.setText("");

                }
            }
        }.execute(aiRequest);
    }
}

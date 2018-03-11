package com.bvisible.carnet;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bvisible.carnet.controllers.BluetoothController;
import com.bvisible.carnet.controllers.NearSitesController;
import com.bvisible.carnet.controllers.TextToSpeechController;
import com.bvisible.carnet.utils.Constants;

public class MainFragment extends Fragment implements AsyncResponse {
    public static final String TAG = MainFragment.class.getSimpleName();
    private View rootView;

    private AsyncResponse asyncResponse;
    private String messageVoice;
    private TextView textResult;
    private Button buttonSendVibrate;
    private Button buttonSimulateShock;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_fragment, container, false);

        asyncResponse = this;

        setUpElements();
        setUpListeners();

        return rootView;
    }

    private void setUpElements() {
        textResult = rootView.findViewById(R.id.main_fragment_result_text);
        buttonSendVibrate = rootView.findViewById(R.id.main_fragment_send_notification);
        buttonSimulateShock = rootView.findViewById(R.id.main_fragment_simulate_shock);
    }

    private void setUpListeners() {
        buttonSendVibrate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BluetoothController.getInstance(getContext()).sendData(BluetoothController.SEND_PALO_VIBRATION, "2");
            }
        });

        buttonSimulateShock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((MainActivity) getActivity()).audioRecognison();
            }
        });
    }

    public void newVoiceMessage(String message) {
        Log.e(TAG, message);

        this.messageVoice = message;
        NearSitesController.getInstance().queryTPGraph(Constants.LAT_PALAU, Constants.LNG_PALAU, asyncResponse);
    }

    @Override
    public void processFinish(String typeAsync) {
        Log.e(TAG, typeAsync);
        String routeName = "33";
        if (typeAsync.equals("STOPS")) {
            if (messageVoice != null) {
                String hour = NearSitesController.getInstance().getAsyncTaskTP().timeToRoute(Constants.LAT_PALAU, Constants.LNG_PALAU, routeName);
                String textToSpeech;
                if (hour != null && !hour.equals("")) {
                    textToSpeech = "Bus " + routeName + " arrives at " + hour;
                } else {
                    //textToSpeech = "No bus is arriving";
                    textToSpeech = "Bus expeted in a few minutes";
                }
                Log.e(TAG, "TextToSpeech: " + textToSpeech);
                TextToSpeechController.getInstance(getContext()).speak(textToSpeech, TextToSpeech.QUEUE_FLUSH);
                textResult.setText(textToSpeech);
            }
        }
    }
}

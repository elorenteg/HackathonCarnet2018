package com.bvisible.carnet;

import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bvisible.carnet.controllers.NearSitesController;
import com.bvisible.carnet.controllers.TextToSpeechController;

public class MainFragment extends Fragment implements AsyncResponse {
    public static final String TAG = MainFragment.class.getSimpleName();
    private View rootView;

    private AsyncResponse asyncResponse;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.main_fragment, container, false);

        setUpElements();
        setUpListeners();

        asyncResponse = this;

        return rootView;
    }

    private void setUpElements() {
    }

    private void setUpListeners() {

    }

    public void newVoiceMessage(String message) {
        Log.e(TAG, message);

        if (((MainActivity) getActivity()).existsLastLocation()) {
            Location location = ((MainActivity) getActivity()).getLastLocation();
            //NearSitesController.getInstance().queryTPGraph(location.getLatitude(), location.getLongitude(), asyncResponse);
        }
    }

    @Override
    public void processFinish(String typeAsync) {
        boolean update = false;
        double lat = -1;
        double lng = -1;
        if (typeAsync.equals("BIKES")) {
            update = true;
            lat = NearSitesController.getInstance().getLatitude();
            lng = NearSitesController.getInstance().getLongitude();
        }
        Log.e(TAG, typeAsync);

        if (update) {
            TextToSpeechController.getInstance(getContext()).speak("Hola", TextToSpeech.QUEUE_FLUSH);
        }
    }
}
